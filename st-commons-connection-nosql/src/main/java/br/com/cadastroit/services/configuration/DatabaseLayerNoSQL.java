package br.com.cadastroit.services.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Configuration
public class DatabaseLayerNoSQL implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host	 	= System.getenv("MONGO_HOST");
    private String username = System.getenv("MONGO_USERNAME");
    private String password = System.getenv("MONGO_PASSWORD");
    private String port		= System.getenv("MONGO_PORT");
    private String database = System.getenv("MONGO_DB");

    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://"+username+":"+password+"@"+host+":"+port+"/"+database);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .retryWrites(true)
                .applyToConnectionPoolSettings(pool->{
                    pool.maxConnectionIdleTime(2000, TimeUnit.MILLISECONDS);
                    pool.maxSize(200);
                    pool.maintenanceFrequency(30000, TimeUnit.MILLISECONDS);
                }).build();
        MongoClient mongoClient = MongoClients.create(settings);
        mongoClient.getDatabase(database);

        return mongoClient;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(this.mongoClient(), database);
    }

}
