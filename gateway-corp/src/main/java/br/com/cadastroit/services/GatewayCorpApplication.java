package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class GatewayCorpApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayCorpApplication.class, args);
	} 
	
	 // & "C:\workspace\gateway-corp\mvnw.cmd" spring-boot:run -f "C:\workspace\gateway-corp\pom.xml"

}
