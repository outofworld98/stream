import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class stream {
    @Test
    void stream_allApi() {
        // 배열 스트림
        String[] strArr = new String[]{"one", "two", "three", "four", "five"};
        Stream<String> stream = Arrays.stream(strArr);
        assertThat(stream.collect(Collectors.toList()), hasItems("one", "two", "three", "four", "five"));

        // 부분 스트림
        Stream<String> partStream = Arrays.stream(strArr, 1, 2);
        assertThat(partStream.collect(Collectors.toList()), hasItems("two"));

        // 빈 스트림
        Stream<String> emptyStream = Stream.empty();
        assertEquals(0L, emptyStream.count());

        // 컬렉션 스트림
        List<String> list = Arrays.asList("one", "two", "three", "four", "five");
        Stream<String> listStream = list.stream();
        assertThat(listStream.collect(Collectors.toList()), hasItems("one", "two", "three", "four", "five"));

        // 병렬 스트림
        Stream<String> parallelStream = list.parallelStream();
        boolean isParallel = parallelStream.isParallel();
        assertTrue(isParallel);
        IntStream intParallelStream = IntStream.range(1, 10).parallel();
        // 병렬 처리로 인해 순서대로 생성되지 않음
        assertThat(intParallelStream.boxed().collect(Collectors.toList()), containsInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9));
        System.out.println();

        // 병렬 스트림 재생성
        intParallelStream = IntStream.range(1, 10).parallel();

        // 시퀀셜 스트림으로 변경
        intParallelStream = intParallelStream.sequential();
        // 시퀀셜 처리로 인해 순서대로 숫자생성됨.
        assertThat(intParallelStream.boxed().collect(Collectors.toList()), hasItems(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // 빌더 패턴을 사용하여 스트림 생성
        Stream<String> builderStream = Stream.<String>builder().add("one").add("two").add("three").build();
        assertThat(builderStream.collect(Collectors.toList()), hasItems("one", "two", "three"));

        // 스트림 generate - 무한으로 생성되므로 limit이 필요
        Stream<String> generateStream = Stream.generate(() -> "gen").limit(5);
        assertThat(generateStream.collect(Collectors.toList()), hasItems("gen", "gen", "gen", "gen", "gen"));

        // 스트림 iterate - 무한으로 생성되므로 limit이 필요
        Stream<Integer> iterateStream = Stream.iterate(10, n -> n + 2).limit(5);
        assertThat(iterateStream.collect(Collectors.toList()), hasItems(10, 12, 14, 16, 18));

        // 기본 타입 스트림
        IntStream intStream = IntStream.range(1, 5);
        assertThat(intStream.boxed().collect(Collectors.toList()), hasItems(1, 2, 3, 4));

        LongStream longStream = LongStream.rangeClosed(1, 5);
        assertThat(longStream.boxed().collect(Collectors.toList()), hasItems(1L, 2L, 3L, 4L, 5L));

        DoubleStream doubleStream = new Random().doubles(10);

        // Boxing : IntStream -> Stream
        Stream<Integer> boxedIntStream = IntStream.range(1, 5).boxed();
        assertThat(boxedIntStream.collect(Collectors.toList()), hasItems(1, 2, 3, 4));

        // Char stream
        IntStream charsStream = "Stream".chars();
        assertThat(charsStream.boxed().collect(Collectors.toList()), hasItems(83, 116, 114, 101, 97, 109));

        // RegEx - 문자열을 정규표현식을 적용하여 스트림으로 반환
        Stream<String> stringStream = Pattern.compile(", ").splitAsStream("one, two, three");
        assertThat(stringStream.collect(Collectors.toList()), hasItems("one", "two", "three"));

        // File -> Stream
        try {
            Stream<String> lineStream = Files.lines(Paths.get("file.txt"), Charset.forName("UTF-8"));
        } catch (Exception e) {
        }

        // 두개의 Stream 합치기(병합)
        List<String> listOne = Stream.of("one", "two", "three").collect(Collectors.toList());
        List<String> listTwo = Stream.of("four", "five", "six").collect(Collectors.toList());
        Stream<String> concatStream = Stream.concat(listOne.stream(), listTwo.stream());
        assertThat(concatStream.collect(Collectors.toList()), hasItems("one", "two", "three", "four", "five", "six"));

        // 필터링
        concatStream = Stream.concat(listOne.stream(), listTwo.stream());
        Stream<String> fiterStream = concatStream.filter(num -> num.contains("three"));
        assertThat(fiterStream.collect(Collectors.toList()), hasItems("three"));

        // 맵핑 - 특정값으로 변환
        concatStream = Stream.concat(listOne.stream(), listTwo.stream());
        Stream<String> uppperStream = concatStream.map(String::toUpperCase);
        assertThat(uppperStream.collect(Collectors.toList()), hasItems("ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX"));

        // 중첩된 리스트를 단일 리스트로 변환 - flatMap
        List<List<String>> overlapList = Arrays.asList(Arrays.asList("one", "two"), Arrays.asList("three", "four"));
        Stream<String> flatStream = overlapList.stream().flatMap(Collection::stream);
        assertThat(flatStream.collect(Collectors.toList()), hasItems("one", "two", "three", "four"));

        // 정렬
        List<Integer> sortedList = IntStream.of(14, 11, 20, 39, 23)
                .sorted()
                .boxed()
                .collect(Collectors.toList());
        assertThat(sortedList, hasItems(11, 14, 20, 23, 39));

        List<String> lang = Arrays.asList("Java", "Scala", "Groovy", "Python", "Go", "Swift");

        // 정렬하여 리스트로 반환
        List<String> sortedLists = lang.stream().sorted().collect(Collectors.toList());
        assertThat(sortedLists, hasItems("Go", "Groovy", "Java", "Python", "Scala", "Swift"));

        // 역순으로 정렬하여 리스트로 반환
        List<String> sortedRLists = lang.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        assertThat(sortedRLists, hasItems("Swift", "Scala", "Python", "Java", "Groovy", "Go"));

        // 데이터 길이로 정렬 후 리스트로 반환
        List<String> sortedSizeLists = lang.stream().sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
        assertThat(sortedSizeLists, hasItems("Go", "Java", "Scala", "Swift", "Groovy", "Python"));

        // 데이터 길이로 역순 정렬 후 리스트로 반환
        List<String> sortedSizeRLists = lang.stream().sorted((s1, s2) -> s2.length() - s1.length()).collect(Collectors.toList());
        assertThat(sortedSizeRLists, hasItems("Groovy", "Python", "Scala", "Swift", "Java", "Go"));

        // peek - 연산 중간에 데이터 확인
        int sum = IntStream.of(1, 3, 5, 7, 9).peek(System.out::println).sum();
        assertEquals(25, sum);

        // Calculating - 데이터 건수, 합계 반환
        assertEquals(5, IntStream.of(1, 3, 5, 7, 9).count());
        assertEquals(25, IntStream.of(1, 3, 5, 7, 9).sum());

        // 최소값 계산
        OptionalInt min = IntStream.of(1, 3, 5, 7, 9).min();
        assertEquals(1, min.getAsInt());

        // 최대값 계산
        OptionalInt max = IntStream.of(1, 3, 5, 7, 9).max();
        assertEquals(9, max.getAsInt());

        // 평균 계산
        OptionalDouble average = DoubleStream.of(1.1, 2.2, 3.3, 4.4, 5.5).average();
        assertEquals(3.3, average.getAsDouble());

        // reduce - 컬렉션의 값 하나 하나를 꺼내 연산.
        OptionalInt reduced = IntStream.range(1, 4).reduce(
                (a, b) -> {
                    return Integer.sum(a, b);
                });
        assertEquals(6, reduced.getAsInt());

        // reduce - 위 연산과 동일하나 초기값 지정하여 연산.
        int reduceTwoParam = IntStream.range(1, 4).reduce(10, Integer::sum);
        assertEquals(16, reduceTwoParam);

        Integer reducedParallel = Arrays.asList(1, 2, 3, 4).parallelStream()
                .reduce(10, Integer::sum, (a, b) -> {
                    return a + b;
                });
        assertEquals(50, reducedParallel.intValue());

        // Collecting
        List<Product> productList =
                Arrays.asList(
                        new Product(23, "potatoes"),
                        new Product(14, "orange"),
                        new Product(13, "lemon"),
                        new Product(23, "bread"),
                        new Product(13, "suger"));
        List<String> collectorCollection =
                productList.stream()
                        .map(Product::getName)
                        .collect(Collectors.toList());
        assertThat(collectorCollection, hasItems("potatoes", "orange", "lemon", "bread", "suger"));

        // Joining
        String listToString =
                productList.stream()
                        .map(Product::getName)
                        .collect(Collectors.joining());
        assertEquals("potatoesorangelemonbreadsuger", listToString);

        String listToString2 =
                productList.stream()
                        .map(Product::getName)
                        .collect(Collectors.joining(", ", "<", ">"));
        assertEquals("<potatoes, orange, lemon, bread, suger>", listToString2);

        // Average
        Double averageAmount =
                productList.stream()
                        .collect(Collectors.averagingInt(Product::getAmount));
        assertEquals(17.2, averageAmount, 0.1);

        // Sum
        Integer sumAmount =
                productList.stream()
                        .collect(Collectors.summingInt(Product::getAmount));
        assertEquals(Integer.valueOf(86), sumAmount);

        Integer sumAmount2 = productList.stream().mapToInt(Product::getAmount).sum();
        assertEquals(Integer.valueOf(86), sumAmount2);

        // Summary
        IntSummaryStatistics statistics = productList.stream().collect(Collectors.summarizingInt(Product::getAmount));

        assertEquals(17.2, statistics.getAverage(), 0.1);
        assertEquals(5, statistics.getCount());
        assertEquals(23, statistics.getMax());
        assertEquals(13, statistics.getMin());
        assertEquals(86, statistics.getSum());

        // Grouping
        Map<Integer, List<Product>> collectorMapOfLists =
                productList.stream().collect(Collectors.groupingBy(Product::getAmount));

        assertEquals(2, collectorMapOfLists.get(13).size());
        assertEquals(1, collectorMapOfLists.get(14).size());

        // Partition
        Map<Boolean, List<Product>> mapPartitioned =
                productList.stream().collect(Collectors.partitioningBy(el -> el.getAmount() > 15));

        assertEquals(2, mapPartitioned.get(true).size());
        assertEquals(3, mapPartitioned.get(false).size());

        // unmodifiable Set 생성
        Set<Product> unmodifiableSet = productList.stream().collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));

        // Collector.of
        Collector<Product, ?, LinkedList<Product>> toLinkedList =
                Collector.of(LinkedList::new, LinkedList::add,
                        (first, second) -> {
                            first.addAll(second);
                            return first;
                        }
                );
        LinkedList<Product> linkedListOfPersons =
                productList.stream().collect(toLinkedList);

        // matching
        List<String> names = Arrays.asList("Eric", "Elena", "Java");
        boolean anyMatch = names.stream().anyMatch(name -> name.contains("J"));
        assertTrue(anyMatch);
        boolean allMatch = names.stream().allMatch(name -> name.length() > 3);
        assertTrue(allMatch);
        boolean noneMatch = names.stream().noneMatch(name -> name.endsWith("s"));
        assertTrue(noneMatch);
    }
}

class Product {
    private int amount;
    private String name;

    public Product(int amount, String name) {
        this.amount = amount;
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
