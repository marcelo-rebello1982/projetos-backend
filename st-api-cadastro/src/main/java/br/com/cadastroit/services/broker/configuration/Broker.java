package br.com.cadastroit.services.broker.configuration;
public class Broker {

	public static final String EXCHANGE_NAME = "DIRECT-RELATORIO-PEDIDO";
	
	//---------------------------------------------------------------------------------------
	
	public static final String ROUTING_SOLICITA_REL      	      = "rk_solicita-rel";
	public static final String QUEUE_SOLICITA_REL     	  	      = Broker.EXCHANGE_NAME+"-SOLICITA-REL";
	
	public static final String ROUTING_RETORNO_PROCESSAMENTO_REL     = "rk_retorno-processamento-rel";
	public static final String QUEUE_RETORNO_PROCESSAMENTO_REL		  = Broker.EXCHANGE_NAME+"-RETORNO-PROCESSAMENTO-REL";
	
	//---------------------------------------------------------------------------------------
	
	public static final String ROUTING_RETORNO_CONSULTA_LOTE  = "retorno-consulta-lote";
	public static final String QUEUE_RETORNO_CONSULTA_LOTE    = Broker.EXCHANGE_NAME+"-RETORNO-CONSULTA-LOTE";
	
	//---------------------------------------------------------------------------------------
	
	public static final String ROUTING_CANCELA_NFS    		  = "cancela-nfs";
	public static final String QUEUE_CANCELA_NFS      		  = Broker.EXCHANGE_NAME+"-CANCELA-NFS";
	
	public static final String ROUTING_RETORNO_CANCELA_NFS    = "retorno-cancela-nfs";
	public static final String QUEUE_RETORNO_CANCELA_NFS      = Broker.EXCHANGE_NAME+"-RETORNO-CANCELA-NFS";
	
	//---------------------------------------------------------------------------------------
	
	public static final String ROUTING_CONSULTA_NFS   		  = "consulta-nfs";
	public static final String QUEUE_CONSULTA_NFS     		  = Broker.EXCHANGE_NAME+"-CONSULTA-NFS";
	
	public static final String ROUTING_RETORNO_CONSULTA_NFS   = "retorno-consulta-nfs";
	public static final String QUEUE_RETORNO_CONSULTA_NFS     = Broker.EXCHANGE_NAME+"-RETORNO-CONSULTA-NFS";
	
	//---------------------------------------------------------------------------------------
	
	public static final String ROUTING_TRANSMISSION   		  = "envia-documento";
	public static final String QUEUE_TRANSMISSION     		  = Broker.EXCHANGE_NAME+"-ENVIA-DOCUMENTO";
	
	//---------------------------------------------------------------------------------------
	
	public static final String EXCHANGE_NAME_DELAYD = "RELATORIO-PEDIDO-DELAYED";
	
	//---------------------------------------------------------------------------------------
	
	public static final String ROUTING_CONSULTA_REL_DELAYED  = "consulta-rel-delayed";
	public static final String QUEUE_CONSULTA_REL_DELAYED 	  = Broker.EXCHANGE_NAME_DELAYD+"-CONSULTA-REL-DELAYED";
	
	//---------------------------------------------------------------------------------------
	
	public static final String ROUTING_MONTAGEM_DOC 		  = "monta-documento";
	public static final String QUEUE_MONTAGEM_DOC      		  = Broker.EXCHANGE_NAME_DELAYD+"-MONTA-DOCUMENTO";
		
	//---------------------------------------------------------------------------------------
		
	public static final String ROUTING_MONTAGEM_CANC 		  = "canc-documento";
	public static final String QUEUE_MONTAGEM_CANC            = Broker.EXCHANGE_NAME_DELAYD+"-CANC-DOCUMENTO";	
	
}
