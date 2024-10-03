package br.com.cadastroit.services.api.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.cadastroit.services.desafio3.interfaces.Monthly;
import br.com.cadastroit.services.desafio3.interfaces.MonthlyImpl;
import br.com.cadastroit.services.desafio3.meses.Abril;
import br.com.cadastroit.services.desafio3.meses.Agosto;
import br.com.cadastroit.services.desafio3.meses.Dezembro;
import br.com.cadastroit.services.desafio3.meses.Fevereiro;
import br.com.cadastroit.services.desafio3.meses.Janeiro;
import br.com.cadastroit.services.desafio3.meses.Julho;
import br.com.cadastroit.services.desafio3.meses.Junho;
import br.com.cadastroit.services.desafio3.meses.Maio;
import br.com.cadastroit.services.desafio3.meses.Marco;
import br.com.cadastroit.services.desafio3.meses.Novembro;
import br.com.cadastroit.services.desafio3.meses.Outubro;
import br.com.cadastroit.services.desafio3.meses.Setembro;
import lombok.Getter;

@Service
@Getter
public class DesafioService {
	
	
	private Map<String, Map<String, List<Double>>> finalMap = new HashMap<String, Map<String,List<Double>>>();
	
    private static AtomicReference<List<Double>> allValues = new AtomicReference<>(new ArrayList<>());
    
	public Map<String, List<Double>> processMonthlyRevenue(MonthlyImpl nameMonthly) {

		Map<String, List<Double>> revenueData = this.processData(getMonthly(nameMonthly.getName()));

		revenueData.forEach((dia, valores) -> System.out.println("Mês " + nameMonthly.getName() + " " + dia + " : " + valores));

		return returnMinMaxPerDay(nameMonthly, getMonthly(nameMonthly.getName()));
	}
    
    public Map<String, Map<String, List<Double>>> processTotalRevenue(MonthlyImpl nameMonthly) {
    	
    	Map<String, List<Double>> monthlyRevenueData = this.processData(getMonthly(nameMonthly.getName()));
    	
    	finalMap.put(nameMonthly.getName(), monthlyRevenueData);
    	
    	return finalMap;

    }
    
    public Long processDaysAverageAnnualMedia_DEPRECATED (Map<String, Map<String, List<Double>>> value) {

    	double sumtotalDay = 0;
    	int totalDaysMounth = 0;

    	for (Map<String, List<Double>> month : value.values()) {
    		for (List<Double> dayValues : month.values()) {
    			for (Double dayValue : dayValues) {
    				sumtotalDay += dayValue;
    				totalDaysMounth = month.size();
    			}
    		}
    	}

    	double sumTotalRound = roundValue(sumtotalDay);
		double annualAverage = roundValue(sumTotalRound / totalDaysMounth);
    	long daysAboveAverage = 0;

    	for (Map<String, List<Double>> month : value.values()) {
    		for (List<Double> dayValues : month.values()) {
    			double daysSum = dayValues.stream().mapToDouble(Double::doubleValue).sum();
    			if (daysSum > annualAverage) {
    				daysAboveAverage++;
    			}
    		}
    	}

    	System.out.println("Número de dias onde faturamento diário foi superior à média anual. : " + daysAboveAverage);

    	return daysAboveAverage;

    }
    
    public Long processDaysAverageAnnualMedia(Map<String, Map<String, List<Double>>> value) {

    	Long dayRevenueAboveAverage = 0L;
    	Map<String, Long> resultMap = new HashMap<>();

    	for (Map.Entry<String, Map<String, List<Double>>> entry : value.entrySet()) {

    		Map<String, List<Double>> dailyRevenues = entry.getValue();

    		double totalSum = 0.0;
    		int totalSales = 0;

    		totalSum = dailyRevenues.values().stream()
    				.flatMap(List::stream)  
    				.mapToDouble(Double::doubleValue) 
    				.sum();

    		totalSales = dailyRevenues.values().stream()
    				.mapToInt(List::size)
    				.sum();

    		double average = totalSum / totalSales;

    		for (List<Double> revenues : dailyRevenues.values()) {
    			for (double revenue :  revenues) {
    				if (revenue > roundValue(average)) {
    					dayRevenueAboveAverage++;
    					break;
    				}
    			}
    		}

    		resultMap.put(entry.getKey(), dayRevenueAboveAverage);
    	}

    	System.out.println("Número de dias onde faturamento diário foi superior à média anual. : " + dayRevenueAboveAverage);

    	return dayRevenueAboveAverage;
    }
    
    public Map<String, Integer> processDaysAverageAnnualMediaDEPRECATED(Map<String, Map<String, List<Double>>> value) {

    	Map<String, Integer> result = new HashMap<>();

    	for (Map.Entry<String, Map<String, List<Double>>> entry : value.entrySet()) {

    		String month = entry.getKey();
    		Map<String, List<Double>> dailyRevenues = entry.getValue();

    		double total = 0.0;
    		int count = 0;

    		for (List<Double> revenues : dailyRevenues.values()) {
    			for (double revenue : revenues) {
    				total += revenue;
    				count++;
    			}
    		}

    		double average = total / count;
    		int daysAboveAverage = 0;

    		for (List<Double> revenues : dailyRevenues.values()) {
    			for (double revenue : revenues) {
    				if (revenue > average) {
    					daysAboveAverage++;
    					break;
    				}
    			}
    		}

    		result.put(month, daysAboveAverage);
    	}

    	return result;
    }
    
	
	private static Map<String,List<Double>> returnMinMaxPerDay (Monthly month, Map<String, List<Double>> valuesPerDay) {

		valuesPerDay.entrySet().stream().forEach( values -> {
			values.getValue().stream().forEach( value -> {
				getAllValues().get().add(value);
			});
		});
		
		returnMinMax(month);
				
		return valuesPerDay;
		
	}
	

	
	private static void returnMinMax(Monthly month) {

		System.out.println("Menor(1) valor do mês de : " + ((MonthlyImpl) month).getName() + " : " + Collections.min(getAllValues().get()));
		System.out.println("Maior(1) valor do mês de : " + ((MonthlyImpl) month).getName() + " : " + Collections.max(getAllValues().get()));

	}
	
	 //	service.processMaxMonthlyValue(totalAnnualRevenue);


	public void processMaxMinMonthlyValue (Map<String, Map<String, List<Double>>> value) {
    	this.getHighLowSaleByMonth(value , 0.0);
    }
    
    public Map<String, Double[]> getHighLowSaleByMonth(Map<String, Map<String, List<Double>>> value , Double defaultValue) {

		Double[] minMaxDay = new Double[2];
    	Map<String, Double[]> mapHighSale = new HashMap<>();

    	value.forEach((month, days) -> {

    		double maxValue = days.values().stream()
    				.flatMap(List::stream)
    				.mapToDouble(Double::doubleValue)
    				.max()
    				.orElse(defaultValue); // default value Round double
    		
    		double minValue = days.values().stream()
    				.flatMap(List::stream)
    				.mapToDouble(Double::doubleValue)
    				.min()
    				.orElse(defaultValue); // default value Round double
    		
    		minMaxDay[0] = maxValue;
    		minMaxDay[1] = minValue;
    		
    		mapHighSale.put(month, minMaxDay);
    	});

    	mapHighSale.forEach((month, minMaxValue) ->

    	
    	System.out.println("Maior(3) valor do mês de : " + month + " : " + minMaxValue[0] + 
    					 "\nMenor(3) valor do mês de : " + month + " : " + minMaxValue[1] ));

    	return mapHighSale;
    }
    
	public Map<String, List<Double>> getDaysAnnualAverage(Map<String, Map<String, List<Double>>> value) {

		List<Double> swapOnly = new ArrayList<Double>();
		AtomicReference<String> month = new AtomicReference<>();
		AtomicReference<List<Double>> valuesPerDay = new AtomicReference<>(new ArrayList<>());
		Map<String, List<Double>> mapHighSaleByMonth = new HashMap<String, List<Double>>();

		for (Map.Entry<String, Map<String, List<Double>>> entry : value.entrySet()) {

			entry.getValue().entrySet().stream().forEach(obj -> {
				swapOnly.addAll(obj.getValue());
				month.set(entry.getKey());
			});
			
		}
		
		valuesPerDay.get().add(Math.round(calculateMedia(swapOnly) * 100.0) / 100.0);
		mapHighSaleByMonth.put(month.get(), valuesPerDay.get());

		return mapHighSaleByMonth;
	}

	private Map<String, List<Double>> processData(Map<String, List<Double>> value) {

		List<Double> valuesPerDay = new ArrayList<Double>();
		Map<String, List<Double>> values = new HashMap<String, List<Double>>();

		for (Map.Entry<String, List<Double>> entry : value.entrySet()) {

			String day = entry.getKey();

			if (entry.getValue() != null) {
				valuesPerDay = entry.getValue();
			} 
			values.put(day, valuesPerDay);
		}

		return values;
	}

	private Map<String, List<Double>> sumValuesFromMaps(List<Map<String, Double>> maps) {

		return maps.stream()
				.filter(map -> map != null && !map.isEmpty())
				.collect(Collectors.toMap(map -> map.keySet().iterator().next(), map -> map.values().stream().collect(Collectors.toList()),
						(obj1, obj2) -> {
							obj1.addAll(obj2);
							return obj1;
						}));
	}
	
	private List<Map.Entry<String, List<Double>>> sumAllValues(Map<String, List<Double>> obj) {
		return obj.entrySet().stream()
				.collect(Collectors.toList());
	}
	
	private Map<String, Long> sumAllValues(List<Map<String, Long>> maps) {
		return maps.stream()
				.filter(map -> map != null && !map.isEmpty())
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.groupingBy(
						Map.Entry::getKey,
						Collectors.summingLong(Map.Entry::getValue)
						));
		//Map<String, Long> getTotalValues = getKeysAndCounts(Arrays.asList(sumQtdProcess));
		//Long count = getTotalValues.values().stream().flatMap(List::stream).mapToLong(Long::longValue).sum();   
	}
	
	
	
	private Double calculateMedia(List<Double> valuesSwap) {
		return valuesSwap.stream().mapToDouble(Double::doubleValue).sum() / valuesSwap.size();
	}
	
	
	private static Map<String,List<Double>> getMonthly(String month) {

		return switch (month) {

			case "Janeiro" -> createMonthlyData(month);
			case "Fevereiro" -> createMonthlyData(month);
			case "Marco" -> createMonthlyData(month);
			case "Abril" -> createMonthlyData(month);
			case "Maio" -> createMonthlyData(month);
			case "Junho" -> createMonthlyData(month);
			case "Julho" -> createMonthlyData(month);
			case "Agosto" -> createMonthlyData(month);
			case "Setembro" -> createMonthlyData(month);
			case "Outubro" -> createMonthlyData(month);
			case "Novembro" -> createMonthlyData(month);
			case "Dezembro" -> createMonthlyData(month);

			default -> throw new IllegalArgumentException("Mês inválido: " + month);

		};

	}

	private static Map<String, List<Double>> createMonthlyData(String month) {

		return switch (month) {

			case "Janeiro" -> new Janeiro().valuesPerDay();
			case "Fevereiro" -> new Fevereiro().valuesPerDay();
			case "Marco" -> new Marco().valuesPerDay();
			case "Abril" -> new Abril().valuesPerDay();
			case "Maio" -> new Maio().valuesPerDay();
			case "Junho" -> new Junho().valuesPerDay();
			case "Julho" -> new Julho().valuesPerDay();
			case "Agosto" -> new Agosto().valuesPerDay();
			case "Setembro" -> new Setembro().valuesPerDay();
			case "Outubro" -> new Outubro().valuesPerDay();
			case "Novembro" -> new Novembro().valuesPerDay();
			case "Dezembro" -> new Dezembro().valuesPerDay();

			default -> throw new IllegalArgumentException("Mês inválido: " + month);
		};
		
	}
	
	private double roundValue(double sumtotalDay) {
		return Math.round(sumtotalDay * 100.0) / 100.0;
	}

	private static AtomicReference<List<Double>> getAllValues() {
		return allValues;
	}

}
