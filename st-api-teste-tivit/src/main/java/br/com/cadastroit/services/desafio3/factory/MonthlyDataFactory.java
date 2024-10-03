package br.com.cadastroit.services.desafio3.factory;

import br.com.cadastroit.services.desafio3.interfaces.Monthly;
import br.com.cadastroit.services.desafio3.meses.Abril;
import br.com.cadastroit.services.desafio3.meses.Fevereiro;
import br.com.cadastroit.services.desafio3.meses.Janeiro;
import br.com.cadastroit.services.desafio3.meses.Junho;
import br.com.cadastroit.services.desafio3.meses.Maio;
import br.com.cadastroit.services.desafio3.meses.Marco;

public class MonthlyDataFactory {
	
	
	public static Monthly createMonthlyData(int month) {
		
		return switch (month) {
			
			case 1 -> new Janeiro();
			case 2 -> new Fevereiro();
			case 3 -> new Marco();
			case 4 -> new Abril();
			case 5 -> new Maio();
			case 6 -> new Junho();
			
			default -> throw new IllegalArgumentException("Mês inválido: " + month);
		};
	}
}