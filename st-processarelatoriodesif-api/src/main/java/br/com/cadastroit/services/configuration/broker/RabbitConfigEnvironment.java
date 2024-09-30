package br.com.cadastroit.services.configuration.broker;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import br.com.cadastroit.services.configuration.CstSolicitaRelatorioDesifServicesConfiguration;

@Primary
@EnableJpaRepositories
@Configuration
public class RabbitConfigEnvironment {

	private final String ERROR = "Erro no setup do mensageiro, erro => ?";
	private Logger log = Logger.getLogger(CstSolicitaRelatorioDesifServicesConfiguration.class);
	private CstSolicitaRelatorioDesifServicesConfiguration rabbitBean;

	@Bean
	public CstSolicitaRelatorioDesifServicesConfiguration rabbitConfigBean() throws Exception {
		try {
			if (this.rabbitBean == null) {
				this.rabbitBean = new CstSolicitaRelatorioDesifServicesConfiguration();
				this.rabbitBean.createDomainsSolicitaRelDesif();
			}
			return rabbitBean;
		} catch (Exception ex) {
            throw new Exception(String.format(ERROR, ex.getMessage()));

		}
	}
}
