package br.com.cadastroit.services.config;

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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.stereotype.Component;

@Component
public class PostGreSqlJdbc {

	private String HOST_FILE = "/opt/csfconfig/";
	private String SPRING_DATASOURCE_URL;
	private String SPRING_DATASOURCE_USERNAME;
	private String SPRING_DATASOURCE_PASSWORD;
	private String SPRING_DATASOURCE_DRIVER_CLASS_NAME;

	private void getProperties() {

		if (System.getenv("JDBC_DRIVER") != null) {
			SPRING_DATASOURCE_DRIVER_CLASS_NAME = System.getenv("JDBC_DRIVER");
		}
		if (System.getenv("SPRING_DATASOURCE_URL") != null) {
			SPRING_DATASOURCE_URL = new String(Base64.getDecoder().decode(System.getenv("SPRING_DATASOURCE_URL")));
		}
		if (System.getenv("SPRING_DATASOURCE_USERNAME") != null) {
			SPRING_DATASOURCE_USERNAME = new String(Base64.getDecoder().decode(System.getenv("SPRING_DATASOURCE_USERNAME")));
		}
		if (System.getenv("SPRING_DATASOURCE_PASSWORD") != null) {
			SPRING_DATASOURCE_PASSWORD = new String(Base64.getDecoder().decode(System.getenv("SPRING_DATASOURCE_PASSWORD")));
		}

		if (SPRING_DATASOURCE_URL == null) {
			// if(OsDetect.OS_NAME().contains("windows"))HOST_FILE="C:\\projetos\\csfconfig";
			File f = new File(HOST_FILE + "jdbc.properties");
			Properties properties = new Properties();
			try (InputStream is = new FileInputStream(f)) {
				properties.load(is);

				SPRING_DATASOURCE_DRIVER_CLASS_NAME = properties.getProperty("PARAM_DRIVER_ORCL");
				SPRING_DATASOURCE_URL = properties.getProperty("PARAM_URL_ORCL");
				SPRING_DATASOURCE_USERNAME = properties.getProperty("PARAM_USER_ORCL");
				SPRING_DATASOURCE_PASSWORD = properties.getProperty("PARAM_PASS_ORCL");

			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public Statement statement(Connection connection) throws SQLException {

		return connection.createStatement();
	}

	public PreparedStatement preparedStatement(Connection connection, String sql) throws SQLException {

		return connection.prepareStatement(sql);
	}

	public void closeConnection(Connection connection) throws SQLException {

		connection.close();
	}

	public void closeStatment(Statement statement) throws SQLException {

		statement.close();
	}

	public void closePrepareStament(PreparedStatement preparedStatement) throws SQLException {

		preparedStatement.close();
	}

	public void closeResultSet(ResultSet resultSet) throws SQLException {

		resultSet.close();
	}

	public Connection openConnection() throws ClassNotFoundException, SQLException {

		this.getProperties();
		Class.forName(SPRING_DATASOURCE_DRIVER_CLASS_NAME);
		return DriverManager.getConnection(SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD);
	}

	public Connection getConnection(EntityManager entityManager) throws SQLException {

		try {
			Session session = entityManager.unwrap(Session.class);
			SessionFactory sf = session.getSessionFactory();
			ConnectionProvider provider = sf.getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class);
			return provider.getConnection();
		} catch (SQLException ex) {
			throw new SQLException("FALHA AO RECUPERAR CONEXAO JDBC..., [ERRO] == " + ex.getMessage());
		}
	}

	public void close(Connection connection, Statement statement, PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {

		if (resultSet != null)
			resultSet.close();
		if (statement != null)
			statement.close();
		if (preparedStatement != null)
			preparedStatement.close();
		if (connection != null)
			connection.close();
	}
}
