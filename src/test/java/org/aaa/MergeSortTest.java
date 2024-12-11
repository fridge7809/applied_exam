package org.aaa;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.UniqueElements;
import net.jqwik.api.constraints.WithNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MergeSortTest {

	// Negative test cases

	@Example
	void shouldThrowWhenInputIsNull(@ForAll @WithNull(value = 1) Integer[] input, @ForAll @IntRange(min = 0, max = 100) int cutoff) {
		assertThatThrownBy(() -> MergeSort.sort(input, cutoff, false)).message().isEqualTo("Array is null or empty");
	}

	// Positive cases examples

	@Example
	void shouldSortNumericFloatingPointArray(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		// Internal representation in memory of floating point precision numerals may not be exact and prone
		// to rounding errors.
		Double[] input = {1.1, 1.2, 1.19, 1.09, 1.10};
		MergeSort.sort(input, cutoff, useThreshold);
		assertThat(input).containsExactly(1.09, 1.1, 1.1, 1.19, 1.2);
	}

	@Example
	void shouldHandleMixedPositiveAndNegativeIntegers(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		Integer[] input = {3, -1, 2, -5, 0};
		MergeSort.sort(input, cutoff, useThreshold);
		assertThat(input).containsExactly(-5, -1, 0, 2, 3);
	}

	@Example
	void shouldSortBooleans(@ForAll @IntRange(min = 0, max = 100) int cutoff) {
		Boolean[] input = {false, false, true, false};
		MergeSort.sort(input, cutoff, false);
		assertThat(input).isSorted();
	}

	@Example
	void shouldHandleDuplicateElements(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		Integer[] input = {5, 1, 3, 3, 2, 5};
		MergeSort.sort(input, cutoff, useThreshold);
		assertThat(input).containsExactly(1, 2, 3, 3, 5, 5);
	}

	@Example
	void shouldHandleDuplicateUnicodeCharacters(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		Character[] input = {'a', 'A', 'a', 'A', 'b', 'B'};
		MergeSort.sort(input, cutoff, useThreshold);
		assertThat(input).containsExactly('A', 'A', 'B', 'a', 'a', 'b');
	}


	@Example
	void shouldHandleAllZeroes(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		Byte[] input = {0, 0, 0, 0, 0, 0};
		MergeSort.sort(input, cutoff, useThreshold);
		assertThat(input).containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
	}

	@Example
	void shouldSortArrayWithLargeAndSmallNumbers(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		Integer[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
		MergeSort.sort(input, cutoff, useThreshold);
		assertThat(input).containsExactly(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
	}

	@Example
	void shouldMaintainStabilityForReferenceTypes(@ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		List<Pair<Integer, Integer>> input = Arrays.asList(
				new Pair<>(1, 0), new Pair<>(2, 1), new Pair<>(1, 2), new Pair<>(2, 3)
		);
		Pair[] pairs = input.toArray(new Pair[0]);
		Integer[] rightValues = input.stream().map(Pair::getRight).toArray(Integer[]::new);

		MergeSort.sort(pairs, cutoff, useThreshold);

		for (int i = 0; i < pairs.length - 1; i++) {
			if (pairs[i].equals(pairs[i + 1])) {
				assertThat(rightValues[i]).isLessThan(rightValues[i + 1]);
			}
		}
	}

	// Positive test cases

	@Property
	<T extends Comparable<T>> void sortedArrayShouldContainAllOriginalElements(@ForAll("dataTypesUnderTestProvider") T[] arr, @ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		List<T> objects = Arrays.stream(arr).toList();
		MergeSort.sort(arr, cutoff, useThreshold);
		assertThat(arr).containsAll(objects);
	}

	@Property
	<T extends Comparable<T>> void shouldSortArray(@ForAll("dataTypesUnderTestProvider") T[] arr, @ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		MergeSort.sort(arr, cutoff, useThreshold);
		assertThat(arr).isSorted();
	}

	@Property
	void shouldMaintainStability(@ForAll("pairListProvider") List<Pair<Integer, Integer>> input, @ForAll @IntRange(min = 0, max = 100) int cutoff, @ForAll boolean useThreshold) {
		// Comparison-based sort for the left values of the pairs
		// while right values remain stable.
		Pair<Integer, Integer>[] pairs = input.toArray(new Pair[0]); // Specify the type of the Pair

		MergeSort.sort(pairs, cutoff, useThreshold);

		for (int i = 0; i < pairs.length - 1; i++) {
			if (pairs[i].getLeft().equals(pairs[i + 1].getLeft())) {
				// Explicitly cast right values to Integer when using assertThat
				assertThat(pairs[i].getRight()).isLessThan(pairs[i + 1].getRight());
			}
		}
	}

	// Data types for test

	@Provide
	Arbitrary<Integer[]> integerArrayProvider() {
		return Arbitraries.integers().withDistribution(RandomDistribution.gaussian()).array(Integer[].class).ofMinSize(1).injectNull(0);
	}

	@Provide
	Arbitrary<Double[]> doubleArrayProvider() {
		return Arbitraries.doubles().array(Double[].class).ofMinSize(1).injectNull(0);
	}

	@Provide
	Arbitrary<Character[]> characterArrayProvider() {
		return Arbitraries.chars().array(Character[].class).ofMinSize(1).injectNull(0);
	}

	@Provide
	Arbitrary<String[]> stringArrayProvider() {
		return Arbitraries.strings().array(String[].class).ofMinSize(1).injectNull(0);
	}

	@Provide
	Arbitrary<Object[]> dataTypesUnderTestProvider() {
		return Arbitraries.oneOf(
				integerArrayProvider(),
				doubleArrayProvider(),
				characterArrayProvider(),
				stringArrayProvider()
		).map(Function.identity());
	}

	@Provide
	Arbitrary<List<Pair<Integer, Integer>>> pairListProvider() {
		Arbitrary<Integer> leftValueProvider = Arbitraries.integers().between(0, 100).injectDuplicates(0.7);
		return Combinators.combine(leftValueProvider, distinctIntegerArrayProvider())
				.as((_, rightArr) ->
						Arrays.stream(rightArr)
								.map(right -> new Pair<>(leftValueProvider.sample(), right))
								.distinct()
								.toList()
				);
	}

	@UniqueElements
	Arbitrary<Integer[]> distinctIntegerArrayProvider() {
		return Arbitraries.integers()
				.between(0, 100)
				.array(Integer[].class)
				.ofMinSize(1)
				.map(arr -> {
					Arrays.sort(arr);
					return arr;
				});
	}
}