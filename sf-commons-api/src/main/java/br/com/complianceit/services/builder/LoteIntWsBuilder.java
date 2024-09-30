package br.com.complianceit.services.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.complianceit.services.main.model.ws.LoteIntWs;
import lombok.Getter;


@Getter
public class LoteIntWsBuilder {

	protected LoteIntWs loteIntWs;
	
	public static class Builder {
		
		public LoteIntWsBuilder createLoteIntWsBuilder(Connection connection, 
													   Map<Long, String> values, 
													   Long multOrgId,
													   String cdMultOrg,
													   byte[] xmlRecebido,
													   Logger LOGGER) throws SQLException {
			try {
				Long tipoObjIntegrId 			  = values.entrySet().iterator().next().getKey();
				String tipoObjIntegrDescr		  = values.entrySet().iterator().next().getValue();
				
				LoteIntWsBuilder loteIntWsBuilder = new LoteIntWsBuilder();
				LoteIntWs loteIntWs				  = this.buildLoteIntWs(xmlRecebido, multOrgId, tipoObjIntegrId, null, LOGGER);
				
				if(loteIntWs != null) {
					loteIntWs.setId(this.sequenceValue(connection, "LOTEINTWS_SEQ"));
					loteIntWsBuilder.loteIntWs 		  = this.createLoteIntWs(connection, loteIntWs, tipoObjIntegrDescr, cdMultOrg, LOGGER);
					return loteIntWsBuilder;
				} else {
					throw new SQLException("LOTE_INT_WS is null...");
				}
			}catch(SQLException ex) {
				throw new SQLException(ex.getMessage());
			}
		}
		
		private LoteIntWs buildLoteIntWs(byte[] xmlRecebido, 
										Long multOrgId, 
										Long tipoObjIntegrId, 
										String pathCompl,
										Logger LOGGER) {
			LoteIntWs loteIntWs = null;
			try {
				loteIntWs = new LoteIntWs();
				loteIntWs.setXmlRecebido(xmlRecebido);
				loteIntWs.setMultOrgId(multOrgId);
				loteIntWs.setDataHoraRecebimento(new java.util.Date());
				loteIntWs.setDataHoraProcessamento(new java.util.Date());
				loteIntWs.setDmStProc(LoteIntWs.RECEBIDO);
				loteIntWs.setTipoObjIntegrId(tipoObjIntegrId);
				loteIntWs.setDiretorioLote(pathCompl);
				loteIntWs.setDmProcXml(0);

			} catch (Exception ex) {
				LOGGER.error(ex.getMessage());
			}
			return loteIntWs;
		}
		
		private LoteIntWs createLoteIntWs(Connection connection, 
										  LoteIntWs loteIntWs, 
										  String tipoObjIntegrDescr,
										  String cdMultOrg,
										  Logger LOGGER) throws SQLException {
			
	        PreparedStatement pstmt = null;            
	        try {
	            LOGGER.info("CREATING DATA FROM LOTE_INT_WS [" + tipoObjIntegrDescr + "]...");
	            StringBuilder sql = new StringBuilder();
	            sql.append("INSERT INTO LOTE_INT_WS(ID,")
	                    .append("MULTORG_ID,")
	                    .append("TIPOOBJINTEGR_ID,")
	                    .append("DT_HR_RECEB,")
	                    .append("DM_ST_PROC,")
	                    .append("DT_HR_PROC,")
	                    .append("XML_RECEB,")
	                    .append("DIR_LOTE,")
	                    .append("DM_PROCESSA_XML)")
	                    .append("VALUES (?,?,?,?,?,?,?,?,?)");
	            connection.setAutoCommit(false);
	
	            Long valueID = this.sequenceValue(connection, "LOTEINTWS_SEQ");
	            Long loteID  = 0l;
	
	            if (cdMultOrg.equals("48") || cdMultOrg.equals("41")) {
	                loteID = valueID;
	            } else {
	                LocalDateTime localDateTime = LocalDateTime.now();
	                int month = localDateTime.getMonthValue();
	                String textPlainMonth = month < 10 ? "0" + month : String.valueOf(month);
	                String year = String.valueOf(localDateTime.getYear()).substring(2, 4);
	                loteID = new Long(String.valueOf(year) + textPlainMonth + String.valueOf(valueID));
	            }
	            loteIntWs.setId(loteID);
	            pstmt = connection.prepareStatement(sql.toString());
	            pstmt.setLong(1, loteIntWs.getId());
	            pstmt.setLong(2, loteIntWs.getMultOrgId());
	            pstmt.setLong(3, loteIntWs.getTipoObjIntegrId());
	            pstmt.setTimestamp(4, new Timestamp(loteIntWs.getDataHoraRecebimento().getTime()));
	            pstmt.setInt(5, loteIntWs.getDmStProc());
	            pstmt.setTimestamp(6, new Timestamp(loteIntWs.getDataHoraProcessamento().getTime()));
	            pstmt.setBytes(7, loteIntWs.getXmlRecebido());
	            pstmt.setString(8, loteIntWs.getDiretorioLote());
	            pstmt.setInt(9, loteIntWs.getDmProcXml());
	
	            int rows = pstmt.executeUpdate();
	            if (rows > 0) {
	                connection.commit();
	            } else {
	                connection.rollback();
	            }
	            return loteIntWs;
	        } catch (Exception ex) {
	            LOGGER.error("ERROR ON CREATE RECORD LOTE_INT_WS, [ERRO] = " + ex.getMessage());
	            throw new SQLException("ERROR ON CREATE RECORD LOTE_INT_WS, [ERRO] = " + ex.getMessage());
	        } finally {
	            try {
	            	if(connection != null)connection.close();
	            	if(pstmt != null)pstmt.close();
	            } catch (SQLException ex) {
	                throw new SQLException("Error on close transactions, [error] = " + ex.getMessage());
	            }
	        }
	    }
	        
	    private Long sequenceValue(Connection connection, String sequence) throws SQLException {
	        try (Statement stmt = connection.createStatement()) {
	            try (ResultSet rs = stmt.executeQuery("SELECT " + sequence + ".NEXTVAL FROM DUAL")) {
	                long id = 0l;
	                while (rs.next()) {
	                    id = rs.getLong(1);
	                }
	                return id;
	            } catch (SQLException ex) {
	                throw new SQLException("ERROR ON CATCH LAST INSERT ID LOTE_INT_WS, [ERROR] = " + ex.getMessage());
	            }
	        } catch (SQLException ex) {
	            throw new SQLException("ERROR ON CALL LAST INSERT ID LOTE_INT_WS, [ERROR] = " + ex.getMessage());
	        }
	    }
	}
}
