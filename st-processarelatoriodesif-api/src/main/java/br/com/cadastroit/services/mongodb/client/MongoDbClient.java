package br.com.cadastroit.services.mongodb.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.bson.UuidRepresentation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import br.com.cadastroit.services.OsDetect;

@Component
public class MongoDbClient {

	private String HOST_FILE = "/opt/csfconfig/nosql-processarelatorio-desif.properties";
	private MongoClient mongoClient;

	public MongoTemplate mongoTemplate() {
		if (OsDetect.OS_NAME().contains("windows")) HOST_FILE = "C:\\ComplianceFiscal\\csfconfig\\nosql-processarelatorio-desif.properties";
		String host = "";
		String database = "";
		Integer port = 27017;
		String username = "";
		String password = "";

		if (System.getenv("NOSQL_DATABASE") != null) {
			database = new String(Base64.getDecoder().decode(System.getenv("NOSQL_DATABASE")));
		}
		if (System.getenv("NOSQL_HOST") != null) {
			host = new String(Base64.getDecoder().decode(System.getenv("NOSQL_HOST")));
		}
		if (System.getenv("NOSQL_USERNAME") != null) {
			username = new String(Base64.getDecoder().decode(System.getenv("NOSQL_USERNAME")));
		}
		if (System.getenv("NOSQL_PASSWORD") != null) {
			password = new String(Base64.getDecoder().decode(System.getenv("NOSQL_PASSWORD")));
		}
		if (System.getenv("NOSQL_DATABASE") == null) {
			File resourceConnection = new File(HOST_FILE);
			Properties properties = new Properties();
			try (InputStream in = new FileInputStream(resourceConnection)) {
				properties.load(in);
				host = new String(Base64.getDecoder().decode(properties.getProperty("NOSQL_HOST")));
				database = new String(Base64.getDecoder().decode(properties.getProperty("NOSQL_DATABASE")));
				username = new String(Base64.getDecoder().decode(properties.getProperty("NOSQL_USERNAME")));
				password = new String(Base64.getDecoder().decode(properties.getProperty("NOSQL_PASSWORD")));
			} catch (IOException ex) {
				System.out.println("Error on read application.properties, [Error] = " + ex.getMessage());
			}
		}

		ConnectionString connectionString = new ConnectionString("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/" + database);
		MongoClientSettings settings = MongoClientSettings.builder()
			.applyToSslSettings(builder -> {
				builder.enabled(false).build();
		}).applyToConnectionPoolSettings(builder -> {
			builder.maxSize(60)
			.maxConnectionLifeTime(60, TimeUnit.SECONDS)
			.maxConnectionIdleTime(60, TimeUnit.SECONDS)
			.maxWaitTime(60, TimeUnit.SECONDS)
			.minSize(20).build();
		}).applyConnectionString(connectionString)
			.uuidRepresentation(UuidRepresentation.STANDARD)
	        .applyToConnectionPoolSettings(pool->{
	        pool.maxConnectionIdleTime(2000, TimeUnit.MILLISECONDS);
	        pool.maxSize(200);
	        pool.maintenanceFrequency(30000, TimeUnit.MILLISECONDS);		
	        }).build();
		mongoClient = MongoClients.create(settings);
		return new MongoTemplate(mongoClient, database);
	}
	
	public void closeMongoConnection() {
		mongoClient.close();
	}
}