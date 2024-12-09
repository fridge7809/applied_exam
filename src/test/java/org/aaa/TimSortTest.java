package org.aaa;

import net.jqwik.api.*;
import net.jqwik.api.constraints.UniqueElements;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class TimSortTest {

	@Example
	void newCase() {
		Integer[] input = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
	}

	@Example
	void shouldSortEvenLengthArrays() {
		Integer[] input = {1, 3, 2, 4};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(1, 2, 3, 4);
	}

	@Example
	void shouldSortOddLengthArrays() {
		Integer[] input = {1, 3, 2};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(1, 2, 3);
	}

	@Example
	void shouldExclusivelySortGivenRange() {
		Double[] input = {1.2, 1.1, 1.19, 1.20, 1.10, 0.2};
		Timsort.sort(input, 3, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(1.2, 1.1, 1.19, 0.2, 1.10, 1.20);
	}

	@Example
	void shouldSortNumericFloatingPointArray() {
		// Internal representation in memory of floating point precision numerals may not be exact and prone
		// to rounding errors.
		Double[] input = {1.1, 1.2, 1.19, 1.09, 1.10};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(1.09, 1.1, 1.1, 1.19, 1.2);
	}

	@Example
	void shouldHandleMixedPositiveAndNegativeIntegers() {
		Integer[] input = {3, -1, 2, -5, 0};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(-5, -1, 0, 2, 3);
	}

	@Example
	void shouldSortBooleans() {
		Boolean[] input = {false, false, true, false};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).isSorted();
	}

	@Example
	void shouldHandleDuplicateElements() {
		Integer[] input = {5, 1, 3, 3, 2, 5};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(1, 2, 3, 3, 5, 5);
	}

	@Example
	void shouldHandleDuplicateUnicodeCharacters() {
		Character[] input = {'a', 'A', 'a', 'A', 'b', 'B'};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly('A', 'A', 'B', 'a', 'a', 'b');
	}

	@Example
	void shouldHandleAllZeroes() {
		Byte[] input = {0, 0, 0, 0, 0, 0};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
	}

	@Example
	void shouldSortArrayWithLargeAndSmallNumbers() {
		Integer[] input = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
		Timsort.sort(input, 0, input.length, MergeRule.LEVELSORT);
		assertThat(input).containsExactly(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
	}

	@Example
	void shouldMaintainStabilityForReferenceTypes() {
		List<Pair<Integer, Integer>> input = Arrays.asList(
				new Pair<>(1, 0), new Pair<>(2, 1), new Pair<>(1, 2), new Pair<>(2, 3)
		);
		Pair[] pairs = input.toArray(new Pair[0]);
		Integer[] rightValues = input.stream().map(Pair::getRight).toArray(Integer[]::new);

		Timsort.sort(pairs, 0, pairs.length, MergeRule.LEVELSORT);

		for (int i = 0; i < pairs.length - 1; i++) {
			if (pairs[i].equals(pairs[i + 1])) {
				assertThat(rightValues[i]).isLessThan(rightValues[i + 1]);
			}
		}
	}

	@Property
	<T extends Comparable<T>> void sortedArrayShouldContainAllOriginalElements(@ForAll("dataTypesUnderTestProvider") T[] arr) {
		List<T> objects = Arrays.stream(arr).toList();
		Timsort.sort(arr, 0, arr.length, MergeRule.LEVELSORT);
		assertThat(arr).containsAll(objects);
	}

	@Property
	<T extends Comparable<T>> void shouldSortArray(@ForAll("dataTypesUnderTestProvider") T[] arr) {
		Timsort.sort(arr, 0, arr.length, MergeRule.LEVELSORT);
		assertThat(arr).isSorted();
	}

	@Example
	void shouldSortWhenRunIsAtBounds() {
		Integer[] arr = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
		Timsort.sort(arr, 0, arr.length, 32, MergeRule.LEVELSORT);
		assertThat(arr).isSorted();
	}

	@Property
	void shouldMaintainStability(@ForAll("pairListProvider") List<Pair<Integer, Integer>> input) {
		// Comparison-based sort for the left values of the pairs
		// while right values remain stable.
		Pair<Integer, Integer>[] pairs = input.toArray(new Pair[0]); // Specify the type of the Pair

		Timsort.sort(pairs, 0, pairs.length, MergeRule.LEVELSORT);

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

	public static class Pair<L extends Comparable, R extends Comparable> implements Comparable<Pair<L, R>> {
		private final L left;
		private final R right;

		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}

		public L getLeft() {
			return left;
		}

		public R getRight() {
			return right;
		}

		@Override
		public String toString() {
			return "(" + left + ", " + right + ")";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Pair<?, ?> pair = (Pair<?, ?>) o;
			return left.equals(pair.left) && right.equals(pair.right);
		}

		@Override
		public int hashCode() {
			return 31 * left.hashCode() + right.hashCode();
		}

		@Override
		public int compareTo(Pair<L, R> o) {
			return this.left.compareTo(o.left);
		}
	}
}