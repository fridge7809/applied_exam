package org.aaa;

public class MergeSortAugmented {

	private MergeSortAugmented() {
	}

	private static <T extends Comparable<T>> int merge(T[] a, T[] aux, int lo, int mid, int hi, int c) {
		assert isSorted(a, lo, mid);
		assert isSorted(a, mid + 1, hi);
		int comparisons = 0;

		if (hi + 1 - lo >= 0) {
			System.arraycopy(a, lo, aux, lo, hi + 1 - lo);
		}

		int i = lo;
		int j = mid + 1;
		for (int k = lo; k <= hi; k++) {
			if (i > mid) {
				a[k] = aux[j++];
			} else if (j > hi) {
				a[k] = aux[i++];
			} else if (less(aux[j], aux[i])) {
				a[k] = aux[j++];
				comparisons++;
			} else {
				a[k] = aux[i++];
			}
		}

		assert isSorted(a, lo, hi);
		return comparisons;
	}

	private static <T extends Comparable<T>> int sort(T[] a, T[] aux, int lo, int hi, int c) {
		if (hi <= lo + c) {
			return InsertionSort.sort(aux, lo, hi);
		}
		int mid = lo + (hi - lo) / 2;
		sort(a, aux, lo, mid, c);
		sort(a, aux, mid + 1, hi, c);
		return merge(a, aux, lo, mid, hi, c);
	}

	public static <T extends Comparable<T>> int sort(T[] a, int c) {
		if (a == null || a.length == 0) {
			throw new IllegalArgumentException("Array is null or empty");
		}

		T[] aux = (T[]) new Comparable[a.length];
		int comparisons = sort(a, aux, 0, a.length - 1, c);
		assert isSorted(a);
		return comparisons;
	}

	// Helpers
	private static <T extends Comparable<T>> boolean less(T v, T w) {
		return v.compareTo(w) < 0;
	}

	private static <T extends Comparable<T>> boolean isSorted(T[] a) {
		return isSorted(a, 0, a.length - 1);
	}

	private static <T extends Comparable<T>> boolean isSorted(T[] a, int lo, int hi) {
		for (int i = lo + 1; i <= hi; i++)
			if (less(a[i], a[i - 1])) return false;
		return true;
	}
}