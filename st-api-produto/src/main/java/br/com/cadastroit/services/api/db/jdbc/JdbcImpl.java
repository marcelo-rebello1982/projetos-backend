package br.com.cadastroit.services.api.db.jdbc;

import org.apache.log4j.Logger;

public interface JdbcImpl {
	
	String DRIVER_ERROR = "Erro na leitura do Driver, [erro] = %s";
	String CONNECTION_ERROR = "Erro na conexao com banco de dados, [erro] = %s";
	String SEARCH_ERROR = "Erro na consulta, [TABELA] = %s, [ORIGEM] = %s";
	String LOG_CONNECTION = "%s connection is %s, connectionID = %s...";

	default void logConnection(Logger LOGGER, String serviceName, String actionConnection, String addressConnection){
		LOGGER.info(String.format(JdbcImpl.LOG_CONNECTION, serviceName, actionConnection, addressConnection));
	}

	String driver = System.getenv("SPRING_DATASOURCE_DRIVER_CLASS_NAME");
	String url = System.getenv("SPRING_DATASOURCE_URL");
	String user = System.getenv("SPRING_DATASOURCE_USERNAME");
	String pass = System.getenv("SPRING_DATASOURCE_PASSWORD");

	String sqlCancelamento = "SELECT NFC.ID "
						   + "FROM NOTA_FISCAL_CANC NFC "
						   + "INNER JOIN NOTA_FISCAL NF ON NF.ID = NFC.NOTAFISCAL_ID "
						   + "INNER JOIN MOD_FISCAL M ON M.ID = NF.MODFISCAL_ID "
						   + "INNER JOIN EMPRESA E ON E.ID = NF.EMPRESA_ID "
						   + "WHERE NF.DM_ST_PROC = 1 AND "
						   		 + "E.DM_SITUACAO = 1 AND "
						   		 + "M.COD_MOD = '55' AND "
						   		 + "NF.DM_LEGADO = 0 AND "
						   		 + "NF.DM_IND_EMIT = 0 ORDER BY NFC.ID ASC";
	
	String sqlUpdateCanc = "UPDATE NOTA_FISCAL SET DM_ST_PROC = 99 WHERE ID = ?";
	
	String sqlCce = "SELECT CCE.ID "
				  + "FROM NOTA_FISCAL_CCE CCE "
				  + "INNER JOIN NOTA_FISCAL NF ON NF.ID = CCE.NOTAFISCAL_ID "
				  + "INNER JOIN MOD_FISCAL M ON M.ID = NF.MODFISCAL_ID "
				  + "INNER JOIN EMPRESA E ON E.ID = NF.EMPRESA_ID "
				  + "WHERE CCE.DM_ST_PROC = 1 AND "
				   		+ "E.DM_SITUACAO = 1 AND "
				   		+ "M.COD_MOD = '55' ORDER BY CCE.ID ASC";
	
	String sqlUpdateCCe = "UPDATE NOTA_FISCAL_CCE SET DM_ST_PROC = 99 WHERE ID = ?";
	
	String sqlInutiliza = "SELECT INUT.ID "
						+ "FROM INUTILIZA_NOTA_FISCAL INUT "
						+ "INNER JOIN EMPRESA E ON E.ID = INUT.EMPRESA_ID "
						+ "WHERE INUT.DM_SITUACAO = 0 AND "
							  + "E.DM_SITUACAO = 1";
	
	String sqlUpdateInut = "UPDATE INUTILIZA_NOTA_FISCAL SET DM_SITUACAO = 9 WHERE ID = ?";
	
	
	String sqlNfeAutorizadosEvento = "SELECT EV.ID "
						   + "FROM EVENTO_NFE EV "
						   + "INNER JOIN NOTA_FISCAL NF ON NF.ID = EV.NOTAFISCAL_ID "
						   + "INNER JOIN MOD_FISCAL M ON M.ID = NF.MODFISCAL_ID "
						   + "INNER JOIN EMPRESA E ON NF.EMPRESA_ID = E.ID "
						   + "INNER JOIN TIPO_EVENTO_SEFAZ TP ON TP.ID = EV.TIPOEVENTOSEFAZ_ID "
						   + "WHERE EV.DM_ST_PROC = 1 AND "
						   + "      E.DM_SITUACAO = 1 AND "
			               + "		TP.CD = '110150' AND "
						   + "		M.COD_MOD = '55' ORDER BY EV.ID";

	String sqlEventoProrrogacao = "SELECT PEDIDO.ID, EV.ID "+
			"FROM NF_PED_PRORROG PEDIDO " +
			"INNER JOIN EVENTO_NFE EV ON PEDIDO.EVENTONFE_ID = EV.ID " +
			"INNER JOIN NOTA_FISCAL NF ON NF.ID = EV.NOTAFISCAL_ID "+
			"INNER JOIN MOD_FISCAL M ON M.ID = NF.MODFISCAL_ID "+
			"INNER JOIN EMPRESA E ON NF.EMPRESA_ID = E.ID "+
			"INNER JOIN TIPO_EVENTO_SEFAZ TP ON TP.ID = EV.TIPOEVENTOSEFAZ_ID "+
			"WHERE EV.DM_ST_PROC = 1 AND "+
			"      E.DM_SITUACAO = 1 AND "+
			"	   TP.CD IN ('111500', '111501') AND "+
			"      M.COD_MOD = '55' ORDER BY EV.ID";

	String sqlEventoCancelamentoProrrogacao = "SELECT PEDIDO_CANC.ID, EV.ID "+
			"FROM NF_PED_PRORROG_CANC PEDIDO_CANC "+
			"INNER JOIN EVENTO_NFE EV ON PEDIDO_CANC.EVENTONFE_ID = EV.ID "+
			"INNER JOIN NOTA_FISCAL NF ON NF.ID = EV.NOTAFISCAL_ID "+
			"INNER JOIN MOD_FISCAL M ON M.ID = NF.MODFISCAL_ID "+
			"INNER JOIN EMPRESA E ON NF.EMPRESA_ID = E.ID "+
			"INNER JOIN TIPO_EVENTO_SEFAZ TP ON TP.ID = EV.TIPOEVENTOSEFAZ_ID "+
			"WHERE EV.DM_ST_PROC = 1 AND "+
			"	E.DM_SITUACAO = 1 AND " +
			"	TP.CD IN ('111502', '111503') AND "+
			"	M.COD_MOD = '55' ORDER BY EV.ID";

	String sqlUpdateEvento = "UPDATE EVENTO_NFE SET DM_ST_PROC = 9 WHERE ID = ?";

}
