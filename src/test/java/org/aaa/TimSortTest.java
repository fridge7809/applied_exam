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

class TimSortTest {

	// Negative test cases

	@Property
	<T extends Comparable<T>> void shouldThrowForNonComparableObjects(@ForAll boolean isAdaptive, @ForAll("mergeRuleProvider") MergeRule mergeRule) {
		Object[] actualMergesort = {new Object(), new Object(), new Object()};
		assertThatThrownBy(() -> Timsort.sort((T[]) actualMergesort, 0, actualMergesort.length, mergeRule, isAdaptive))
				.isInstanceOf(ClassCastException.class);
	}

	@Property
	<T extends Comparable<T>> void shouldThrowForNonComparablePrimitives(@ForAll boolean isAdaptive, @ForAll("mergeRuleProvider") MergeRule mergeRule) {
		Object[] input = {1, 3.14, 'c'};
		assertThatThrownBy(() -> Timsort.sort((T[]) input, 0, input.length, mergeRule, isAdaptive))
				.isInstanceOf(ClassCastException.class);
	}

	@Property
	@Disabled("inefficient")
	void shouldSortLargeArray(@ForAll("mergeRuleProvider") MergeRule mergeRule,
	                          @ForAll boolean isAdaptive,
	                          @ForAll @IntRange(min = 1, max = 100) int cutoff) {
		int largeSize = 26_843_545; // approaching upper bound for -Xmx4G
		Byte[] input = new Byte[largeSize];
		Timsort.sort(input, 0, input.length, MergeRule.BINOMIALSORT, isAdaptive);
		assertThat(input).isSorted();
	}

	// Positive test cases

	@Property
	<T extends Comparable<T>> void shouldThrowForCutoffLengthZero(
			@ForAll("mergeRuleProvider") MergeRule mergeRule,
			@ForAll boolean isAdaptive,
			@ForAll("dataTypesUnderTestProvider") T[] arr
	) {
		assertThatThrownBy(() -> Timsort.sort(arr, mergeRule, isAdaptive, 0)).message().contains("cutoff must be greater than 0");
	}

	@Property
	<T extends Comparable<T>> void shouldHandleSingleElementArray(
			@ForAll("mergeRuleProvider") MergeRule mergeRule,
			@ForAll boolean isAdaptive,
			@ForAll @IntRange(min = 1, max = 100) int cutoff
	) {
		Integer[] input = {42};
		Timsort.sort(input, mergeRule, isAdaptive, cutoff);
		assertThat(input).containsExactly(42);
	}

	@Property
	<T extends Comparable<T>> void shouldReturnNMinusOneComparisonsWhenAdaptiveForSortedInput(
			@ForAll("mergeRuleProvider") MergeRule mergeRule,
			@ForAll @IntRange(min = 1, max = 100) int cutoff,
			@ForAll("dataTypesUnderTestProvider") T[] arr
	) {
		Assume.that(arr.length > 2);
		Arrays.sort(arr);
		int comps = Timsort.sort(arr, mergeRule, true, cutoff);
		assertThat(comps).isEqualTo(arr.length - 1);
	}


	@Property
	void shouldHandleEmptyArray(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] input = {};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).isEmpty();
	}

	@Property
	void shouldThrowForNullSource(@ForAll @WithNull(value = 1) Integer[] input, @ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		assertThatThrownBy(() -> Timsort.sort(input, 0, 10, mergeRule, isAdaptive)).message().isEqualTo("source is null");
	}

	@Property
	void nonAdaptiveLongestRunIsEqualToMinimumThreshold(@ForAll("mergeRuleProvider") MergeRule mergeRule) {
		// resetting the variable to avoid dependency on test execution order is a bit hacky
		Timsort.longestRunFound = 0;
		int cutoff = 3;
		Integer[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		Timsort.sort(input, 0, input.length, cutoff, mergeRule, false);
		assertThat(Timsort.longestRunFound).isEqualTo(cutoff);
		Timsort.longestRunFound = 0;
	}

	@Property
	void adaptiveTimsortFindsRunsLongerThanMinimumThreshold(@ForAll("mergeRuleProvider") MergeRule mergeRule) {
		// resetting the variable to avoid dependency on test execution order is a bit hacky
		Timsort.longestRunFound = 0;
		Integer[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		Timsort.sort(input, 0, input.length, 3, mergeRule, true);
		assertThat(Timsort.longestRunFound).isEqualTo(input.length);
		Timsort.longestRunFound = 0;
	}

	@Property
	void shouldCombineLengthOfMergedRunsIfMergingThirdToLastRunOnStack(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] input = {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0
		};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(
				-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1);
	}

	@Property
	void shouldSortEvenLengthArrays(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] input = {1, 3, 2, 4};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(1, 2, 3, 4);
	}

	@Property
	void shouldSortOddLengthArrays(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] input = {1, 3, 2};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(1, 2, 3);
	}

	@Property
	void shouldExclusivelySortGivenRange(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Double[] input = {1.2, 1.1, 1.19, 1.20, 1.10, 0.2};
		Timsort.sort(input, 3, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(1.2, 1.1, 1.19, 0.2, 1.10, 1.20);
	}

	@Property
	void shouldSortNumericFloatingPointArray(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		// Internal representation in memory of floating point precision numerals may not be exact and prone
		// to rounding errors.
		Double[] input = {1.1, 1.2, 1.19, 1.09, 1.10};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(1.09, 1.1, 1.1, 1.19, 1.2);
	}

	@Property
	void shouldHandleMixedPositiveAndNegativeIntegers(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] input = {3, -1, 2, -5, 0};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(-5, -1, 0, 2, 3);
	}

	@Property
	void shouldSortBooleans(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Boolean[] input = {isAdaptive, isAdaptive, true, isAdaptive};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).isSorted();
	}

	@Property
	void shouldHandleDuplicateElements(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] input = {5, 1, 3, 3, 2, 5};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(1, 2, 3, 3, 5, 5);
	}

	@Property
	void shouldHandleDuplicateUnicodeCharacters(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Character[] input = {'a', 'A', 'a', 'A', 'b', 'B'};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly('A', 'A', 'B', 'a', 'a', 'b');
	}

	@Property
	void shouldHandleAllZeroes(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Byte[] input = {0, 0, 0, 0, 0, 0};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
	}

	@Property
	void shouldSortArrayWithLargeAndSmallNumbers(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
		Timsort.sort(input, 0, input.length, mergeRule, isAdaptive);
		assertThat(input).containsExactly(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
	}

	@Property
	void shouldMaintainStabilityForReferenceTypes(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		List<Pair<Integer, Integer>> input = Arrays.asList(
				new Pair<>(1, 0), new Pair<>(2, 1), new Pair<>(1, 2), new Pair<>(2, 3)
		);
		Pair[] pairs = input.toArray(new Pair[0]);
		Integer[] rightValues = input.stream().map(Pair::getRight).toArray(Integer[]::new);

		Timsort.sort(pairs, 0, pairs.length, mergeRule, isAdaptive);

		for (int i = 0; i < pairs.length - 1; i++) {
			if (pairs[i].equals(pairs[i + 1])) {
				assertThat(rightValues[i]).isLessThan(rightValues[i + 1]);
			}
		}
	}

	@Property
	<T extends Comparable<T>> void sortedArrayShouldContainAllOriginalElements(@ForAll("dataTypesUnderTestProvider") T[] arr, @ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		List<T> objects = Arrays.stream(arr).toList();
		Timsort.sort(arr, 0, arr.length, mergeRule, isAdaptive);
		assertThat(arr).containsAll(objects);
	}

	@Property
	<T extends Comparable<T>> void shouldSortArray(@ForAll("dataTypesUnderTestProvider") T[] arr, @ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Timsort.sort(arr, 0, arr.length, mergeRule, isAdaptive);
		assertThat(arr).isSorted();
	}

	@Example
	void shouldSortWhenRunIsAtBounds(@ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		Integer[] arr = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
		Timsort.sort(arr, 0, arr.length, 32, mergeRule, isAdaptive);
		assertThat(arr).isSorted();
	}

	@Property
	void shouldMaintainStability(@ForAll("pairListProvider") List<Pair<Integer, Integer>> input, @ForAll("mergeRuleProvider") MergeRule mergeRule, @ForAll boolean isAdaptive) {
		// Comparison-based sort for the left values of the pairs
		// while right values remain stable.
		Pair<Integer, Integer>[] pairs = input.toArray(new Pair[0]); // Specify the type of the Pair

		Timsort.sort(pairs, 0, pairs.length, mergeRule, isAdaptive);

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
		return Arbitraries.integers().withDistribution(RandomDistribution.gaussian()).array(Integer[].class).injectNull(0);
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

	@Provide
	Arbitrary<MergeRule> mergeRuleProvider() {
		return Arbitraries.of(MergeRule.class);
	}

}