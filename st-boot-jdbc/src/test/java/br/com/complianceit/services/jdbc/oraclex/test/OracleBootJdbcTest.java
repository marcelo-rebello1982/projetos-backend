package br.com.complianceit.services.jdbc.oraclex.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.cadastroit.services.jdbc.oraclex.OracleBootJdbc;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OracleBootJdbc.class})
class OracleBootJdbcTest {

	@Autowired
	private OracleBootJdbc oracleBootJdbc;
	
	@Test
	void checkJdbcConnection() throws ClassNotFoundException, SQLException{
		Connection connection = null;
		Statement statement   = null;
		ResultSet resultSet   = null;
		PreparedStatement pstmt = null;
		try {
			connection = this.oracleBootJdbc.connection();
			if(!connection.isClosed()) {
				statement = this.oracleBootJdbc.statement(connection);
				pstmt	  = this.oracleBootJdbc.preparedStatement(connection, "SELECT 1 FROM DUAL");
				resultSet = statement.executeQuery("SELECT 1 FROM DUAL");
				resultSet = pstmt.executeQuery();
				while(resultSet.next()) {
					System.out.println(resultSet.getObject(1));
				}
			}
		} finally {
			this.oracleBootJdbc.close(connection, statement, pstmt, resultSet);
		}
	}
	
	@Test
	void checkJdbcConnectionMethods() throws ClassNotFoundException, SQLException{
		Connection connection = null;
		Statement statement   = null;
		ResultSet resultSet   = null;
		PreparedStatement pstmt = null;
		try {
			connection = this.oracleBootJdbc.connection();
			if(!connection.isClosed()) {
				statement = this.oracleBootJdbc.statement(connection);
				pstmt	  = this.oracleBootJdbc.preparedStatement(connection, "SELECT 1 FROM DUAL");
				resultSet = statement.executeQuery("SELECT 1 FROM DUAL");
				resultSet = pstmt.executeQuery();
				while(resultSet.next()) {
					System.out.println(resultSet.getObject(1));
				}
			}
		} finally {
			this.oracleBootJdbc.closeResultSet(resultSet);
			this.oracleBootJdbc.closeStatment(statement);
			this.oracleBootJdbc.closePrepareStament(pstmt);
			this.oracleBootJdbc.closeConnection(connection);
			
			this.oracleBootJdbc.close(connection, statement, null, resultSet);
		}
	}
}
