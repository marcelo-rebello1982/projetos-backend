package br.com.cadastroit.services.desafio3.interfaces;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyImpl implements Monthly {
	
    private String name;
    
	@Override
	public List<Map<String, List<Double>>> getMonthValues(String day, List<Double> values) {
		return null;
	}
}
