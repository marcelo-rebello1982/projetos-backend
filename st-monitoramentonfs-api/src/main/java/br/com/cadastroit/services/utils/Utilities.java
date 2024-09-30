package br.com.cadastroit.services.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.cadastroit.services.commons.api.ViewCidadeEmpresa;
import br.com.cadastroit.services.commons.api.ViewTotalEstado;
import br.com.cadastroit.services.commons.api.ViewTotalItemNfServ;
import br.com.cadastroit.services.web.dto.Filters;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
@Getter
public class Utilities {

    protected final Function<Integer[], String> toInt = String::valueOf;

    public static String createQuery(StringBuilder sBuilder, int length) {
        return new StringBuilder()
                .append(sBuilder)
                .append("(")
                .append(String.join(",", Collections.nCopies(length, "?")))
                .append(")").toString();
    }
    
    public StringBuilder createQuery(Boolean rowCount, String fields, String table, LinkedHashMap<String, String> campoValorCond) {
        StringBuilder sb = new StringBuilder();
        sb.append(rowCount ? "SELECT COUNT ( NFSERV_ID ) FROM " : "SELECT ").append(fields)
                .append(table).append(" V");
        if (!campoValorCond.isEmpty()) {
            String cond = campoValorCond.entrySet()
                    .stream()
                    .map(entry -> entry.getKey().toUpperCase() + entry.getValue())
                    .collect(Collectors.joining(" AND ", " WHERE ", ""));
            sb.append(cond);
        }
        return sb;
    }

	public StringBuilder createQueryDEPRECATEd(Boolean rowCount, String fields, String table,
			LinkedHashMap<String, String> campoValorCond ) {
		StringBuilder sb = new StringBuilder();
		sb.append(rowCount ? "SELECT COUNT ( NFSERV_ID ) FROM " : "SELECT ").append(fields).append(table).append(" V");

		if (!campoValorCond.isEmpty()) {
			StringJoiner joiner = new StringJoiner(" AND ", " WHERE ", "");
			campoValorCond.forEach((key, value) -> joiner.add(key.toUpperCase() + value));
			sb.append(joiner);
		}

		return sb;
	}
       
    public <T> Optional<T> checkIsNull(T field) {
        return Optional.ofNullable(field);
    }

    public static String montarQuery(StringBuilder sBuilder, int length) {
        String qry = sBuilder.toString();
        StringJoiner strJoiner = new StringJoiner(",", qry + "(", ")");
        for (int i = 0; i < length; i++) {
            strJoiner.add("?");
        }
        return strJoiner.toString();
    }
    
    public Set<String> getUfs(Map<String, List<String>> maps) {
		return new ArrayList<>(
				Arrays.asList(maps)).stream()
				.map(map -> new ArrayList<>(map.entrySet()))
				.flatMap(List::stream)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}
    
    public Map<String, List<String>> getQtdCidades(List<ViewTotalItemNfServ> list) {
		return list.stream()
				.collect(Collectors.partitioningBy(
						obj -> obj.getIbgeCidade() == null))
				.entrySet().stream()
				.filter(obj-> !obj.getKey())
				.collect(Collectors.toMap(obj -> "CODIBGE", 
						obj -> obj.getValue().stream()
						.map(ViewTotalItemNfServ::getIbgeCidade)
						.distinct()
						.collect(Collectors.toList())));
	}
    
    public Map<String, List<String>> getData(List<ViewTotalItemNfServ> list) {
		return list.stream() 
				.collect(Collectors.groupingBy(
						ViewTotalItemNfServ::getUf, 
						Collectors.mapping(
								ViewTotalItemNfServ::getDescrCidade,
								Collectors.toList())));
	}
    
    public Map<String, Long> getKeysAndCounts(List<Map<String, Long>> maps) {
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
    
    public Map<String, List<Long>> getKeysAndCountsDEPRECATED(List<Map<String, Long>> maps) {
    	return maps.stream()
    			.filter(map -> map != null && !map.isEmpty())
    			.flatMap(map -> map.entrySet().stream())
    			.collect(Collectors.groupingBy(
    					Map.Entry::getKey,
    					Collectors.mapping(
    							Map.Entry::getValue, Collectors.toList())
    					));
    }
    
    public Map<String, List<Long>> getKeysAndCountsDEPRECATED1(List<Map<String, Long>> maps) {
    	return maps.stream()
    		.filter(map -> map != null && !map.isEmpty())
    			.collect(Collectors.toMap(
    				map -> map.keySet().iterator().next(),
    				map -> map.values().stream().collect(Collectors.toList()),
    					(obj1, obj2) -> { // caso contrário duplicira a entrada na lista.
    						obj1.addAll(obj2);
    						return obj1;
    				}
    		));
    }
    
    public Map<String, List<String>> getKeysAndCount(Map<String, String> maps ) {

    	Map<String, List<String>> list = new HashMap<>();
    	Arrays.asList(maps).stream()
    	.filter(map -> map != null && !map.isEmpty())
    	.forEach(map -> {
    		list.put(map.keySet()
    				.stream().findFirst().get(),
    				map.values().stream()
    				.collect(Collectors.toList()));
    	});
    	return list;
    }

    public <T> Map<String, Double> groupByColumnAndSumValues(Stream<ViewTotalItemNfServ> stream,
                                                             Function<ViewTotalItemNfServ, T> groupBy) {
        return stream.collect(Collectors.groupingBy(obj -> obj.getMultOrgCd(),
                Collectors.summingDouble(obj -> ((Double) groupBy.apply(obj)))));
    }

    public <T> Map<String, Double> groupByColumnAndSumValues(List<ViewTotalItemNfServ> list,
                                                             Function<ViewTotalItemNfServ, T> groupBy) {
        return list.stream().collect(Collectors.groupingBy(obj -> obj.getMultOrgCd(),
                Collectors.summingDouble(obj -> ((Double) groupBy.apply(obj)))));
    }

    public List<Map.Entry<String, Double>> getValuesFromMap(Map<String, Double> obj) {
        return obj.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public <T, K> Map<K, List<T>> groupByColumn(List<T> list, Function<T, K> groupBy) {
        return list.stream().collect(Collectors.groupingBy(groupBy));
    }

    public <T> Map<String, List<ViewTotalEstado>> groupByColumnOld(List<ViewTotalEstado> list,
                                                                   Function<ViewTotalEstado, T> groupBy) {
        Map<String, List<ViewTotalEstado>> groupColumn = list.stream()
                .collect(Collectors.groupingBy(obj -> groupBy.apply(obj).toString()));
        return groupColumn;
    }

    public <T> Map<String, List<ViewCidadeEmpresa>> groupByColumnOld_(List<ViewCidadeEmpresa> list,
                                                                      Function<ViewCidadeEmpresa, T> groupBy) {
        Map<String, List<ViewCidadeEmpresa>> groupColumn = list.stream()
                .collect(Collectors.groupingBy(obj -> groupBy.apply(obj).toString()));
        return groupColumn;
    }

    // Closes the stream and releases any system resources associated with it.
    // If the stream has already been closed, invoking this method has no effect.
    public <T> Map<String, List<ViewTotalEstado>> groupByColumn(Stream<ViewTotalEstado> stream,
                                                                Function<ViewTotalEstado, T> groupBy) {
        Map<String, List<ViewTotalEstado>> groupColumn = stream
                .collect(Collectors.groupingBy(obj -> groupBy.apply(obj).toString()));
        return groupColumn;
    }

    public <T> Long getListSize(AtomicReference<List<T>> list) {
        return list.get().stream().distinct().count();
    }

    public Integer getListSize(String key, Map<String, List<ViewTotalEstado>> list) {
        return list.getOrDefault(key, Collections.emptyList()).size();
    }

    public String retornarValuesPorValorChave(int key, Map<Integer, String> map) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getKey() == key) {
                return entry.getValue();
            }
        }
        return null;
    }

    public <T, K> Map<K, Long> groupByAndSum(Stream<T> stream, Function<T, K> groupBy,
                                             List<ToLongFunction<T>> columns) {
        return stream.
                collect(Collectors.groupingBy(groupBy, Collectors.summingLong(
                        obj -> columns.stream()
                                .mapToLong(column -> column
                                        .applyAsLong(obj))
                                .sum())));
    }

    public List<ToLongFunction<ViewTotalEstado>> getListOfColumns() {
        List<ToLongFunction<ViewTotalEstado>> listFunctions = Arrays.asList(
                obj -> obj.getQtdTotalNotasAutoriz(), obj -> obj.getQtdTotalNotasProc(),
                obj -> obj.getQtdTotalNotasPend(), obj -> obj.getQtdTotalNotasCancel());
        return listFunctions;
    }

    public <K, V> V getValuesFromKeyMaps(K key, Map<K, V> obj) {
        return obj
                .containsKey(key) ? obj.get(key) : throwKeyNotFoundException();
    }

    private <V> V throwKeyNotFoundException() {
        throw new IllegalArgumentException("List is empty...");
    }

    public <T> Map<String, Long> groupByAndSumValues(List<Filters> list, Function<Filters, T> groupBy) {
        return list.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(obj -> obj.getMultOrgCd(),
                Collectors.reducing(0L, obj -> (Long) groupBy.apply(obj), Long::sum)));
        //Long sumQtdProcess_												 = listDto.stream()
    	//		.mapToLong(Filters::getQtdProcess)
    	//		.reduce(0L, Long::sum);
		//Map<String, Long> sumQtdProcess = groupByAndSumValues(listDto, obj -> obj.getQtdProcess());
    }
    
    // método collect() coleta os elementos libera a memória alocada pelo stream
    public Map<String, Long> sumValuesFromListMaps_(List<Map<String, Long>> list) {
        Map<String, Long> sum = list.stream().flatMap(obj -> obj.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
        return sum;
    }

    public Long sumValuesFromListMaps(List<Map<String, Long>> list) {
        return list.stream()
                .flatMap(map -> map.values().stream())
                .mapToLong(Long::longValue)
                .sum();
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
    	Class.forName(System.getenv("SPRING_DATASOURCE_DRIVER_CLASS_NAME"));
    	return DriverManager.getConnection(System.getenv("SPRING_DATASOURCE_URL"), 
    			System.getenv("SPRING_DATASOURCE_USERNAME"), System.getenv("SPRING_DATASOURCE_PASSWORD"));
    }
    
    public void putValuesInMap(Map<String, String> map, String key, String... values) {
        Optional.ofNullable(values).ifPresent(v ->
                map.put(key, String.join(", ", values)
                        .concat(")")));
    }

    public void putValuesInMap(Map<String, String> map, String key, Integer... values) {
        Optional.ofNullable(key).ifPresent(v ->
                map.put(key, String.join(", ", Arrays.stream(values)
                                .map(Object::toString)
                                .collect(Collectors.toList()))
                        .concat(")")));
    }

    public void putValuesInMap(Map<String, String> map, String key, String value) {
        Optional.ofNullable(key)
        .ifPresent(v -> map.put(value, !value.contains("BETWEEN") ? v + "'" : v));
    }

    // ajustar demais campos do createPredicates para uso do método abaixo LN 227
    public void putValuesInMapDeprecated(Map<String, String> map, String key, String[] values) {
        Optional.ofNullable(values)
                .ifPresent(v -> map.put(key, Arrays.stream(v)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .get().concat(")")));
    }

    public Long sumTotalValues(Long... params) {
    	return Arrays
    			.stream(params)
    			.filter(Objects::nonNull)
    			.mapToLong(Long::longValue)
    			.sum();
    }

    public void putValuesInMap(Map<String, String> map, Date date, String key, String value) {
        Optional.ofNullable(date)
                .map(r -> UtilDate.toDateString(r))  // Formata a data para uma string
                .ifPresent(val -> map.put(key, value));
    }

    public boolean isEmptyCollection(Object collection) {
        boolean result = true;
        if (collection instanceof Set<?>) {
            Set<?> copyCollection = (Set<?>) collection;
            result = (copyCollection == null || copyCollection.isEmpty()) ? true : false;
        } else if (collection instanceof List<?>) {
            List<?> copyCollection = (List<?>) collection;
            result = (copyCollection == null || copyCollection.isEmpty()) ? true : false;
        } else if (collection instanceof Collection<?>) {
            Collection<?> copyCollection = (Collection<?>) collection;
            result = (copyCollection == null || copyCollection.isEmpty()) ? true : false;
        }
        return result;
    }
}
