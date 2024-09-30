package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryIntegracaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryIntegracaoApplication.class, args);
    }
    
	 // & "C:\workspace\discovery-corp\mvnw.cmd" spring-boot:run -f "C:\workspace\discovery-corp\pom.xml"
}
