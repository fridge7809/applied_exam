package org.aaa;

import net.jqwik.api.*;
import net.jqwik.api.constraints.UniqueElements;
import net.jqwik.api.constraints.WithNull;
import org.aaa.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MergeSortAugmentedTest {

	// Negative test cases

	@Example
	void shouldThrowWhenInputIsNull(@ForAll @WithNull(value = 1) Integer[] input) {
		assertThatThrownBy(() -> MergeSortAugmented.sort(input)).message().isEqualTo("Array is null or empty");
	}

	// Positive cases examples

	@Example
	void shouldSortNumericFloatingPointArray() {
		// Internal representation in memory of floating point precision numerals may not be exact and prone
		// to rounding errors.
		Double[] input = {1.1, 1.2, 1.19, 1.09, 1.10};
		MergeSortAugmented.sort(input);
		assertThat(input).containsExactly(1.09, 1.1, 1.1, 1.19, 1.2);
	}

	@Example
	void shouldHandleMixedPositiveAndNegativeIntegers() {
		Integer[] input = {3, -1, 2, -5, 0};
		MergeSortAugmented.sort(input);
		assertThat(input).containsExactly(-5, -1, 0, 2, 3);
	}

	@Example
	void shouldSortBooleans() {
		Boolean[] input = {false, false, true, false};
		MergeSortAugmented.sort(input);
		assertThat(input).isSorted();
	}

	@Example
	void shouldHandleDuplicateElements() {
		Integer[] input = {5, 1, 3, 3, 2, 5};
		MergeSortAugmented.sort(input);
		assertThat(input).containsExactly(1, 2, 3, 3, 5, 5);
	}

	@Example
	void shouldHandleDuplicateUnicodeCharacters() {
		Character[] input = {'a', 'A', 'a', 'A', 'b', 'B'};
		MergeSortAugmented.sort(input);
		assertThat(input).containsExactly('A', 'A', 'B', 'a', 'a', 'b');
	}


	@Example
	void shouldHandleAllZeroes() {
		Byte[] input = {0, 0, 0, 0, 0, 0};
		MergeSortAugmented.sort(input);
		assertThat(input).containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
	}

	@Example
	void shouldSortArrayWithLargeAndSmallNumbers() {
		Integer[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
		MergeSortAugmented.sort(input);
		assertThat(input).containsExactly(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
	}

	@Example
	void shouldMaintainStabilityForReferenceTypes() {
		List<Pair<Integer, Integer>> input = Arrays.asList(
				new Pair<>(1, 0), new Pair<>(2, 1), new Pair<>(1, 2), new Pair<>(2, 3)
		);
		Pair[] pairs = input.toArray(new Pair[0]);
		Integer[] rightValues = input.stream().map(Pair::getRight).toArray(Integer[]::new);

		MergeSortAugmented.sort(pairs);

		for (int i = 0; i < pairs.length - 1; i++) {
			if (pairs[i].equals(pairs[i + 1])) {
				assertThat(rightValues[i]).isLessThan(rightValues[i + 1]);
			}
		}
	}

	// Positive test cases

	@Property
	<T extends Comparable<T>> void sortedArrayShouldContainAllOriginalElements(@ForAll("dataTypesUnderTestProvider") T[] arr) {
		List<T> objects = Arrays.stream(arr).toList();
		MergeSortAugmented.sort(arr);
		assertThat(arr).containsAll(objects);
	}

	@Property
	<T extends Comparable<T>> void shouldSortArray(@ForAll("dataTypesUnderTestProvider") T[] arr) {
		MergeSortAugmented.sort(arr);
		assertThat(arr).isSorted();
	}

	@Property
	void shouldMaintainStability(@ForAll("pairListProvider") List<Pair<Integer, Integer>> input) {
		// Comparison-based sort for the left values of the pairs
		// while right values remain stable.
		Pair<Integer, Integer>[] pairs = input.toArray(new Pair[0]); // Specify the type of the Pair

		MergeSortAugmented.sort(pairs);

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
		return Arbitraries.integers().array(Integer[].class).ofMinSize(1).injectNull(0);
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