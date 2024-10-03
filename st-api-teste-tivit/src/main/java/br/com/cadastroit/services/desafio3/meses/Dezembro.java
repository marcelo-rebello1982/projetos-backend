package br.com.cadastroit.services.desafio3.meses;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.cadastroit.services.desafio3.interfaces.AbstractDataValues;

public class Dezembro extends AbstractDataValues {


	@Override
	protected TreeMap<String, List<Double>> getDayValues(String mes, List<Double> values) {

		TreeMap<String, List<Double>> value = new TreeMap<>();
		value.put(mes, values);
		
		return value;
	}
	
	public Map<String, List<Double>> valuesPerDay() {

		return Map.ofEntries(
				Map.entry("01", List.of(9.75, 15.78, 28.56, 54.90)),
				Map.entry("02", List.of(12.67, 19.12, 30.45, 58.34 ,11.47, 17.87, 33.20)),
				Map.entry("03", List.of(11.56, 16.89, 29.78, 55.12)),
				Map.entry("04", List.of(13.45, 20.34)),
				Map.entry("05", List.of(14.90, 21.45, 36.78, 62.12, 16.45, 29.90, 55.67, 20.34, 34.56)),
				Map.entry("06", List.of(12.23, 17.90, 28.45, 18.56, 31.79, 58.97,18.56, 31.79, 58.97,56.34)),
				Map.entry("07", List.of(15.67, 22.12, 37.89, 66.45)),
				Map.entry("08", List.of(13.34, 18.90, 32.67, 59.12)),
				Map.entry("09", List.of(11.00, 19.34, 33.45, 18.56, 31.79, 58.97, 57.89)),
				Map.entry("10", List.of(14.78, 20.90, 38.67, 63.34)),
				Map.entry("11", List.of(12.34, 18.12, 35.90, 32.67, 59.12, 61.12)),
				Map.entry("12", List.of(10.90, 16.78)),
				Map.entry("13", List.of(13.45, 22.12, 37.89, 66.45, 21.34, 39.12, 62.78)),
				Map.entry("14", List.of(11.89, 19.67, 31.79, 21.34, 39.12, 62.78, 33.90, 58.34)),
				Map.entry("15", List.of(15.12, 19.78, 60.67, 19.78, 34.90, 60.67 ,19.90, 36.45, 65.12)),
				Map.entry("16", List.of(12.78, 20.78, 34.12, 62.90, 30.45, 58.34 ,11.47)),
				Map.entry("17", List.of(10.34, 18.90, 30.45, 57.12)),
				Map.entry("18", List.of(13.67, 22.45, 38.90, 12.94, 21.34, 64.67)),
				Map.entry("19", List.of(11.12, 19.92, 33.67, 59.34, 19.67, 31.79, 21.34)),
				Map.entry("20", List.of(14.90, 21.78)),
				Map.entry("21", List.of(12.45, 18.56, 31.79, 21.34, 39.12, 62.78, 58.97)),
				Map.entry("22", List.of(10.90, 16.45, 29.90, 55.67, 16.45, 29.90, 55.67, 55.67, 16.45, 29.90)),
				Map.entry("23", List.of(13.90, 20.34, 35.67, 61.12)),
				Map.entry("24", List.of(11.34, 19.12)),
				Map.entry("25", List.of()),
				Map.entry("26", List.of(12.94, 21.34, 33.45, 64.78)),
				Map.entry("27", List.of(10.12, 21.78, 37.12, 66.34, 17.90, 30.90, 56.90)),
				Map.entry("28", List.of(13.34, 19.94)),
				Map.entry("29", List.of(11.67, 19.78, 34.90, 60.67)),
				Map.entry("30", List.of(14.12, 20.34,12.23, 17.90, 28.45, 39.12, 67.12)),
				Map.entry("31", List.of())
		);
	}
}
