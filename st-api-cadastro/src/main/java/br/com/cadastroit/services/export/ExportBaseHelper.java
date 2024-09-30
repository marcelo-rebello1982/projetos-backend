package br.com.cadastroit.services.export;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class ExportBaseHelper {

	protected static void sort(List<ExportColumnDTO> exportColumns) {

		if (exportColumns.stream().anyMatch(c -> c.getOrder() > 0)) {
			exportColumns = exportColumns.stream().sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder())).collect(Collectors.toList());
		}

	}

	protected static Stream<AccessibleObject> getFieldsMethodsRecursive(Class<?> type) {

		Stream<AccessibleObject> stream = Stream.concat(Stream.of(type.getDeclaredFields()), Stream.of(type.getDeclaredMethods()));

		if (type.getSuperclass() != Object.class) {

			Stream<AccessibleObject> sSuper = getFieldsMethodsRecursive(type.getSuperclass());
			stream = Stream.concat(stream, sSuper);

		}

		return stream;

	}

	protected static ArrayList<Integer> intersection(List<List<Integer>> inputArrays) {

		HashSet<Integer> intersectionSet = new HashSet<>(inputArrays.get(0));

		for (int i = 1; i < inputArrays.size(); i++) {
			HashSet<Integer> set = new HashSet<>(inputArrays.get(i));
			intersectionSet.retainAll(set);
		}

		return new ArrayList<>(intersectionSet);
	}

}
