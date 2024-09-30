package br.com.cadastroit.services.api.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import br.com.cadastroit.services.api.db.jdbc.JdbcImpl;
import br.com.cadastroit.services.desif.enumerators.EnumDesifSQL;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PedidoReference {

	private Logger logger = Logger.getLogger(PedidoReference.class);
	private final String SQL = EnumDesifSQL.R_LOTEINTWS_INSERT_SQL.value();
	private final String SEQUENCE = EnumDesifSQL.R_LOTEINTWS_SEQ.value();
	private final String TABLE = EnumDesifSQL.R_LOTEINTWS_TBL.value();

	public Long getReference(Long pedidoId, Long pessoaId, String sufixTable, String sufixSeq) throws SQLException, ClassNotFoundException {

		try {
			Class.forName(JdbcImpl.driver);
			String exception = "";
			try (Connection connection = DriverManager.getConnection(JdbcImpl.url, JdbcImpl.user, JdbcImpl.pass)) {

				String tableRLoteIntWS = String.format(TABLE, sufixTable);
				String seqRLoteIntWS = String.format(SEQUENCE, sufixSeq) + ".NEXTVAL";
				String sqlInsert = String.format(SQL, tableRLoteIntWS, seqRLoteIntWS, pedidoId, pessoaId);
				
				try (Statement statement = connection.createStatement()) {
					int rows = statement.executeUpdate(sqlInsert);
					if (rows >= 0) {
						logger.info(String.format("Associacao criada na tabela %s. LOTEINTWS_ID = %s", tableRLoteIntWS, pedidoId));
					}
					return Long.valueOf(seqRLoteIntWS);
					
				} catch (SQLException ex) {
					exception = String.format("Erro na associacao dos dados na tabela %s, LOTEINTWS_ID = %s. [Erro] = %s", tableRLoteIntWS, pedidoId,
							ex.getMessage());
					throw new SQLException(exception);
				}
				
			} catch (SQLException ex) {
				throw new SQLException(
						(!exception.equals("") ? exception : (String.format("Falhas na conexao com banco de dados, [Erro] = %s", ex.getMessage()))));
			}
		} catch (ClassNotFoundException ex) {
			throw new ClassNotFoundException(String.format("Driver de conexao invalido, [Erro] = %s", ex.getMessage()));
		}
	}
}
