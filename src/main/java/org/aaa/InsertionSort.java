package org.aaa;

import java.util.Arrays;

public class InsertionSort {

	private InsertionSort() {
	}

	public static <T extends Comparable<T>> int sort(T[] array) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException("Array is null or empty");
		}

		int comparisons = 0;

		for (int i = 1; i < array.length; i++) {
			T current = array[i];
			int j = i - 1;
			while (j >= 0 && array[j].compareTo(current) > 0) {
				comparisons++;
				array[j + 1] = array[j];
				j = j - 1;
			}
			array[j + 1] = current;
		}

		return comparisons;
	}

	public static <T extends Comparable<T>> int sort(T[] array, int lo, int hi) {
		return sort(Arrays.copyOfRange(array, lo, hi));
	}
}
