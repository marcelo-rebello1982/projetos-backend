package br.com.cadastroit.services.configuration;

import org.apache.log4j.Logger;

import br.com.cadastroit.services.configuration.broker.utils.Broker;
import br.com.cadastroit.services.configuration.broker.utils.BrokerUtilities;
import br.com.cadastroit.services.configuration.broker.utils.CommonsSchedules;
import br.com.complianceit.rabbitmq.connectors.RabbitMQConnection;
import br.com.complianceit.rabbitmq.model.RabbitDomain;

public class CstSolicitaRelatorioDesifServicesConfiguration {

	private Logger log = Logger.getLogger(CstSolicitaRelatorioDesifServicesConfiguration.class);

	private RabbitMQConnection rabbitMQConnection;
	private RabbitDomain rabbitRelDesif;
	private final String connectionIdRelDesif = System.getenv("CONNECTION_ID_RELDESIF");

	public RabbitMQConnection initializer() throws Exception {
		if (this.rabbitMQConnection == null) {
			this.rabbitMQConnection = RabbitMQConnection.builder().build();
			this.rabbitMQConnection.buildConnectionFactory();
		}
		return this.rabbitMQConnection;
	}

	public RabbitDomain createDomainsSolicitaRelDesif() throws Exception {

		if (this.rabbitRelDesif == null) {
			this.initializer();
			this.rabbitRelDesif = this.rabbitMQConnection.rabbitDomain(connectionIdRelDesif);

			log.info("Validando mensageiro. Criando fila " + Broker.EXCHANGE_NAME + " -> " + Broker.ROUTING_SOLICITA_REL + " -> " + Broker.QUEUE_SOLICITA_REL);
			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_SOLICITA_REL, Broker.EXCHANGE_NAME, Broker.ROUTING_SOLICITA_REL, this.rabbitRelDesif.getConnectionId());

			log.info("Validando mensageiro. Criando fila " + Broker.EXCHANGE_NAME + " -> " + Broker.ROUTING_RETORNO_PROCESSAMENTO_REL + " -> " + Broker.QUEUE_RETORNO_PROCESSAMENTO_REL);
			rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_RETORNO_PROCESSAMENTO_REL, Broker.EXCHANGE_NAME, Broker.ROUTING_RETORNO_PROCESSAMENTO_REL, this.rabbitRelDesif.getConnectionId());

			/*
			 * log.info("Validando mensageiro. Criando fila "+ Broker.EXCHANGE_NAME
			 * +" -> "+Broker.ROUTING_RETORNO_CONSULTA_LOTE+" -> " +
			 * Broker.QUEUE_RETORNO_CONSULTA_LOTE);
			 * rabbitMQConnection.createDirectExchangeForQueue(Broker.
			 * QUEUE_RETORNO_CONSULTA_LOTE, Broker.EXCHANGE_NAME,
			 * Broker.ROUTING_RETORNO_CONSULTA_LOTE, this.rabbitRelDesif.getConnectionId());
			 * 
			 * log.info("Validando mensageiro. Criando fila "+ Broker.EXCHANGE_NAME
			 * +" -> "+Broker.ROUTING_CANCELA_NFS+" -> " + Broker.QUEUE_CANCELA_NFS);
			 * rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_CANCELA_NFS,
			 * Broker.EXCHANGE_NAME, Broker.ROUTING_CANCELA_NFS,
			 * this.rabbitRelDesif.getConnectionId());
			 * 
			 * log.info("Validando mensageiro. Criando fila "+ Broker.EXCHANGE_NAME
			 * +" -> "+Broker.ROUTING_RETORNO_CANCELA_NFS+" -> " +
			 * Broker.QUEUE_RETORNO_CANCELA_NFS);
			 * rabbitMQConnection.createDirectExchangeForQueue(Broker.
			 * QUEUE_RETORNO_CANCELA_NFS, Broker.EXCHANGE_NAME,
			 * Broker.ROUTING_RETORNO_CANCELA_NFS, this.rabbitRelDesif.getConnectionId());
			 * 
			 * log.info("Validando mensageiro. Criando fila "+ Broker.EXCHANGE_NAME
			 * +" -> "+Broker.ROUTING_RETORNO_CONSULTA_NFS+" -> " +
			 * Broker.QUEUE_RETORNO_CONSULTA_NFS);
			 * rabbitMQConnection.createDirectExchangeForQueue(Broker.
			 * QUEUE_RETORNO_CONSULTA_NFS, Broker.EXCHANGE_NAME,
			 * Broker.ROUTING_RETORNO_CONSULTA_NFS, this.rabbitRelDesif.getConnectionId());
			 * 
			 * log.info("Validando mensageiro. Criando fila "+ Broker.EXCHANGE_NAME
			 * +" -> "+Broker.ROUTING_CONSULTA_NFS+" -> " + Broker.QUEUE_CONSULTA_NFS);
			 * rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_CONSULTA_NFS,
			 * Broker.EXCHANGE_NAME, Broker.ROUTING_CONSULTA_NFS,
			 * this.rabbitRelDesif.getConnectionId());
			 * 
			 * log.info("Validando mensageiro. Criando fila "+ Broker.EXCHANGE_NAME
			 * +" -> "+Broker.ROUTING_TRANSMISSION+" -> " + Broker.QUEUE_TRANSMISSION);
			 * rabbitMQConnection.createDirectExchangeForQueue(Broker.QUEUE_TRANSMISSION,
			 * Broker.EXCHANGE_NAME, Broker.ROUTING_TRANSMISSION,
			 * this.rabbitRelDesif.getConnectionId());
			 */

			log.info("Validando mensageiro. Criando fila " + Broker.EXCHANGE_NAME_DELAYD + " -> "
					+ Broker.ROUTING_CONSULTA_REL_DELAYED + " -> " + Broker.QUEUE_CONSULTA_REL_DELAYED);
			rabbitMQConnection.createDirectExchangeDelayedMessageForQueue(Broker.QUEUE_CONSULTA_REL_DELAYED,
					Broker.EXCHANGE_NAME_DELAYD, Broker.ROUTING_CONSULTA_REL_DELAYED,
					this.rabbitRelDesif.getConnectionId());

			CommonsSchedules.WAIT(60000l);

			if (rabbitMQConnection.queueExists("REL_DESIF", Broker.QUEUE_MONTAGEM_DOC) != null) {
				log.info("Validando mensageiro. Deletando fila -> " + Broker.QUEUE_MONTAGEM_DOC);
				rabbitMQConnection.dropQueue(Broker.QUEUE_MONTAGEM_DOC, "REL_DESIF");
			}

			if (rabbitMQConnection.queueExists("REL_DESIF", Broker.QUEUE_MONTAGEM_CANC) != null) {
				log.info("Validando mensageiro. Deletando fila -> " + Broker.QUEUE_MONTAGEM_CANC);
				rabbitMQConnection.dropQueue(Broker.QUEUE_MONTAGEM_CANC, "REL_DESIF");
			}

			log.info("Validando mensageiro. Criando fila " + Broker.EXCHANGE_NAME_DELAYD + " -> "
					+ Broker.ROUTING_MONTAGEM_DOC + " -> " + Broker.QUEUE_MONTAGEM_DOC);
			rabbitMQConnection.createDirectExchangeDelayedMessageForQueue(Broker.QUEUE_MONTAGEM_DOC,
					Broker.EXCHANGE_NAME_DELAYD, Broker.ROUTING_MONTAGEM_DOC, this.rabbitRelDesif.getConnectionId());

			log.info("Validando mensageiro. Criando fila " + Broker.EXCHANGE_NAME_DELAYD + " -> "
					+ Broker.ROUTING_MONTAGEM_CANC + " -> " + Broker.QUEUE_MONTAGEM_CANC);
			rabbitMQConnection.createDirectExchangeDelayedMessageForQueue(Broker.QUEUE_MONTAGEM_CANC,
					Broker.EXCHANGE_NAME_DELAYD, Broker.ROUTING_MONTAGEM_CANC, this.rabbitRelDesif.getConnectionId());

			rabbitMQConnection.sendObjectMessage("REL_DESIF", Broker.EXCHANGE_NAME_DELAYD, Broker.ROUTING_MONTAGEM_DOC,
					BrokerUtilities.buildMessageSchedule(30000, "RECUPERANDO LOTES MONTAGEM"));// 5 segundos
			rabbitMQConnection.sendObjectMessage("REL_DESIF", Broker.EXCHANGE_NAME_DELAYD, Broker.ROUTING_MONTAGEM_CANC,
					BrokerUtilities.buildMessageSchedule(35000, "RECUPERANDO LOTES CANCELAMENTO"));// 30 segundos

		}
		return this.rabbitRelDesif;
	}
}
