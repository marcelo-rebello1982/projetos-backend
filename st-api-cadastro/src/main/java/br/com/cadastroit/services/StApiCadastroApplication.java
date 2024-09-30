package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
@EntityScan(basePackages = "br.com.cadastroit.services.api.domain")
@EnableJpaRepositories(basePackages = "br.com.cadastroit.services.repositories")
public class StApiCadastroApplication {

	public static void main(String[] args) {

		SpringApplication.run(StApiCadastroApplication.class, args);
		
		 // https://thorben-janssen.com/implement-soft-delete-hibernate/
	}
}
