package org.aaa;

import java.util.Arrays;

public interface Sorting<T extends Comparable<T>> {

	static <T extends Comparable<T>> boolean isSorted(T[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			if (array[i].compareTo(array[i + 1]) > 0) {
				return false;
			}
		}
		return true;
	}

	static <T> boolean containsAllElements(T[] original, T[] sorted) {
		return Arrays.asList(sorted).containsAll(Arrays.asList(original));
	}

	void sort(T[] array);
}