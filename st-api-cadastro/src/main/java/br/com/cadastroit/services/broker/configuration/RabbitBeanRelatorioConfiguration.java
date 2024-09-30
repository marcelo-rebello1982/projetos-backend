package br.com.cadastroit.services.broker.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.cadastroit.services.OsDetect;
import br.com.cadastroit.services.api.soap.utils.CommonsSchedules;
import br.com.cadastroit.services.rabbitmq.connectors.RabbitMQConnection;
import br.com.cadastroit.services.rabbitmq.model.RabbitDomain;
import br.com.cadastroit.services.utils.UtilString;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class RabbitBeanRelatorioConfiguration {

	private Logger log = Logger.getLogger(RabbitBeanRelatorioConfiguration.class);

	private String HOST_FILE = "/opt/st-rabbit-config/nosql-processarelatorio.properties";

	private RabbitMQConnection rabbitMQConnection;
	private RabbitDomain rabbitDomain;
	private final String[] data = getConnectionInfo();

	public RabbitMQConnection initializer() throws Exception {

		if (this.rabbitMQConnection == null) {
			this.rabbitMQConnection = RabbitMQConnection.builder().build();
			this.rabbitMQConnection.buildConnectionFactory();
		}
		return this.rabbitMQConnection;
	}

	public RabbitDomain createDomains() throws Exception {

		if (this.rabbitDomain == null) {
			this.initializer();
			this.rabbitDomain = this.rabbitMQConnection.rabbitDomain(data[1]);

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_SOLICITA_REL, Broker.EXCHANGE_NAME, Broker.ROUTING_SOLICITA_REL,
					this.rabbitDomain.getConnectionId());

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_RETORNO_PROCESSAMENTO_REL, Broker.EXCHANGE_NAME,
					Broker.ROUTING_RETORNO_PROCESSAMENTO_REL, this.rabbitDomain.getConnectionId());

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_RETORNO_CONSULTA_LOTE, Broker.EXCHANGE_NAME,
					Broker.ROUTING_RETORNO_CONSULTA_LOTE, this.rabbitDomain.getConnectionId());

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_CANCELA_NFS, Broker.EXCHANGE_NAME, Broker.ROUTING_CANCELA_NFS,
					this.rabbitDomain.getConnectionId());

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_RETORNO_CANCELA_NFS, Broker.EXCHANGE_NAME,
					Broker.ROUTING_RETORNO_CANCELA_NFS, this.rabbitDomain.getConnectionId());

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_RETORNO_CONSULTA_NFS, Broker.EXCHANGE_NAME,
					Broker.ROUTING_RETORNO_CONSULTA_NFS, this.rabbitDomain.getConnectionId());

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_CONSULTA_NFS, Broker.EXCHANGE_NAME, Broker.ROUTING_CONSULTA_NFS,
					this.rabbitDomain.getConnectionId());

			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_TRANSMISSION, Broker.EXCHANGE_NAME, Broker.ROUTING_TRANSMISSION,
					this.rabbitDomain.getConnectionId());

		}
		return this.rabbitDomain;
	}

	public String[] getConnectionInfo() {

		String[] data = new String[5];

		if (OsDetect.OS_NAME().contains("windows"))
			HOST_FILE = "C:\\workspace\\st-rabbit-config\\rabbitmq.properties";

		if (System.getenv("RABBIT_CFG") == null) {
			File resourceConnection = new File(HOST_FILE);
			Properties properties = new Properties();
			try (InputStream in = new FileInputStream(resourceConnection)) {

				properties.load(in);

				data[0] = UtilString.replaceChars(new String(Base64.getDecoder().decode(properties.getProperty("CONNECTIONS"))));
				data[1] = UtilString.replaceChars(new String(Base64.getDecoder().decode(properties.getProperty("CONNECTIONID"))));
				data[2] = UtilString.replaceChars(new String(Base64.getDecoder().decode(properties.getProperty("HOST"))));
				data[3] = UtilString.replaceChars(new String(Base64.getDecoder().decode(properties.getProperty("USERNAME"))));
				data[4] = UtilString.replaceChars(new String(Base64.getDecoder().decode(properties.getProperty("PASSWORD"))));
				data[3] = UtilString.replaceChars(new String(Base64.getDecoder().decode(properties.getProperty("PORT"))));

			} catch (IOException ex) {
				System.out.println("Error on read application.properties, [Error] = " + ex.getMessage());
			}
		}
		
		return data;
	}
}
