package br.com.cadastroit.services.api.enums;

public enum ProcessaPedidoEnum {

	PEDIDO_VALIDATE_QUEUE_PAIR_NAME("ST-PEDIDO_APROVADO-PAIR"),
	PEDIDO_VALIDATE_PAIR_RK("rk-valida-pedido-pair"),
	PEDIDO_VALIDATE_QUEUE_ODD_NAME("CST-VALIDA-PEDIDO-ODD"),
	PEDIDO_VALIDATE_ODD_RK("valida-pedido-odd"),
	PEDIDO_VALIDATE_EXCHANGE("ST-PEDIDO-VALIDA-DELAYED"),
	PEDIDO_INFO_SETUP("Registrando Fila: %s. Ex.: %s. RK.: %s"),

	DRIVER_ERROR("Erro na leitura do driver JDBC Oracle, [erro] = %s"),
	ERROR_QUEUE("Error on process message at %s queue. Queue name: CST-VALIDA-%s, [error] = %s"),
	NOSQL_ERROR("Falha ao salvar controle no NoSQL (Collection[%s]), [erro] = %s"),
	SUCCESS_VALIDATE("SUCCESS ON validate record %s[ID] = %s, [COMMIT]"),
	ERROR_VALIDATE("ERROR ON validate record %s[ID] = %s, [COMMIT]"),
	MESSAGE_VALIDATE("Validando %s_ID[%s] na tabela %s"),
	MESSAGE_VALIDATE_FAIL("Descartando mensagem para %s[ID] = %s"),
	MESSAGE_DB_LOG_ORACLE_NOREFERENCE("%s = %s sem referencia na tabela %s."),
	MESSAGE_DB_LOG_ORACLE_NOREFERENCE_FAIL("%s = %s nao validada(o). Esgotado o nro de validacoes programadas. Total de validacoes realizadas = [%s]. Verifique a situacao do registro no banco ORACLE."),
	SQL_COUNT_GNRE_RECORDS("SELECT COUNT(ID) FROM %s WHERE ID = ?");

	private String pedidoProperty;

	ProcessaPedidoEnum(String pedidoProperty) {

		this.pedidoProperty = pedidoProperty;
	}

	public String propertyValue() {

		return this.pedidoProperty;
	}
}
