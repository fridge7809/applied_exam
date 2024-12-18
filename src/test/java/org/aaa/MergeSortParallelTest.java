//package org.aaa;
//
//import net.jqwik.api.*;
//import net.jqwik.api.constraints.IntRange;
//import net.jqwik.api.constraints.WithNull;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Function;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//class MergeSortParallelTest {
//
//	@Example
//	void getNIsInclusive() {
//		assertThat(MergeSortParallel.getN(0, 10)).isEqualTo(11);
//		assertThat(MergeSortParallel.getN(0, 1)).isEqualTo(2);
//	}
//
//	@Example
//	void chunkSize() {
//		// 10, 2
//		assertThat(MergeSortParallel.getChunkSize(10, 2)).isEqualTo(5);
//		assertThat(MergeSortParallel.getChunkSize(20, 4)).isEqualTo(5);
//	}
//
//	@Example
//	<T extends Comparable<T>>  void even() {
//		Integer[] even = new Integer[] {1, 2, 3, 4};
//		int threadCount = 2;
//		int expected = 2;
//		assertThat(MergeSortParallel.getParts(even, threadCount)).isEqualTo(expected);
//	}
//
//	@Example
//	<T extends Comparable<T>>  void odd() {
//		Integer[] odd = new Integer[] {1, 2, 3};
//		int threadCount = 4;
//		int expected = 1;
//		assertThat(MergeSortParallel.getParts(odd, threadCount)).isEqualTo(expected);
//	}
//
//	@Property
//	<T extends Comparable<T>>  void shouldHavePositiveParts(@ForAll("dataTypesUnderTestProvider") T[] source, @ForAll @IntRange(min = 1, max = 12) int threadCount) {
//		assertThat(MergeSortParallel.getParts(source, threadCount)).isPositive();
//	}
//
//	// Negative test cases
//
//	@Example
//	void shouldThrowWhenInputIsNull(@ForAll @WithNull(value = 1) Integer[] input,
//			@ForAll @IntRange(min = 0, max = 100) int cutoff) {
//		assertThatThrownBy(() -> MergeSortParallel.sortParallelWithParallelMerge(input, 0, 4)).message()
//				.isEqualTo("Array is null or empty");
//	}
//
//	@Property
//	<T extends Comparable<T>> void shouldThrowForNonComparableObjects(@ForAll @IntRange(min = 0, max = 100) int cutoff,
//			@ForAll boolean useThreshold) {
//		Object[] actualMergesort = { new Object(), new Object(), new Object() };
//		assertThatThrownBy(() -> MergeSortParallel.sortParallelWithParallelMerge((T[]) actualMergesort, 0, 4))
//				.isInstanceOf(ClassCastException.class);
//	}
//
//	@Property
//	<T extends Comparable<T>> void shouldThrowForNonComparablePrimitives(
//			@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
//		Object[] input = { 1, 3.14, 'c' };
//		assertThatThrownBy(() -> MergeSortParallel.sortParallelWithParallelMerge((T[]) input, 0, 4))
//				.isInstanceOf(ClassCastException.class);
//	}
//
//	// Positive cases examples
//
//	// sequential and parallel mergesort works differently. Seq version uses
//	// insertion sort when subarray is smaller than threshold, and parallel version
//	// uses mergesort sequential when lower than threshold.
//	// we therefore keep threshold as 0 to test these
//	@Example
//	@Disabled
//	void shouldReturnCorrectParallel() {
//		Integer[] actualOne = { 5, 2, 8, 6, 9, 1, 3, 7, 1, 2, 3, 4, 5, 6, 7, 8 };
//		Integer[] actualTwo = { 5, 2, 8, 6, 9, 1, 3, 7, 1, 2, 3, 4, 5, 6, 7, 8 };
//		int comparisonsOne = MergeSort.sort(actualOne, 0, true);
//		int comparisonsTwo = MergeSortParallel.sortParallel(actualTwo, 0);
//		assertThat(comparisonsOne).isEqualTo(comparisonsTwo);
//	}
//
//	@Example
//	void shouldReturnCorrectNumberOfComparisonsWithInsertionSortThreshold() {
//		Integer[] actualOne = { 5, 2, 8, 6, 9, 1, 3, 7, 1, 2, 3, 4, 5, 6, 7, 8 };
//		Integer comparisonsOne;
//		comparisonsOne = MergeSortParallel.sortParallelWithParallelMerge(actualOne, 0, 4);
//		assertThat(comparisonsOne).isEqualTo(64);
//
//	}
//
//	@Example
//	void shouldSortNumericFloatingPointArray() {
//		// Internal representation in memory of floating point precision numerals may
//		// not be exact and prone
//		// to rounding errors.
//		Double[] input = { 1.1, 1.2, 1.19, 1.09, 1.10 };
//		MergeSortParallel.sortParallelWithParallelMerge(input, 0, 4);
//
//		assertThat(input).containsExactly(1.09, 1.1, 1.1, 1.19, 1.2);
//	}
//
//	@Example
//	void shouldHandleMixedPositiveAndNegativeIntegers(@ForAll @IntRange(min = 0, max = 100) int cutoff,
//			@ForAll boolean useThreshold) {
//		Integer[] input = { 3, -1, 2, -5, 0 };
//		MergeSortParallel.sortParallelWithParallelMerge(input, 0, 4);
//
//		assertThat(input).containsExactly(-5, -1, 0, 2, 3);
//	}
//
//	@Example
//	void shouldSortBooleans() {
//		Boolean[] input = { false, false, true, false };
//		MergeSortParallel.sortParallelWithParallelMerge(input, 0, 4);
//
//		assertThat(input).isSorted();
//	}
//
//	@Example
//	void shouldHandleDuplicateElements(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
//		Integer[] input = { 5, 1, 3, 3, 2, 5 };
//		MergeSortParallel.sortParallelWithParallelMerge(input, 0, 4);
//
//		assertThat(input).containsExactly(1, 2, 3, 3, 5, 5);
//	}
//
//	@Example
//	void shouldHandleDuplicateUnicodeCharacters() {
//		Character[] input = { 'a', 'A', 'a', 'A', 'b', 'B' };
//		MergeSortParallel.sortParallelWithParallelMerge(input, 0, 4);
//
//		assertThat(input).containsExactly('A', 'A', 'B', 'a', 'a', 'b');
//	}
//
//	@Example
//	void shouldHandleAllZeroes(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
//		Byte[] input = { 0, 0, 0, 0, 0, 0 };
//		MergeSortParallel.sortParallelWithParallelMerge(input, 0, 4);
//
//		assertThat(input).containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
//	}
//
//	@Example
//	void shouldSortArrayWithLargeAndSmallNumbers(@ForAll @IntRange(min = 0, max = 100) int serialSortThreshold,
//			@ForAll int numOfAvailableThreads) {
//		Integer[] input = { Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1 };
//		MergeSortParallel.sortParallelWithParallelMerge(input, serialSortThreshold, numOfAvailableThreads);
//
//		assertThat(input).containsExactly(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Example
//	void shouldMaintainStabilityForReferenceTypes(@ForAll @IntRange(min = 0, max = 100) int serialSortThreshold,
//			@ForAll int numOfAvailableThreads) {
//		List<Pair<Integer, Integer>> input = Arrays.asList(
//				new Pair<>(1, 0), new Pair<>(2, 1), new Pair<>(1, 2), new Pair<>(2, 3));
//		Pair[] pairs = input.toArray(new Pair[0]);
//		Integer[] rightValues = input.stream().map(Pair::getRight).toArray(Integer[]::new);
//
//		MergeSortParallel.sortParallelWithParallelMerge(pairs, serialSortThreshold, numOfAvailableThreads);
//
//		for (int i = 0; i < pairs.length - 1; i++) {
//			if (pairs[i].equals(pairs[i + 1])) {
//				assertThat(rightValues[i]).isLessThan(rightValues[i + 1]);
//			}
//		}
//	}
//
//	// Positive test cases
//
//	@Property
//	<T extends Comparable<T>> void sortedArrayShouldContainAllOriginalElements(
//			@ForAll("dataTypesUnderTestProvider") T[] arr,
//			@ForAll @IntRange(min = 1, max = 100) int serialSortThreshold,
//			@ForAll @IntRange(min = 1, max = 100) int numOfAvailableThreads) {
//		List<T> objects = Arrays.stream(arr).toList();
//		MergeSortParallel.sortParallelWithParallelMerge(arr, serialSortThreshold, numOfAvailableThreads);
//
//		assertThat(arr).containsAll(objects);
//	}
//
//	@Property
//	<T extends Comparable<T>> void shouldSortArray(@ForAll("dataTypesUnderTestProvider") T[] arr,
//			@ForAll @IntRange(min = 1, max = 100) int serialSortThreshold,
//			@ForAll @IntRange(min = 1, max = 100) int numOfAvailableThreads) {
//		MergeSortParallel.sortParallelWithParallelMerge(arr, serialSortThreshold, numOfAvailableThreads);
//
//		assertThat(arr).isSorted();
//	}
//
//	@Property
//	void shouldMaintainStability(@ForAll("pairListProvider") List<Pair<Integer, Integer>> input,
//			@ForAll @IntRange(min = 1, max = 100) int serialSortThreshold,
//			@ForAll @IntRange(min = 1, max = 6) int numOfAvailableThreads) {
//		// Comparison-based sort for the left values of the pairs
//		// while right values remain stable.
//		Pair<Integer, Integer>[] pairs = input.toArray(new Pair[0]); // Specify the type of the Pair
//
//		MergeSortParallel.sortParallelWithParallelMerge(pairs, serialSortThreshold, numOfAvailableThreads);
//
//		for (int i = 0; i < pairs.length - 1; i++) {
//			if (pairs[i].equals(pairs[i + 1])) {
//				// Explicitly cast right values to Integer when using assertThat
//				assertThat(pairs[i].getRight()).isLessThan(pairs[i + 1].getRight());
//			}
//		}
//	}
//
//	@Property
//	<T extends Comparable<T>> void shouldSortArrayParallel(@ForAll("dataTypesUnderTestProvider") T[] arr) {
//		int numOfAvailableThreads = 4;
//		MergeSortParallel.sortParallelWithParallelMerge(arr, 0, numOfAvailableThreads);
//		assertThat(arr).isSorted();
//	}
//
//	// Data types for test
//
//	@Provide
//	Arbitrary<Integer[]> integerArrayProvider() {
//		return Arbitraries.integers().withDistribution(RandomDistribution.gaussian()).array(Integer[].class)
//				.ofMinSize(1).injectNull(0);
//	}
//
//	@Provide
//	Arbitrary<Double[]> doubleArrayProvider() {
//		return Arbitraries.doubles().array(Double[].class).ofMinSize(1).injectNull(0);
//	}
//
//	@Provide
//	Arbitrary<Character[]> characterArrayProvider() {
//		return Arbitraries.chars().array(Character[].class).ofMinSize(1).injectNull(0);
//	}
//
//	@Provide
//	Arbitrary<String[]> stringArrayProvider() {
//		return Arbitraries.strings().array(String[].class).ofMinSize(1).injectNull(0);
//	}
//
//	@Provide
//	Arbitrary<Object[]> dataTypesUnderTestProvider() {
//		return Arbitraries.oneOf(
//				integerArrayProvider(),
//				doubleArrayProvider(),
//				characterArrayProvider(),
//				stringArrayProvider()).map(Function.identity());
//	}
//
//	@Provide
//	Arbitrary<List<Pair<Integer, Integer>>> pairListProvider() {
//		Arbitrary<Integer> leftValueProvider = Arbitraries.integers().between(0, 100).injectDuplicates(0.7);
//		return Combinators.combine(leftValueProvider, distinctIntegerArrayProvider())
//				.as((_, rightArr) ->
//						Arrays.stream(rightArr)
//								.map(right -> new Pair<>(leftValueProvider.sample(), right))
//								.distinct()
//								.toList()
//				);
//	}
//
//	Arbitrary<Integer[]> distinctIntegerArrayProvider() {
//		return Arbitraries.integers()
//				.between(0, 100)
//				.array(Integer[].class)
//				.ofMinSize(1)
//				.uniqueElements()
//				.map(arr -> {
//					Arrays.sort(arr);
//					return arr;
//				});
//	}
//}
