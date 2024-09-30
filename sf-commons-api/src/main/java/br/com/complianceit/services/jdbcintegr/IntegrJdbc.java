package br.com.complianceit.services.jdbcintegr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import br.com.complianceit.services.oracle.OracleBootJdbc;

public class IntegrJdbc {
	private final String LOGGER_TMP_TABLE 		 	= "Error on execute Insert Script at table [%s], Keys[%s,$%]";
	private final String LOGGER_ERROR_REF_TMP_TABLE = "Error on create reference at table [%s], [error] = %s";

	public HashMap<Long,String> buildCollectionTpObjIntegr(PreparedStatement pstmt) throws SQLException {
		try(ResultSet resultSet = pstmt.executeQuery()){
			HashMap<Long, String> values = new HashMap<>();
			while(resultSet.next()) {
				values.put(resultSet.getLong(1), resultSet.getString(2));
			}
			return values;
		}catch(SQLException ex) {
			throw new SQLException(ex.getMessage());
		}
	}
	public HashMap<Long,String> tpObjIntegr(Connection connection, String objIntegrCd, String tipoObjIntegrCd) throws SQLException, ClassNotFoundException {
		String sql = "SELECT TP.ID, "
					+ 		"TP. DESCR "
					+ "FROM TIPO_OBJ_INTEGR TP "
					+ "INNER JOIN OBJ_INTEGR OBJ ON TP.OBJINTEGR_ID = OBJ.ID "
					+ "WHERE OBJ.CD = ? AND "
					+ "      TP.CD = ? ";
		try(PreparedStatement pstmt = connection.prepareStatement(sql)){
			pstmt.setString(1, objIntegrCd);//DADOS CONTABEIS
			pstmt.setString(2, tipoObjIntegrCd);//LANCAMENTO CONTABIL
			return this.buildCollectionTpObjIntegr(pstmt);
		}catch(SQLException ex) {
			throw new SQLException(ex.getMessage());
		}
	}
	
	public Long getMultOrgResult(PreparedStatement pstmt) throws SQLException {
		try(ResultSet resultSet = pstmt.executeQuery()){
			while(resultSet.next()) {
				return resultSet.getLong(1);
			} 
			throw new SQLException("Error on get multOrg reference...");
		}catch(SQLException ex) {
			throw new SQLException(ex.getMessage());
		}
	}
	
	public Long multOrg(Connection connection, String cd, String hash) throws SQLException, ClassNotFoundException {
		String sql = "SELECT M.ID "
					+ "FROM MULT_ORG M "
					+ "WHERE M.CD = ? AND "
					+ "      M.HASH = ? ";
		try(PreparedStatement pstmt = connection.prepareStatement(sql)){
			pstmt.setString(1, cd);
			pstmt.setString(2, hash);
			return this.getMultOrgResult(pstmt);
		}catch(SQLException ex) {
			throw new SQLException(ex.getMessage());
		}
	}
	
	public void createRecortAtTempObj(Long pk, 
									  Long loteIntWsId, 
									  Logger logger, 
									  Connection connection, 
									  OracleBootJdbc oracleBootJdbc) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO TMP_CTRL_ARQ").append(" VALUES (TMPCTRLARQ_SEQ.NEXTVAL,?,?)");
		try(PreparedStatement pstmt = oracleBootJdbc.preparedStatement(connection, sql.toString())){
			pstmt.setLong(1, pk);
			pstmt.setLong(2, loteIntWsId);
			int rows = pstmt.executeUpdate();
			if(rows < 0) {
				logger.error(String.format(LOGGER_TMP_TABLE, ("TMP_CTRL_ARQ"), pk, loteIntWsId));
			}
		}catch(SQLException ex) {
			logger.error(String.format(LOGGER_ERROR_REF_TMP_TABLE, ("TMP_CTRL_ARQ"), ex.getMessage()));
		}
	}
	
}
