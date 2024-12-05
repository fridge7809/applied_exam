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

	static <T extends Comparable<T>> boolean lessWeak(T v, T w) {
		return v.compareTo(w) <= 0;
	}

	// Swap two array elements in place
	static <T extends Comparable<T>> void swapInPlace(T[] a, int i, int j) {
		T temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	// Reverse range low…high of the array in place
	static <T extends Comparable<T>> void reverseRangeInPlace(T[] array, int low, int high) {
		high--;
		while (low < high) {
			T t = array[low];
			array[low++] = array[high];
			array[high--] = t;
		}
	}
}
