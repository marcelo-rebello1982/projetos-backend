package br.com.cadastroit.services.desafio3.meses;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.cadastroit.services.desafio3.interfaces.AbstractDataValues;

public class Janeiro extends AbstractDataValues {
	
	public Janeiro() {
		super();
	}

	@Override
	protected TreeMap<String, List<Double>> getDayValues(String mes, List<Double> values) {

		TreeMap<String, List<Double>> value = new TreeMap<>();
		value.put(mes, values);
		
		return value;
	}
	
	public Map<String, List<Double>> valuesPerDay() {

	    return Map.ofEntries(
	            Map.entry("01", List.of(68.27, 15.87, 29.13, 52.35, 55.87, 25.63, 88.41, 62.54)),
	            Map.entry("02", List.of(59.75 , 12.11, 19.71, 31.99, 58.78, 11.25, 32.47)),
	            Map.entry("03", List.of(11.35, 17.44, 30.28, 55.57, 13.31, 55.21, 47.63, 21.45, 18.88, 19.24, 21.45)),
	            Map.entry("04", List.of(13.01, 20.66, 34.13, 61.90, 17.44, 38.28, 55.57)),
	            Map.entry("05", List.of(14.22, 21.49, 76.12, 62.20, 34.13, 17.44)),
	            Map.entry("06", List.of(12.13, 16.88)),
	            Map.entry("07", List.of(10.88, 27.45)),
	            Map.entry("08", List.of(13.75, 47.63, 21.45, 18.88, 18.32, 31.91, 59.08)),
	            Map.entry("09", List.of(81.65, 19.22, 334.03, 57.55)),
	            Map.entry("10", List.of(74.01, 20.87, 38.28, 63.56)),
	            Map.entry("11", List.of(172.56, 21.00, 39.65, 78.99, 35.33, 61.13)),
	            Map.entry("12", List.of(10.10, 16.78, 30.77, 54.01)),
	            Map.entry("13", List.of(13.99, 21.00, 39.65, 62.23, 102.01)),
	            Map.entry("14", List.of(11.47, 17.87)),
	            Map.entry("15", List.of(15.56, 19.88, 16.88, 28.35, 56.78, 85.09)),
	            Map.entry("16", List.of(12.89, 20.56, 34.35, 62.85)),
	            Map.entry("17", List.of(110.24, 18.57, 30.99, 47.63, 21.45, 57.78)),
	            Map.entry("18", List.of(13.68, 22.45, 38.55, 64.13)),
	            Map.entry("19", List.of(11.98, 19.77, 33.45, 59.35)),
	            Map.entry("20", List.of(14.91, 21.33, 37.88, 66.09)),
	            Map.entry("21", List.of(12.01, 18.67, 30.78, 58.12)),
	            Map.entry("22", List.of(10.67, 380.99, 47.63, 71.45, 16.56, 29.45, 55.78)),
	            Map.entry("23", List.of(13.23, 20.78, 35.56, 91.47, 17.87, 33.24)),
	            Map.entry("24", List.of(11.45, 19.33, 32.78, 59.78, 61.90, 17.44, 30.28, 17.55)),
	            Map.entry("25", List.of(14.13, 19.88, 36.13, 62.46)),
	            Map.entry("26", List.of(12.78, 21.23, 33.90, 64.45)),
	            Map.entry("27", List.of(10.90, 17.45, 30.46, 19.33, 32.78, 56.99)),
	            Map.entry("28", List.of(13.35, 22.01, 38.11, 64.79)),
	            Map.entry("29", List.of(130.46, 19.33, 51.98, 19.78, 34.89, 60.35)),
	            Map.entry("30", List.of(6.45, 5.47)),
	            Map.entry("31", List.of(15.07, 8.54))

	    );
	}
}
