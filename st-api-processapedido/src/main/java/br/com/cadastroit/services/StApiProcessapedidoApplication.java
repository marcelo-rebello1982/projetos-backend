package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class StApiProcessapedidoApplication {

	public static void main(String[] args) {

		SpringApplication.run(StApiProcessapedidoApplication.class, args);
	}
}
