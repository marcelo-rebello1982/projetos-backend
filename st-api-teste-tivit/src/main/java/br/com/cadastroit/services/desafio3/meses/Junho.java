package br.com.cadastroit.services.desafio3.meses;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.cadastroit.services.desafio3.interfaces.AbstractDataValues;

public class Junho extends AbstractDataValues {


	@Override
	protected TreeMap<String, List<Double>> getDayValues(String mes, List<Double> values) {

		TreeMap<String, List<Double>> value = new TreeMap<>();
		value.put(mes, values);
		
		return value;
	}
	
	public Map<String, List<Double>> valuesPerDay() {

		return Map.ofEntries(
				Map.entry("01", List.of(11.34, 16.78, 29.56, 53.45)),
				Map.entry("02", List.of(12.90, 19.67, 31.89, 58.12)),
				Map.entry("03", List.of(10.47, 17.34, 30.12, 55.67)),
				Map.entry("04", List.of(13.78, 20.12, 34.78, 60.23)),
				Map.entry("05", List.of(14.56, 21.45, 36.45, 62.34)),
				Map.entry("06", List.of(12.12, 15.89, 28.90, 54.67)),
				Map.entry("07", List.of(15.90, 22.34, 37.12, 66.01)),
				Map.entry("08", List.of(13.45, 18.76, 32.45, 59.34)),
				Map.entry("09", List.of()),
				Map.entry("10", List.of(14.78, 20.89, 38.12, 63.45)),
				Map.entry("11", List.of(12.56, 18.34, 35.12, 61.78)),
				Map.entry("12", List.of(10.89, 16.90, 30.78, 54.12)),
				Map.entry("13", List.of(13.78, 21.34, 39.45, 62.90)),
				Map.entry("14", List.of(11.45, 17.67, 33.90, 58.45)),
				Map.entry("15", List.of(15.12, 19.78, 36.78, 65.34)),
				Map.entry("16", List.of(12.34, 20.90, 34.12, 62.23)),
				Map.entry("17", List.of(10.56, 18.90, 30.45, 57.12)),
				Map.entry("18", List.of(13.89, 22.12, 38.90, 64.78)),
				Map.entry("19", List.of(11.10, 19.23, 33.45, 59.34)),
				Map.entry("20", List.of(14.67, 21.12, 37.90, 66.12)),
				Map.entry("21", List.of(12.34, 18.56, 31.27, 58.90)),
				Map.entry("22", List.of(10.90, 16.78, 29.90, 55.67)),
				Map.entry("23", List.of(13.45, 20.34, 35.11, 61.90)),
				Map.entry("24", List.of(11.80, 19.67, 32.45, 59.12)),
				Map.entry("25", List.of(14.90, 19.12, 36.99, 62.34)),
				Map.entry("26", List.of(12.67, 21.45, 33.78, 64.12)),
				Map.entry("27", List.of(10.12, 17.90, 30.67, 56.34)),
				Map.entry("28", List.of(13.34, 22.90, 38.45, 63.45)),
				Map.entry("29", List.of(11.67, 19.12, 34.78, 60.78)),
				Map.entry("30", List.of(14.23, 20.34, 39.12, 67.89)),
				Map.entry("31", List.of(12.90, 18.56, 35.45, 62.34))
				);
		}
}
