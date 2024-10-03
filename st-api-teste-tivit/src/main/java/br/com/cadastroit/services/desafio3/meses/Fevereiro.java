package br.com.cadastroit.services.desafio3.meses;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import br.com.cadastroit.services.desafio3.interfaces.AbstractDataValues;

public class Fevereiro extends AbstractDataValues {

	@Override
	protected TreeMap<String, List<Double>> getDayValues(String mes, List<Double> values) {

		TreeMap<String, List<Double>> value = new TreeMap<>();
		value.put(mes, values);
		
		return value;
	}
	
	public Map<String, List<Double>> valuesPerDay() {
		return Map.ofEntries(
				Map.entry("01", generateValues(1 + random.nextInt(7))),
				Map.entry("02", generateValues(3 + random.nextInt(7))),
				Map.entry("03", generateValues(4 + random.nextInt(7))),
				Map.entry("04", generateValues(5 + random.nextInt(7))),
				Map.entry("05", generateValues(1 + random.nextInt(7))),
				Map.entry("06", generateValues(4 + random.nextInt(7))),
				Map.entry("07", generateValues(3 + random.nextInt(7))),
				Map.entry("08", generateValues(5 + random.nextInt(7))),
				Map.entry("09", generateValues(4 + random.nextInt(7))),
				Map.entry("10", generateValues(1 + random.nextInt(7))),
				Map.entry("11", generateValues(1 + random.nextInt(7))),
				Map.entry("12", generateValues(3 + random.nextInt(7))),
				Map.entry("13", generateValues(4 + random.nextInt(7))),
				Map.entry("14", generateValues(4 + random.nextInt(7))),
				Map.entry("15", generateValues(4 + random.nextInt(7))),
				Map.entry("16", generateValues(4 + random.nextInt(7))),
				Map.entry("17", generateValues(4 + random.nextInt(7))),
				Map.entry("18", generateValues(4 + random.nextInt(7))),
				Map.entry("19", generateValues(4 + random.nextInt(7))),
				Map.entry("20", generateValues(4 + random.nextInt(7))),
				Map.entry("21", generateValues(4 + random.nextInt(7))),
				Map.entry("22", generateValues(4 + random.nextInt(7))),
				Map.entry("23", generateValues(4 + random.nextInt(7))),
				Map.entry("24", generateValues(4 + random.nextInt(7))),
				Map.entry("25", generateValues(4 + random.nextInt(7))),
				Map.entry("26", generateValues(4 + random.nextInt(7))),
				Map.entry("27", generateValues(4 + random.nextInt(7))),
				Map.entry("28", generateValues(4 + random.nextInt(7)))
				);
		}
}
