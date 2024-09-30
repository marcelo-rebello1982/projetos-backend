package br.com.cadastroit.services.broker.configuration;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

@Component
public class BrokerUtilities {

	public static Message buildMessageSchedule(int time, String msg) throws Exception {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader(MessageProperties.X_DELAY, time);
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		byte[] body = new ConverterMessage().convertToBytes(msg);
//		byte[] body = new ConverterMessage.ConverterBuilder().convertToBytes(msg).build().getMessageBytes();
		return new Message(body, messageProperties);
	}
}
