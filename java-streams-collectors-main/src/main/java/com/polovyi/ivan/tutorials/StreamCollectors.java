package com.polovyi.ivan.tutorials;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StreamCollectors {

    public static void main(String[] args) {
        System.out.println("toList method");
        List<Integer> example1 = Stream.of(1, 2, 3)
                .toList();
        // [1, 2, 3]
        System.out.println("example1 = " + example1);
        //  example1.add(4); -- throws UnsupportedOperationException

        System.out.println("toArray method");
        Object[] example2 = Stream.of(1, 2, 3)
                .toArray();
        // [1, 2, 3]
        System.out.println("example3 = " + Arrays.toString(example2));

        Integer[] example3 = Stream.of(1, 2, 3)
                .toArray(Integer[]::new);
        // [1, 2, 3]
        System.out.println("example3 = " + Arrays.toString(example3));

        int[] example4 = IntStream.of(1, 2, 3)
                .toArray();
        // [1, 2, 3]
        System.out.println("example4 = " + Arrays.toString(example4));

        long[] example5 = LongStream.of(1L, 2L, 3L)
                .toArray();
        // [1, 2, 3]
        System.out.println("example5 = " + Arrays.toString(example5));

        double[] example6 = DoubleStream.of(1.1, 2.2, 3.3)
                .toArray();
        // [1.1, 2.2, 3.3]
        System.out.println("example6 = " + Arrays.toString(example6));

        System.out.println("Collectors.toCollection()");
        ArrayList<Integer> example7 = Stream.of(1, 2, 3)
                .collect(Collectors.toCollection(ArrayList::new));
        // [1, 2, 3]
        System.out.println("example7 = " + example7);

        Set<Integer> example8 = Stream.of(1, 2, 3)
                .collect(Collectors.toCollection(HashSet::new));
        // [1, 2, 3]
        System.out.println("example8 = " + example8);

        ArrayList<Integer> example9List = new ArrayList<>();
        example9List.add(0);
        ArrayList<Integer> example9 = Stream.of(1, 2, 3)
                .collect(Collectors.toCollection(() -> example9List));
        // [0, 1, 2, 3]
        System.out.println("example9 = " + example9);

        System.out.println("Collectors.toList()");
        List<Integer> example10 = Stream.of(1, 2, 3)
                .collect(Collectors.toList());
        // [1, 2, 3]
        System.out.println("example10 = " + example10);

        List<Integer> example11 = Stream.of(1, 2, 3)
                .collect(Collectors.toUnmodifiableList());
        // [1, 2, 3]
        System.out.println("example11 = " + example11);

        System.out.println("Collectors.toSet()");
        Set<Integer> example12 = Stream.of(1, 2, 3)
                .collect(Collectors.toSet());
        // [1, 2, 3]
        System.out.println("example12 = " + example12);

        Set<Integer> example13 = Stream.of(1, 2, 3)
                .collect(Collectors.toUnmodifiableSet());
        // [1, 2, 3]
        System.out.println("example13 = " + example13);

        System.out.println("Collectors.collectingAndThen()");
        List<Integer> example14 = Stream.of(1, 2, 3)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), Collections::unmodifiableList));
        // [1, 2, 3]
        System.out.println("example14 = " + example14);

        class CustomClass {

            private List<Integer> streamOutput;

            CustomClass(List<Integer> streamOutput) {
                this.streamOutput = new ArrayList<>(streamOutput);
            }

            @Override
            public String toString() {
                return "CustomClass{" +
                        "streamOutput=" + streamOutput +
                        '}';
            }
        }

        CustomClass example15 = Stream.of(1, 2, 3)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), CustomClass::new));
        // CustomClass{streamOutput=[1, 2, 3]}
        System.out.println("example15 = " + example15);

        String example16 = Stream.of(1, 2, 3).collect(
                Collectors.teeing(
                        Collectors.summingInt(Integer::intValue), // First collector: sums all elements
                        Collectors.averagingInt(Integer::intValue), // Second collector: calculates the average
                        (sum, average) ->
                                String.format("Sum: %d, Avg: %.2f", sum, average) // Merge the results
                )
        );
        // Sum: 6, Avg: 2,00
        System.out.println("example16 = " + example16);

        String example17 = Stream.of("String1", "String2", "String3")
                .collect(Collectors.joining());
        // String1String2String3
        System.out.println("example17 = " + example17);

        String example18 = Stream.of("String1", "String2", "String3")
                .collect(Collectors.joining("-delimiter-"));
        // String1-delimiter-String2-delimiter-String3
        System.out.println("example18 = " + example18);

        String example19 = Stream.of("String1", "String2", "String3")
                .collect(Collectors.joining("-delimiter-", "prefix", "suffix"));
        // prefixString1-delimiter-String2-delimiter-String3suffix
        System.out.println("example19 = " + example19);

        System.out.println("To map method");
        Map<Integer, Integer> example20 = Stream.of(1, 2, 3)
                .collect(Collectors.toMap(k -> k, v -> v * 10));
        //.collect(Collectors.toMap(Function.identity(), v -> v * 10));
        // {1=10, 2=20, 3=30}
        System.out.println("example20 = " + example20);

//        Map<Integer, Integer> example21 = Stream.of(1, 1, 3)
//                .collect(Collectors.toMap(k -> k, v -> v * 10));
        // IllegalStateException: Duplicate key 1
//        System.out.println("example21 = " + example21);
//
        Map<Integer, Integer> example22 = Stream.of(1, 1, 3)
                .collect(Collectors.toMap(k -> k,
                        v -> v * 10,
                        (v1, v2) -> v1 + v2));
        // {1=20, 3=30} {1=(1*10) + (1*10), 3=3*10}
        System.out.println("example22 = " + example22);

        LinkedHashMap<Integer, Integer> example23 = Stream.of(1, 1, 3)
                .collect(Collectors.toMap(k -> k,
                        v -> v * 10,
                        (v1, v2) -> v1 + v2,
                        LinkedHashMap::new));
        // {1=20, 3=30} {1=(1*10) + (1*10), 3=3*10}
        System.out.println("example23 = " + example23);

        System.out.println("Partitioning by method");
        Map<Boolean, List<Integer>> example24 = Stream.of(1, 2, 3)
                .collect(Collectors.partitioningBy(el -> el % 2 == 0));
        // {false=[1, 3], true=[2]}
        System.out.println("example24 = " + example24);

        Map<Boolean, Set<Integer>> example25 = Stream.of(1, 2, 3, 4, 5, 6)
                .collect(Collectors.partitioningBy(
                        el -> el % 2 == 0,  // Predicate to partition by even or odd
                        Collectors.toSet()   // Downstream collector to collect into a set
                ));
        // {false=[1, 3, 5], true=[2, 4, 6]}
        System.out.println("example25 = " + example25);

        Map<Boolean, Optional<Integer>> example26 = Stream.of(1, 2, 3, 4, 5, 6)
                .collect(Collectors.partitioningBy(
                        el -> el % 2 == 0,  // Predicate to partition by even or odd
                        Collectors.maxBy(Integer::compare)   // Downstream collector to collect into a set
                ));
        // {false=Optional[5], true=Optional[6]}
        System.out.println("example26 = " + example26);

        List<Integer> example27 = Stream.of(1, 2, 3).collect(
                // Supplier: Provides a new ArrayList
                ArrayList::new,
                // Accumulator: Adds each element to the ArrayList
                List::add,
                // Combiner: Merges two lists (useful for parallel processing)
                List::addAll
        );
        // [1, 2, 3]
        System.out.println("example27 = " + example27);

        Map<Integer, Integer> example28 = Stream.of(1, 2, 3).collect(
                // Supplier: Creates a new HashMap
                HashMap::new,
                // Accumulator: Adds each number and its square to the Map
                (map, number) -> map.put(number, number * number),
                // Combiner: Merges two maps (useful for parallel processing)
                Map::putAll
        );
        // {1=1, 2=4, 3=9}
        System.out.println("example28 = " + example28);
    }
}
