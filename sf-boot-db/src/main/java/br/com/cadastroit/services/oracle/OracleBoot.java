package br.com.cadastroit.services.oracle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
public class OracleBoot {

	private String HOST_FILE = "/opt/sfconfig/";
	private static String FILE_SAVE_FOLDER = ""; 
	private String SERVICE_PACKAGE = "";

	private String SPRING_DATASOURCE_URL;
	private String SPRING_DATASOURCE_USERNAME;
	private String SPRING_DATASOURCE_PASSWORD;
	private String SPRING_DATASOURCE_DRIVER_CLASS_NAME;
	
	@Bean
	public DataSource dataSource() {
		if(System.getenv("JDBC_DRIVER") != null) {
			SPRING_DATASOURCE_DRIVER_CLASS_NAME = System.getenv("JDBC_DRIVER");
		}
		if(System.getenv("SERVICE_PROVIDER") != null) {
			SERVICE_PACKAGE = new String(Base64.getDecoder().decode(System.getenv("SERVICE_PROVIDER")));
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
//		System.out.println("URL:"+SPRING_DATASOURCE_URL);
//		System.out.println("USERNAME:"+SPRING_DATASOURCE_USERNAME);
//		System.out.println("PASSWORD:"+SPRING_DATASOURCE_PASSWORD);
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		if(SPRING_DATASOURCE_URL == null) {
			if(System.getProperty("os.name").toLowerCase().contains("windows"))HOST_FILE="C:\\Workspace\\sfconfig";
			String nameFile = HOST_FILE + "jdbc.properties";
			File f 			= new File(nameFile);
			boolean crypt   = false;
			if(!f.exists()) {
				nameFile = HOST_FILE + "jdbc-crypt.properties";
				f  		 = new File(nameFile);
				crypt	 = true;
			}
			Properties properties = new Properties();
			try (InputStream is = new FileInputStream(f)) {
				properties.load(is);
				
				SPRING_DATASOURCE_DRIVER_CLASS_NAME = properties.getProperty("PARAM_DRIVER_ORCL");
				SPRING_DATASOURCE_URL 				= crypt ? new String(Base64.getDecoder().decode(properties.getProperty("PARAM_URL_ORCL")))  : properties.getProperty("PARAM_URL_ORCL");
				SPRING_DATASOURCE_USERNAME			= crypt ? new String(Base64.getDecoder().decode(properties.getProperty("PARAM_USER_ORCL"))) : properties.getProperty("PARAM_USER_ORCL");
				SPRING_DATASOURCE_PASSWORD 			= crypt ? new String(Base64.getDecoder().decode(properties.getProperty("PARAM_PASS_ORCL"))) : properties.getProperty("PARAM_PASS_ORCL");
				SERVICE_PACKAGE  					= crypt ? new String(Base64.getDecoder().decode(properties.getProperty("SERVICE_PACKAGE"))) : properties.getProperty("SERVICE_PACKAGE");
				
				dataSource.setDriverClassName(SPRING_DATASOURCE_DRIVER_CLASS_NAME.trim());
				dataSource.setUrl(SPRING_DATASOURCE_URL.trim());
				FILE_SAVE_FOLDER 	  = properties.getProperty("FILE_SAVE_FOLDER");
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}else {
			dataSource.setDriverClassName(SPRING_DATASOURCE_DRIVER_CLASS_NAME.trim());
			dataSource.setUrl(SPRING_DATASOURCE_URL.trim());
		}
		dataSource.setUsername(SPRING_DATASOURCE_USERNAME.trim());
		dataSource.setPassword(SPRING_DATASOURCE_PASSWORD.trim());
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.ORACLE);
		vendorAdapter.setGenerateDdl(false);

		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setPersistenceUnitName("csf-api");
		localContainerEntityManagerFactoryBean.setDataSource(dataSource());
		localContainerEntityManagerFactoryBean.setPackagesToScan(SERVICE_PACKAGE.trim());
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
		localContainerEntityManagerFactoryBean.setJpaProperties(additionalProperties());

		return localContainerEntityManagerFactoryBean;
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "none");
		properties.setProperty("hibernate.connection.pool_size", "500");
		properties.setProperty("hibernate.show_sql", "false");
		properties.setProperty("hibernate.format_sql", "false");

//		properties.setProperty("hibernate.current_session_context_class",
//				env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
//		properties.setProperty("hibernate.jdbc.lob.non_contextual_creation",
//				env.getProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation"));
		return properties;
	}
	
	public static String FILE_SAVE_FOLDER() {
		return FILE_SAVE_FOLDER.trim();
	}
}