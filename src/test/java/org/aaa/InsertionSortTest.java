package org.aaa;


import net.jqwik.api.*;
import net.jqwik.api.constraints.NotEmpty;
import net.jqwik.api.constraints.WithNull;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InsertionSortTest {

	@Property
	void insertionSort_shouldBeSortedForAnAlreadySortedSubarray(@ForAll("partiallySortedIntArrayProvider") @NotEmpty @WithNull(value = 0) Integer[] arr) {
		int offset = arr.length / 2;
		Integer[] original = Arrays.copyOf(arr, arr.length);
		InsertionSort.sort(arr, 0, arr.length - 1, offset);
		assertThat(arr).containsAll(Arrays.asList(original));
	}

	@Property
	void insertionSort_shouldContainAllElementsOfInputArray(@ForAll("intArrayProvider") @NotEmpty Integer[] arr) {
		Integer[] original = Arrays.copyOf(arr, arr.length);
		InsertionSort.sort(arr);
		assertThat(arr).containsAll(Arrays.asList(original));
	}

	@Property
	void insertionSort_shouldBeSorted(@ForAll("intArrayProvider") @NotEmpty Integer[] arr) {
		InsertionSort.sort(arr);
		assertThat(arr).isSorted();
	}

	@Property
	void insertionSort_isStable(@ForAll("pairSequenceProvider") List<Pair<Integer, Integer>> input) {
		// Example case: Sorting pairs using left values for comparison.
		// Sorting is stable because the right value of the pair remains sorted even though it is not used in the comparison for the sort.
		// INPUT: (0, 0), (1, 1), (2, 2), (1, 3)
		// OUTPUT: (0, 0), (1, 1), (1, 3), (2, 2)
		Integer[] arrayToSort = Arrays.stream(input.toArray(new Pair[0])).map(Pair::getLeft).toArray(Integer[]::new);
		InsertionSort.sort(arrayToSort);
		for (int i = 0; i < arrayToSort.length - 1; i++) {
			if (arrayToSort[i].equals(arrayToSort[i + 1])) {
				assertThat(input.get(i).getRight()).isLessThanOrEqualTo(input.get(i + 1).getRight());
			}
		}
	}


	@Provide
	Arbitrary pairSequenceProvider() {
		Arbitrary<Integer> xProvider = Arbitraries.integers();

		Arbitrary<Integer[]> sortedArrayProvider = Arbitraries.integers().between(0, 100)
				.array(Integer[].class).ofMinSize(3).ofMaxSize(30)
				.map(anArray -> {
					Arrays.sort(anArray);
					return anArray;
				});

		return Combinators.combine(xProvider, sortedArrayProvider)
				.as((_, sortedArray) -> Arrays.stream(sortedArray)
						.map(second -> new Pair(xProvider.sample(), second))
						.toList());
	}

	@Provide
	Arbitrary<Integer[]> intArrayProvider() {
		return Arbitraries.integers().array(Integer[].class);
	}

	@Provide
	Arbitrary<Integer[]> partiallySortedIntArrayProvider() {
		return Arbitraries.integers().array(Integer[].class).ofMinSize(2).map(arr -> {
			int mid = arr.length / 2;
			Integer[] temp = Arrays.copyOfRange(arr, 0, mid);
			Arrays.sort(temp);

			Integer[] result = new Integer[arr.length];
			System.arraycopy(temp, 0, result, 0, mid);
			System.arraycopy(arr, mid, result, mid, arr.length - mid);

			return result;
		});
	}
}