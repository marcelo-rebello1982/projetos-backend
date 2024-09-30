package br.com.cadastroit.services.api.broker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
public class CstRabbitApiConfiguration {

	private RabbitBeanConfiguration rabbitBeanConfiguration;

	@Bean
	public RabbitBeanConfiguration rabbitQeuesConfiguration() throws Exception {

		try {
			if (this.rabbitBeanConfiguration == null) {
				this.rabbitBeanConfiguration = RabbitBeanConfiguration.builder().build();
				this.rabbitBeanConfiguration.createQueues();
			}
			return this.rabbitBeanConfiguration;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

}
