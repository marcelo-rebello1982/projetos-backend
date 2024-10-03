package br.com.cadastroit.services.desafio3.interfaces;

import java.util.List;
import java.util.Map;

public interface Monthly {

	/**
	 * Obtém uma lista de mapas que associam nomes de meses a listas de valores diários.
	 *
	 * @return uma lista de mapas, onde cada mapa contém o nome do mês associado a uma lista de valores.
	 */
	List<Map<String, List<Double>>> getMonthValues(String day, List<Double> values);
	
}