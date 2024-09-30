package br.com.cadastroit.services.api.broker.configuration;

import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

import br.com.cadastroit.services.desif.rabbit.CstRelatoriosDefault;
import br.com.cadastroit.services.enums.CstMensageiroMessagesEnum;
import br.com.cadastroit.services.rabbitmq.connectors.RabbitMQConnection;
import br.com.cadastroit.services.rabbitmq.converters.ConverterMessage;
import br.com.cadastroit.services.rabbitmq.model.RabbitDomain;
import br.com.cadastroit.services.rabbitmq.util.RabbitCFGClient;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class RabbitBeanConfiguration implements CstRelatoriosDefault {

	private RabbitMQConnection rabbitConnection;
	private RabbitDomain rabbitGetDomain;
	private final String infoSetupMensageiro = CstMensageiroMessagesEnum.INFO_SETUP_QUEUE_MENSAGEIRO.infoMensageiro();

	public RabbitDomain createQueues() throws Exception {
		
		 Map<String , String> environments = RabbitCFGClient.environments();

		if (this.rabbitGetDomain == null) {
			this.getConnection();
			this.rabbitGetDomain = this.rabbitConnection.rabbitDomain(environments.get("connectionId"));
			this.createQueues(rabbitConnection, environments.get("connectionId"), infoSetupMensageiro, log);
		}
		return this.rabbitGetDomain;
	}
	
	public RabbitMQConnection getConnection() throws Exception {

		if (this.rabbitConnection == null) {
			this.rabbitConnection = RabbitMQConnection.builder().build();
			this.rabbitConnection.buildConnectionFactory();
		}
		return this.rabbitConnection;
	}
	
	public static Message buildMessageSchedule(int time, String msg) throws Exception {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader(MessageProperties.X_DELAY, time);
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		byte[] body = new ConverterMessage().convertToBytes(msg);
		return new Message(body, messageProperties);
	}
}
