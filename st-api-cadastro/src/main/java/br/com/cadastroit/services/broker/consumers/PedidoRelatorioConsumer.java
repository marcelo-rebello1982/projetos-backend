package br.com.cadastroit.services.broker.consumers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.aws.AwsBucketUploadModel;
import br.com.cadastroit.services.broker.consumers.model.RelatorioMessage;
import br.com.cadastroit.services.exceptions.RelatorioException;
import br.com.cadastroit.services.mongodb.domain.CollectionRelatorioApi;
import br.com.cadastroit.services.mongodb.repository.impl.CollectionRelatorioApiRepository;
import br.com.cadastroit.services.rabbitmq.connectors.RabbitMQConnection;
import br.com.cadastroit.services.repositories.DepartamentoRepository;
import br.com.cadastroit.services.repositories.PedidoRepository;
import br.com.cadastroit.services.utils.UtilDate;
import br.com.cadastroit.services.web.dto.PedidoDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PedidoRelatorioConsumer {

	private Gson gson = new Gson();
	private EntityManagerFactory entityManagerFactory;
	private PedidoRepository pedidoRepository;
	private CollectionRelatorioApiRepository collectionRelatorioApiRepository;

	// @RabbitListener(queues = "RELATORIO_PEDIDO", concurrency = "1")
	private void consultaFilaSolicitaRel(Message msg) throws Exception {

		try {
			this.updateCollectionRelatorioApi(this.processaRelatorio(gson.fromJson(new String(msg.getBody()), PedidoDTO.class)));
		} catch (Exception ex) {
			throw new RelatorioException(String.format("ERROR PROCESSING THE REQUEST - 1" + ex.getMessage()));
		}
	}

	public AwsBucketUploadModel processaRelatorio(PedidoDTO dto) throws NoSuchAlgorithmException, IOException, URISyntaxException {

		int page = 1; // dto.getPage();
		int lenght = 100; // dto.getLenght();
		String sufix = "planilha_xlsx";
		String data = UtilDate.toString(new Date(), "dd/MM/yyyy");
		String prefix = sufix + File.separator + data + File.separator + "rel";
		String bucketName = System.getenv("BUCKET");
		Long pessoaId = dto.getPessoa().getId();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		List<Pedido> pedidos = this.pedidoRepository.findByFilters(pessoaId, dto, entityManagerFactory, dto.getRequestParams(), page, lenght);

		// instanciar a cada novo processamento devido aba workbench
		// ExcelCreatorService excelCreatorService = new ExcelCreatorService(list);
		// AwsBucketUploadModel awsBucketUploadModel = this.uploadFileTos3Bucket(bucketName, prefix,
		// excelCreatorService.mountData(list, empresaId, currentDateTime).getAbsoluteFile(),
		// empresaId, entityDto.getNroProtocolo());

		AwsBucketUploadModel awsBucketUploadModel = new AwsBucketUploadModel();
		return awsBucketUploadModel;
	}

	public void updateCollectionRelatorioApi(AwsBucketUploadModel awsBucketUploadModel) throws Exception {

		try {

			if (awsBucketUploadModel != null && awsBucketUploadModel.getPessoaId() != null) {

				GsonBuilder builder = new GsonBuilder();
				builder.setLenient();
				Gson gson = builder.create();

				CollectionRelatorioApi requestObject = gson.fromJson(gson.toJson(awsBucketUploadModel), CollectionRelatorioApi.class);
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

				this.collectionRelatorioApiRepository.updateCallRelatorioApi(requestObject, requestObject.getPESSOA_ID(),
						requestObject.getNROPROTOCOLO());
			}

		} catch (Exception ex) {
			throw new Exception(String.format("ERROR UPDATE DOCUMENT IN MONGODB - 2" + ex.getMessage()));
		} finally {
			try {
				this.sendMessageToRabbit(awsBucketUploadModel);
			} catch (Exception ex) {
				throw new Exception(String.format("ERROR SENDING PROCESSING RETURN FOR MESSAGING" + ex.getMessage()));
			}
		}
	}

	public void sendMessageToRabbit(AwsBucketUploadModel entity) {

		try {

			String connections[] = new String[] { System.getenv("connections") };
			RelatorioMessage msg = RelatorioMessage.builder()
					.fileDirectory(entity.getAwsS3RabbitMessage().getPrefix())
					.fileName(entity.getAwsS3RabbitMessage().getFileName())
					.uuid(entity.getAwsS3RabbitMessage().getUuid())
					.urlTempBucket(entity.getAwsS3RabbitMessage().getUrlTempBucket())
					.nroProtocolo(String.valueOf(entity.getAwsS3RabbitMessage().getNroProtocolo()))
					.build();

			RabbitMQConnection rabbitMQConnection = RabbitMQConnection.builder().connections(connections).build();

			rabbitMQConnection.buildConnectionFactory();
			rabbitMQConnection.sendTextMessage("REL_DEPARTAMENTO", "RELATORIO-DEPARTAMENTO", "rk_retorno-processamento-rel", this.convertToJson(msg));

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	public String convertToJson(Object data) throws JsonProcessingException {

		return new Gson().toJson(data);
	}
}
