package br.com.complianceit.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cadastroit.services.controller.DatabaseController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CstBootViewsApplicationTests {

	@Test
	public void buildCountQueryString() throws Exception {
		String sql = "select cpf_cnpj_emit, cod_mod, serie, nro_ct, cfop from VW_CSF_CONHEC_TRANSP";
		DatabaseController databaseController = new DatabaseController();
		String value = databaseController.buildCountQueryString(sql);
		System.out.println("Value = " + value);

		sql = "select cpf_cnpj_emit, cod_mod, serie, nro_ct, cfop from VW_CSF_CONHEC_TRANSP where CPF_CNPJ_EMIT = '43854116006484'";
		value = databaseController.buildCountQueryString(sql);
		System.out.println("Value = " + value);

		sql = "select VW.cpf_cnpj_emit, VW.cod_mod, VW.serie, VW.nro_ct, VW.cfop, VWP.CPF_CNPJ"
				+ "  from VW_CSF_CONHEC_TRANSP VW" + "  inner join VW_CSF_PESSOA VWP on VWP.CPF_CNPJ = VW.CPF_CNPJ_EMIT"
				+ "  where VW.CPF_CNPJ_EMIT = '43854116006484'";
		value = databaseController.buildCountQueryString(sql);
		System.out.println("Value = " + value);

		sql = "select VW.cpf_cnpj_emit, VW.cod_mod, VW.serie, VW.nro_ct, VW.cfop, VWP.CPF_CNPJ"
				+ "  from VW_CSF_CONHEC_TRANSP VW, VW_CSF_PESSOA VWP" + "  where VW.CPF_CNPJ_EMIT = VWP.CPF_CNPJ AND"
				+ "        VW.CPF_CNPJ_EMIT = '43854116006484'";
		value = databaseController.buildCountQueryString(sql);
		System.out.println("Value = " + value);
	}

}
