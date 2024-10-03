package br.com.cadastroit.services.desafio3.meses;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.cadastroit.services.desafio3.interfaces.AbstractDataValues;

public class Setembro extends AbstractDataValues {


	@Override
	protected TreeMap<String, List<Double>> getDayValues(String mes, List<Double> values) {

		TreeMap<String, List<Double>> value = new TreeMap<>();
		value.put(mes, values);
		
		return value;
	}
	
	public Map<String, List<Double>> valuesPerDay() {

		return Map.ofEntries(
				Map.entry("01", List.of(10.45, 15.89, 28.34, 55.12)),
				Map.entry("02", List.of(12.67, 18.90, 30.45, 57.34)),
				Map.entry("03", List.of(11.12, 16.78, 29.56, 54.78)),
				Map.entry("04", List.of(13.56, 21.23, 34.67, 62.11)),
				Map.entry("05", List.of(14.78, 19.45, 35.89, 63.45)),
				Map.entry("06", List.of(12.34, 17.12, 28.90, 56.90)),
				Map.entry("07", List.of()),
				Map.entry("08", List.of(13.45, 18.12, 35.78, 59.89)),
				Map.entry("09", List.of(10.90, 19.67, 33.12, 58.34)),
				Map.entry("10", List.of(14.12, 20.90, 38.45, 64.78)),
				Map.entry("11", List.of(12.56, 18.34, 34.12, 61.23)),
				Map.entry("12", List.of(11.34, 17.67, 30.89, 55.67)),
				Map.entry("13", List.of(13.90, 21.12, 39.76, 62.90)),
				Map.entry("14", List.of(11.89, 19.67, 33.45, 59.45)),
				Map.entry("15", List.of(14.45, 20.12, 36.78, 65.34)),
				Map.entry("16", List.of(12.34, 18.90, 34.56, 63.12)),
				Map.entry("17", List.of(10.12, 17.78, 30.45, 57.89)),
				Map.entry("18", List.of(13.67, 22.34, 38.90, 64.67)),
				Map.entry("19", List.of(11.90, 19.12, 33.67, 60.12)),
				Map.entry("20", List.of(14.90, 21.56, 37.45, 66.90)),
				Map.entry("21", List.of(12.34, 18.23, 31.27, 58.34)),
				Map.entry("22", List.of(10.78, 16.90, 29.45, 55.12)),
				Map.entry("23", List.of(13.12, 20.90, 35.34, 61.34)),
				Map.entry("24", List.of(11.45, 19.78, 32.89, 59.90)),
				Map.entry("25", List.of(14.67, 20.34, 36.78, 62.34)),
				Map.entry("26", List.of(12.90, 21.23, 33.12, 64.12)),
				Map.entry("27", List.of(10.90, 17.89, 30.67, 56.78)),
				Map.entry("28", List.of(13.45, 22.90, 39.12, 63.45)),
				Map.entry("29", List.of(11.67, 19.34, 34.90, 60.67)),
				Map.entry("30", List.of(14.12, 20.78, 38.45, 67.89)),
				Map.entry("31", List.of(12.90, 18.56, 35.12, 62.34))
				);
		}
}
