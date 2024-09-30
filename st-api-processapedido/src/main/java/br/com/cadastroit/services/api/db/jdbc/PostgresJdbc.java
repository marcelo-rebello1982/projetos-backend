package br.com.cadastroit.services.api.db.jdbc;
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
import java.util.Base64;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.stereotype.Component;

@Component
public class PostgresJdbc {
	
	private String HOST_FILE = "/opt/sf-db-config/";
	private String SPRING_DATASOURCE_URL;
	private String SPRING_DATASOURCE_USERNAME;
	private String SPRING_DATASOURCE_PASSWORD;
	private String SPRING_DATASOURCE_DRIVER_CLASS_NAME;
	
	private void getProperties() {
		if(System.getenv("JDBC_DRIVER") != null) {
			SPRING_DATASOURCE_DRIVER_CLASS_NAME = System.getenv("JDBC_DRIVER");
		}
		if(System.getenv("SPRING_DATASOURCE_URL") != null) {
			SPRING_DATASOURCE_URL = new String(Base64.getDecoder().decode(System.getenv("SPRING_DATASOURCE_URL")));
		}
		if(System.getenv("SPRING_DATASOURCE_USERNAME") != null) {
			SPRING_DATASOURCE_USERNAME = new String(Base64.getDecoder().decode(System.getenv("SPRING_DATASOURCE_USERNAME")));
		}
		if(System.getenv("SPRING_DATASOURCE_PASSWORD") != null) {
			SPRING_DATASOURCE_PASSWORD = new String(Base64.getDecoder().decode(System.getenv("SPRING_DATASOURCE_PASSWORD")));
		}
		
		if(SPRING_DATASOURCE_URL == null) {
			if(System.getProperty("os.name").toLowerCase().contains("windows"))HOST_FILE="C:\\Workspace\\st-db-config\\";
			File f = new File(HOST_FILE+"jdbc.properties");
			Properties properties = new Properties();
			try(InputStream is = new FileInputStream(f)){
				properties.load(is);
				
				SPRING_DATASOURCE_DRIVER_CLASS_NAME = properties.getProperty("PARAM_DRIVER_PGSQL");
				SPRING_DATASOURCE_URL    			= properties.getProperty("PARAM_URL_PGSQL");
				SPRING_DATASOURCE_USERNAME   		= properties.getProperty("PARAM_USER_PGSQL");
				SPRING_DATASOURCE_PASSWORD   		= properties.getProperty("PARAM_PASS_PGSQL");
				
			}catch(IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	public Connection getConnection(EntityManagerFactory entityManagerFactory) throws ClassNotFoundException, SQLException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
        Connection connection = entityManager.unwrap(java.sql.Connection.class);
        return connection;
	}
	
	public Connection getConnection() throws ClassNotFoundException,SQLException {
		this.getProperties();
		Class.forName(SPRING_DATASOURCE_DRIVER_CLASS_NAME);
		Connection connection = DriverManager.getConnection(SPRING_DATASOURCE_URL,SPRING_DATASOURCE_USERNAME,SPRING_DATASOURCE_PASSWORD);
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
		if(connection != null)connection.close();
	}
	
	public void closeStatment(Statement statement) throws SQLException{
		if(statement != null)statement.close();
	}
	
	public void closePrepareStament(PreparedStatement preparedStatement) throws SQLException{
		if(preparedStatement != null)preparedStatement.close();
	}
	
	public void closeResultSet(ResultSet resultSet) throws SQLException {
		if(resultSet != null)resultSet.close();
	}
	
	public void close(Connection connection, Statement statement, PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
		if(resultSet != null)resultSet.close();
		if(statement != null)statement.close();
		if(preparedStatement != null)preparedStatement.close();
		if(connection != null)connection.close();
	}
}
