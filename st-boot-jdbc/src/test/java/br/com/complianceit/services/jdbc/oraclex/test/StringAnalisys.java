package br.com.complianceit.services.jdbc.oraclex.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.cadastroit.services.jdbc.oraclex.OracleBootJdbc;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OracleBootJdbc.class})
class StringAnalisys {

	@Test
	void analyseText() {
		String value 	= "As notas fiscais enviadas serão geradas e será notificado através do e-mail evandro.ribeiro@compliancefiscal.com.br quando o processamento finalizar. Para consultar as notas fiscais geradas, utilize o código de transmissão 1925";
		
		StringBuilder sbCodigo = new StringBuilder();
		char[] charV   = value.trim().toCharArray();
		
		boolean stop   = false;
		int count 	   = charV.length-1;
		while(count > 0) {
			if(charV[count] != ' ') {
				sbCodigo.append(charV[count]);
			} else {
				stop = true;
			}
			count --;
			if(stop) {
				break;
			}
		}
		String codigo = sbCodigo.reverse().toString();
		System.out.println("Codigo: "+codigo);
	}
	
}
