package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class CstJwtTokenApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CstJwtTokenApplication.class, args);
	}

}
