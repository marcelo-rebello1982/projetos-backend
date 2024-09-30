package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class StApiProdutosApplication {

	public static void main(String[] args) {
		SpringApplication.run(StApiProdutosApplication.class, args);
		
		 // https://thorben-janssen.com/implement-soft-delete-hibernate/
	}

}
