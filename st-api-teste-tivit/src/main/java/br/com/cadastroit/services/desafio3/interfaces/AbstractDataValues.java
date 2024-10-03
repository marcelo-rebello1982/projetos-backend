package br.com.cadastroit.services.desafio3.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public abstract class AbstractDataValues implements Monthly {
	
	protected Random random = new Random();

	@Override
	public  List<Map<String, List<Double>>> getMonthValues(String day, List<Double> values) {
		
		TreeMap<String, List<Double>> dias = getDayValues(day,values);
		return dias.entrySet().stream()
				.map(entry -> Map.of(entry.getKey(), entry.getValue()))
				.toList();
	}
	
	protected List<Double> generateValues(int size) {
		Random random = new Random();
		return random.doubles(size, 10.0, 100.0)
				.map(value -> Math.round(value * 100.0) / 100.0) 
				.boxed()
				.toList();
	}

	
	protected abstract TreeMap<String, List<Double>> getDayValues(String day, List<Double> values);

}