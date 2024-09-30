package br.com.cadastroit.services.broker.consumers;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.cadastroit.services.api.broker.configuration.RabbitBeanConfiguration;
import br.com.cadastroit.services.api.broker.consumers.model.RelatorioMessage;
import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.aws.AwsBucketUploadModel;
import br.com.cadastroit.services.common.aws.AwsS3RabbitMessage;
import br.com.cadastroit.services.converters.ConverterMessage;
import br.com.cadastroit.services.exceptions.PedidoException;
import br.com.cadastroit.services.mongodb.domain.CollectionRelatorioApi;
import br.com.cadastroit.services.mongodb.repository.impl.CollectionRelatorioApiRepository;
import br.com.cadastroit.services.repositories.ProcessaPedidoRepository;
import br.com.cadastroit.services.utils.UtilDate;
import br.com.cadastroit.services.web.dto.PedidoDTO;
import br.com.cadastroit.services.xlsx.ExcelRelCreator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ProcessaPedidoConsumer {

	private ProcessaPedidoRepository processaPedidoRepository;
	private EntityManagerFactory entityManagerFactory;
	private final Logger logger = Logger.getLogger(ProcessaPedidoConsumer.class);
	private CollectionRelatorioApiRepository  collectionRelatorioApiRepository;
	private final ObjectMapper mapperJson = new ObjectMapper();
	

	@RabbitListener(queues = "PEDIDO-APROVADO", concurrency = "1")
	private void consultaFilaPedidoAprovado(Message message) throws Exception {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		try {
			this.processarPedidoAprovado(message, entityManager);
		} catch (Exception ex) {
			throw new PedidoException(String.format("ERROR PROCESSING THE REQUEST - 1" + ex.getMessage()));
		} finally {
			entityManager.clear();
			entityManager.close();
		}
	}
	
	private void processarPedidoAprovado(Message message, EntityManager entityManager) {

		try {
			
			PedidoDTO dto = mapperJson.convertValue(convertObject(message), PedidoDTO.class);
			this.updateCollectionRelatorioApi(this.processaRelatorio(dto));

		} catch (Exception ex) {
			throw new PedidoException(String.format("ERROR PROCESSING THE REQUEST - 2" + ex.getMessage()));
		}
	}
	
	public AwsBucketUploadModel processaRelatorio(PedidoDTO entityDto)
			throws NoSuchAlgorithmException, IOException, URISyntaxException {

		int page = 1; //entityDto.getPage();
		int lenght = 10; //entityDto.getLenght();
		String sufix = "planilha_xlsx";
		String data = UtilDate.toDateTimeStringTextPlain(Timestamp.from(Instant.now()));
		String prefix = sufix + File.separator + data + File.separator + "rel";
		String bucketName = "RELATORIO_S3"; // System.getenv("BUCKET");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH");
		String currentDateTime = dateFormatter.format(new Date());
		List<Pedido> list = this.processaPedidoRepository.findByFilters(entityDto.getPessoaId(), entityDto, entityManagerFactory,
																	entityDto.getRequestParams(), page, lenght);

		ExcelRelCreator excelCreatorService = new ExcelRelCreator(list);
		AwsBucketUploadModel awsBucketUploadModel = this.uploadFileTos3Bucket(bucketName, prefix,
															excelCreatorService.writeData(list, entityDto.getPessoaId(), 
																	currentDateTime).getAbsoluteFile(), 
																		entityDto.getPessoaId(), 
																			entityDto.getNroProtocolo());
		
		return awsBucketUploadModel;
	}

	public void updateCollectionRelatorioApi(AwsBucketUploadModel awsBucketUploadModel)
			throws PedidoException {

		try {

			GsonBuilder builder = new GsonBuilder();
			builder.setLenient();
			Gson gson = builder.create();

			CollectionRelatorioApi requestObject = gson.fromJson(gson.toJson(awsBucketUploadModel),
					CollectionRelatorioApi.class);
					requestObject.setID(awsBucketUploadModel.getUuid().toString());
					requestObject.setSTATUS(awsBucketUploadModel.getCode());
					requestObject.setPESSOA_ID(awsBucketUploadModel.getPessoaId());
					requestObject.setDESCRIPTION(awsBucketUploadModel.getPrefix());
					requestObject.setMESSAGES(awsBucketUploadModel.getTextCode());
					requestObject.setURLS3(awsBucketUploadModel.getUrlTempBucket());
					requestObject.setTYPEARCHIVE(FilenameUtils.getExtension(awsBucketUploadModel.getFileName()));
					requestObject.setNAMEARCHIVE(awsBucketUploadModel.getFileName());
					requestObject.setCREATIONDATE(awsBucketUploadModel.getCreationDate());
					requestObject.setNROPROTOCOLO(awsBucketUploadModel.getNroProtocolo());
					requestObject.setREFERENCE(awsBucketUploadModel.getReference());

			this.collectionRelatorioApiRepository.updateCallRelatorioApi(
						requestObject, 
							requestObject.getPESSOA_ID(),
								requestObject.getNROPROTOCOLO());

		} catch (Exception ex) {
			throw new PedidoException(String.format("ERROR UPDATE DOCUMENT IN MONGODB - 2" + ex.getMessage()));
		} finally {
			logger.info("FINALIZING REGISTRATION UPDATE ON MONGO DB..");
			try {
				this.sendMsgToRabbitMQ(awsBucketUploadModel);
			} catch (Exception ex) {
				throw new PedidoException(
						String.format("ERROR SENDING PROCESSING RETURN FOR MESSAGING" + ex.getMessage()));
			}
		}
	}

	public AwsBucketUploadModel uploadFileTos3Bucket(String bucketName, String prefix, File file, Long pessoaId,
			Long nroProtocolo) throws NoSuchAlgorithmException, IOException, URISyntaxException {

		BasicFileAttributes fileAtt = Files.readAttributes((Path) file.toPath(), BasicFileAttributes.class);

		// AwsTempBucketClient awsTempBucketClient = new AwsTempBucketClient();
		//										  	awsTempBucketClient.setObjectKey(file.getName());
		//										  	awsTempBucketClient.initializeAmazon(bucketName);
		//										  	awsTempBucketClient.createTempBucket(bucketName);
		//										  	awsTempBucketClient.uploadFileTos3Bucket(bucketName, file);

		AwsS3RabbitMessage awsS3RabbitMessage = new AwsS3RabbitMessage();
													awsS3RabbitMessage.setFile(FileUtils.readFileToByteArray(file));
													awsS3RabbitMessage.setFileName("nomedaplanilhagerada.xlsx");
													//awsS3RabbitMessage.setFileName(awsTempBucketClient.getObjectKey());
													awsS3RabbitMessage.setId(pessoaId);
													awsS3RabbitMessage.setPrefix(prefix + File.separator + pessoaId + File.separator);
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
													.fileName("nomedaplanilhagerada.xlsx")
													.pessoaId(pessoaId)
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
		
		RabbitTemplate rabbitTemplate = new RabbitTemplate();

		try {

			RelatorioMessage message = RelatorioMessage.builder()
													.fileDirectory(entity.getAwsS3RabbitMessage().getPrefix())
													.fileName(entity.getAwsS3RabbitMessage().getFileName())
													.uuid(entity.getAwsS3RabbitMessage().getUuid())
													.urlTempBucket(entity.getAwsS3RabbitMessage().getUrlTempBucket())
													.nroProtocolo(entity.getAwsS3RabbitMessage().getNroProtocolo())
													.build();
			
			rabbitTemplate.convertAndSend(RabbitBeanConfiguration.exchangePedidoAprovado, RabbitBeanConfiguration.rkPedidoAprovado,
					ConverterMessage.builder().build().convertToBytes(message).getMessageBytes());


		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
	
	protected String converterCampoStatus(Long value) {
		Map<Integer, String> status = new HashMap<>();
		status.put(0, "0-Não validado");
		status.put(1, "1-Validado");
		status.put(2, "2-Erro de validação");
		return status.entrySet().stream().filter(p -> p.getKey().equals(value)).findFirst().get().getValue();
	}

	private RelatorioMessage convertObject(Message message) throws IOException, ClassNotFoundException {

		return (RelatorioMessage) ConverterMessage.builder().build().convertFromBytes(message.getBody()).getData();
	}

	public String convertToJson(Object data) throws JsonProcessingException {
		Gson gson = new Gson();
		return gson.toJson(data);
	}
}
