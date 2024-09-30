package br.com.cadastroit.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import br.com.cadastroit.services.configuration.broker.utils.BrokerUtilities;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "br.com.complianceit.*" })
@EnableJpaRepositories
@SpringBootApplication
public class CstRelatoriodesifApiApplication extends BrokerUtilities {

	public static void main(String[] args) {
		SpringApplication.run(CstRelatoriodesifApiApplication.class, args);
	}
}
