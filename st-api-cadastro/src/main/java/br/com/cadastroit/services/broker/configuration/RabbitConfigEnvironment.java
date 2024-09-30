package br.com.cadastroit.services.broker.configuration;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
public class RabbitConfigEnvironment {

	private final String ERROR = "Erro no setup do mensageiro, erro => ?";
	private Logger log = Logger.getLogger(RabbitBeanRelatorioConfiguration.class);
	private RabbitBeanRelatorioConfiguration rabbitBean;

	@Bean
	public RabbitBeanRelatorioConfiguration rabbitConfig() throws Exception {

		try {
			if (this.rabbitBean == null) {
				this.rabbitBean = RabbitBeanRelatorioConfiguration.builder().build();
				this.rabbitBean.createDomains();
			}
			return rabbitBean;
		} catch (Exception ex) {
			throw new Exception(String.format(ERROR, ex.getMessage()));
		}
	}
}
