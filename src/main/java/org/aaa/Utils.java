package org.aaa;

public class Utils<T extends Comparable<T>> {
	static <T extends Comparable<T>> boolean isSorted(T[] array) {
		return isSorted(array, 0, array.length - 1);
	}

	static <T extends Comparable<T>> boolean isSorted(T[] array, int low, int high) {
		for (int i = low + 1; i <= high; i++) {
			if (less(array[i], array[i - 1])) {
				return false;
			}
		}
		return true;
	}

	static <T extends Comparable<T>> boolean less(T v, T w) {
		return v.compareTo(w) < 0;
	}

	static <T extends Comparable<T>> void swap(T[] a, int i, int j) {
		T temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
}
