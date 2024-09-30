package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CstMonitoramentonfsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CstMonitoramentonfsApiApplication.class, args);
	}
}
