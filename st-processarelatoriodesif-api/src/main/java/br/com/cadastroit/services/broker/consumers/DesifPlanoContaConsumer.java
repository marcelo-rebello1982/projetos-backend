package br.com.cadastroit.services.broker.consumers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.cadastroit.services.api.domain.DesifPlanoConta;
import br.com.cadastroit.services.api.services.DesifPlanoContaService;
import br.com.cadastroit.services.api.services.ExcelCreatorService;
import br.com.cadastroit.services.aws.AwsBucketUploadModel;
import br.com.cadastroit.services.aws.AwsTempBucketClient;
import br.com.cadastroit.services.broker.consumers.model.DesifPlanoContaMessage;
import br.com.cadastroit.services.common.aws.AwsS3RabbitMessage;
import br.com.cadastroit.services.common.util.UtilDate;
import br.com.cadastroit.services.exceptions.DesifPlanoContaException;
import br.com.cadastroit.services.mongodb.domain.CallRelatorioDesifApi;
import br.com.cadastroit.services.repository.impl.CallRelatorioDesifApiRepository;
import br.com.cadastroit.services.web.dto.DesifPlanoContaDto;
import br.com.complianceit.rabbitmq.connectors.RabbitMQConnection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DesifPlanoContaConsumer {

	private Gson gson = new Gson();
	private DesifPlanoContaService service;
	private EntityManagerFactory entityManagerFactory;
	private final Logger logger = Logger.getLogger(DesifPlanoContaConsumer.class);
	private CallRelatorioDesifApiRepository callRelatorioDesifRepository;

	@RabbitListener(queues = "RELATORIO-DESIF-SOLICITA-REL", concurrency = "1")
	private void consultaFilaSolicitaRel(Message msg) throws Exception {

		logger.info("STARTING RETURN PROCESSING OF THE REPORT, CONSULTING THE REQUEST QUEUE ..");

		try {
			this.updateCallRelatorioDesifApi(
					this.processaRelatorio(gson.fromJson(new String(msg.getBody()), DesifPlanoContaDto.class)));
		} catch (Exception ex) {
			logger.error("ERROR ON METHOD consultaFila - ERROR [" + ex.getMessage() + "]");
			throw new DesifPlanoContaException(String.format("ERROR PROCESSING THE REQUEST - 1" + ex.getMessage()));
		} finally {
			logger.info("FINALIZING RETURN OF THE REPORT PROCESSING..");
		}
	}

	public void updateCallRelatorioDesifApi(AwsBucketUploadModel awsBucketUploadModel)
			throws DesifPlanoContaException {

		try {

			GsonBuilder builder = new GsonBuilder();
			builder.setLenient();
			Gson gson = builder.create();

			CallRelatorioDesifApi requestObject = gson.fromJson(gson.toJson(awsBucketUploadModel),
					CallRelatorioDesifApi.class);
					requestObject.setId(awsBucketUploadModel.getUuid().toString());
					requestObject.setSTATUS(awsBucketUploadModel.getCode());
					requestObject.setEMPRESA_ID(awsBucketUploadModel.getEmpresaId());
					requestObject.setDESCRIPTION(awsBucketUploadModel.getPrefix());
					requestObject.setMESSAGES(awsBucketUploadModel.getTextCode());
					requestObject.setURLS3(awsBucketUploadModel.getUrlTempBucket());
					requestObject.setTYPEARCHIVE(FilenameUtils.getExtension(awsBucketUploadModel.getFileName()));
					requestObject.setNAMEARCHIVE(awsBucketUploadModel.getFileName());
					requestObject.setCREATIONDATE(awsBucketUploadModel.getCreationDate());
					requestObject.setNROPROTOCOLO(awsBucketUploadModel.getNroProtocolo());
					requestObject.setREFERENCE(awsBucketUploadModel.getReference());

			this.callRelatorioDesifRepository.updateCallRelatorioDesifApi(
						requestObject, 
							requestObject.getEMPRESA_ID(),
								requestObject.getNROPROTOCOLO());

		} catch (Exception ex) {
			logger.error("ERROR ON METHOD updateCallRelatorioDesifApiStatus [" + ex.getMessage() + "]");
			throw new DesifPlanoContaException(String.format("ERROR UPDATE DOCUMENT IN MONGODB - 2" + ex.getMessage()));
		} finally {
			logger.info("FINALIZING REGISTRATION UPDATE ON MONGO DB..");
			try {
				this.sendMsgToRabbitMQ(awsBucketUploadModel);
			} catch (Exception ex) {
				logger.error("ERROR ON METHOD sendMsgToRabbitMQ - ERROR [" + ex.getMessage() + "]");
				throw new DesifPlanoContaException(
						String.format("ERROR SENDING PROCESSING RETURN FOR MESSAGING" + ex.getMessage()));
			}
		}
	}

	public AwsBucketUploadModel processaRelatorio(DesifPlanoContaDto entityDto)
			throws NoSuchAlgorithmException, IOException, URISyntaxException {

		int page = entityDto.getPage();
		int lenght = entityDto.getLenght();
		String sufix = "planilha_xlsx";
		String data = UtilDate.toString(new Date(), "dd/MM/yyyy");
		String prefix = sufix + File.separator + data + File.separator + "rel";
		String bucketName = System.getenv("BUCKET");
		Long empresaId = entityDto.getEmpresa().getId();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		List<DesifPlanoConta> list = this.service.findByFilters(empresaId, entityDto, entityManagerFactory,
																	entityDto.getRequestParams(), page, lenght);

		// instanciar a cada novo processamento devido aba workbench
		ExcelCreatorService excelCreatorService = new ExcelCreatorService(list);
		AwsBucketUploadModel awsBucketUploadModel = this.uploadFileTos3Bucket(bucketName, prefix,
															excelCreatorService.mountData(list, empresaId, currentDateTime).getAbsoluteFile(), 
		                                             			empresaId, entityDto.getNroProtocolo());
		return awsBucketUploadModel;
	}

	public AwsBucketUploadModel uploadFileTos3Bucket(String bucketName, String prefix, File file, Long empresaId,
			Long nroProtocolo) throws NoSuchAlgorithmException, IOException, URISyntaxException {

		BasicFileAttributes fileAtt = Files.readAttributes((Path) file.toPath(), BasicFileAttributes.class);

		// AwsTempBucketClient awsTempBucketClient = new AwsTempBucketClient();
		//										  	awsTempBucketClient.setObjectKey(file.getName());
		//										  	awsTempBucketClient.initializeAmazon(bucketName);
		//										  	awsTempBucketClient.createTempBucket(bucketName);
		//										  	awsTempBucketClient.uploadFileTos3Bucket(bucketName, file);

		AwsS3RabbitMessage awsS3RabbitMessage = new AwsS3RabbitMessage();
													awsS3RabbitMessage.setFile(FileUtils.readFileToByteArray(file));
													awsS3RabbitMessage.setFileName("nomedaplanilhagerada");
													//awsS3RabbitMessage.setFileName(awsTempBucketClient.getObjectKey());
													awsS3RabbitMessage.setId(empresaId);
													awsS3RabbitMessage.setPrefix(prefix + File.separator + empresaId + File.separator);
													// awsS3RabbitMessage.setUrlTempBucket(awsTempBucketClient.getTempURL());
													awsS3RabbitMessage.setUrlTempBucket("https://buckets.s3.amazonaws.com/files/relatorios/planilhaxxx.xlsx");
													awsS3RabbitMessage.setUuid(UUID.randomUUID());
													
													SimpleDateFormat dtCreation = new SimpleDateFormat("dd/MM/yyyy");
													awsS3RabbitMessage.setCreationDate(dtCreation.format(fileAtt.creationTime().toMillis()));
													awsS3RabbitMessage.setNroProtocolo(nroProtocolo);

		HashMap<String, String> reference = new HashMap<>();
		reference.put(String.valueOf(awsS3RabbitMessage.getId()), 
													awsS3RabbitMessage.getCreationDate());
													awsS3RabbitMessage.setReference(reference);

		AwsBucketUploadModel awsBucketUploadModel = AwsBucketUploadModel.builder()
													.file(file)
													//.fileName(awsTempBucketClient.getObjectKey())
													.fileName("nomedaplanilhagerada")
													.empresaId(empresaId)
													.prefix(awsS3RabbitMessage.getPrefix())
													.urlTempBucket(awsS3RabbitMessage.getUrlTempBucket())
													.uuid(awsS3RabbitMessage.getUuid())
													.creationDate(awsS3RabbitMessage.getCreationDate())
													.nroProtocolo(String.valueOf(awsS3RabbitMessage
													.getNroProtocolo()))
													.code(file.exists() ? 0 : 1)
													.reference(awsS3RabbitMessage.getReference())
													.awsS3RabbitMessage(awsS3RabbitMessage)
													.build();
		
		Files.delete(Paths.get(file.getName()));
		return awsBucketUploadModel;
	}

	public void sendMsgToRabbitMQ(AwsBucketUploadModel entity) {

		try {

			String connections[] = new String[] { System.getenv("connections") };
			DesifPlanoContaMessage msg = DesifPlanoContaMessage.builder()
													.fileDirectory(entity.getAwsS3RabbitMessage().getPrefix())
													.fileName(entity.getAwsS3RabbitMessage().getFileName())
													.uuid(entity.getAwsS3RabbitMessage().getUuid())
													.urlTempBucket(entity.getAwsS3RabbitMessage().getUrlTempBucket())
													.nroProtocolo(String.valueOf(entity.getAwsS3RabbitMessage().getNroProtocolo()))
													.build();
			
			RabbitMQConnection rabbitMQConnection = RabbitMQConnection.builder()
													.connections(connections)
													.build();

			rabbitMQConnection.buildConnectionFactory();
			rabbitMQConnection.sendTextMessage("REL_DESIF", "RELATORIO-DESIF", "rk_retorno-processamento-rel", this.convertToJson(msg));

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	public String convertToJson(Object data) throws JsonProcessingException {
		Gson gson = new Gson();
		return gson.toJson(data);
	}
}
