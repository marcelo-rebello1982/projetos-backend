package br.com.cadastroit.services.jdbc.oraclex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class OracleBootJdbc {
	
	private String hostFile = "/opt/sf-config/";
	private String url;
	private String username;
	private String password;
	private String driver;
	
	public void getProperties() {
		if(System.getenv("JDBC_DRIVER") != null) {
			driver = System.getenv("JDBC_DRIVER");
		}
		if(System.getenv("SPRING_DATASOURCE_URL") != null) {
			url = System.getenv("SPRING_DATASOURCE_URL");
		}
		if(System.getenv("SPRING_DATASOURCE_USERNAME") != null) {
			username = System.getenv("SPRING_DATASOURCE_USERNAME");
		}
		if(System.getenv("SPRING_DATASOURCE_PASSWORD") != null) {
			password = System.getenv("SPRING_DATASOURCE_PASSWORD");
		}
		
		if(url == null) {
			if(System.getProperty("os.name").toLowerCase().contains("windows"))hostFile="C:\\Worksapce\\st-db-config";
			File f = new File(hostFile+"jdbc.properties");
			Properties properties = new Properties();
			try(InputStream is = new FileInputStream(f)){
				properties.load(is);
				
				driver = properties.getProperty("PARAM_DRIVER_ORCL");
				url    			= properties.getProperty("PARAM_URL_ORCL");
				username   		= properties.getProperty("PARAM_USER_ORCL");
				password   		= properties.getProperty("PARAM_PASS_ORCL");
				
			}catch(IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	public Connection connection() throws ClassNotFoundException,SQLException {
		this.getProperties();
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url,username,password);
		connection.setAutoCommit(false);
		return connection;
	}
	
	public Statement statement(Connection connection) throws SQLException{
		return connection.createStatement();
	}
	
	public PreparedStatement preparedStatement(Connection connection, String sql) throws SQLException{
		return connection.prepareStatement(sql);
	}
	
	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}
	
	public void closeStatment(Statement statement) throws SQLException{
		statement.close();
	}
	
	public void closePrepareStament(PreparedStatement preparedStatement) throws SQLException{
		preparedStatement.close();
	}
	
	public void closeResultSet(ResultSet resultSet) throws SQLException {
		resultSet.close();
	}
	
	public void close(Connection connection, Statement statement, PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
		if(resultSet != null)resultSet.close();
		if(statement != null)statement.close();
		if(preparedStatement != null)preparedStatement.close();
		if(connection != null)connection.close();
	}
}
