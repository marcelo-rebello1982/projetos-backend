package br.com.cadastroit.services.broker.configuration;

import java.sql.Connection;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

import br.com.cadastroit.services.rabbitmq.converters.ConverterMessage;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class RabbitBeanRelatorioIntegra  {
	
	public static final String PADRAO = "2";
	public static final String JAR_PREFIX = "ginfes";
	public static String activeCompanies = null;
	public static String SPRING_TIME_CONSULT = null;
	public static int recoveryTime = 5000;
	public static boolean STARTED_PROCESS = false;
	protected Connection oracleConnection;


	public static Message buildMessageSchedule(int time, String msg) throws Exception {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader(MessageProperties.X_DELAY, time);
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		byte[] body = new ConverterMessage().convertToBytes(msg);
		return new Message(body, messageProperties);
	}
}
