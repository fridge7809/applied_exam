package org.aaa;

public class InsertionSort<T extends Comparable<T>> {

	private InsertionSort() {
	}

	public static <T extends Comparable<T>> int sort(T[] source, int low, int high, int offset) {
		// Preconditions
		assert high >= low;
		assert high - low + 1 >= offset;
		assert offset > 0;
		assert Utils.isSorted(source, low, offset - 1);

		if (source == null) {
			throw new IllegalArgumentException("Array cannot be null");
		}

		if (source.length <= 1) {
			return 0;
		}

		int comparisons = 0;

		for (int i = low + offset; i <= high; i++) {
			int j = i - 1;
			T v = source[i];
			while (Utils.less(v, source[j])) {
				comparisons++;
				source[j + 1] = source[j];
				j--;
				if (j < low) {
					break;
				}
			}
			source[j + 1] = v;
		}

		return comparisons;
	}

	public static <T extends Comparable<T>> int sort(T[] source, int left, int right) {
		assert left <= right;
		int comparisons = 0;
		for (int i = left; i <= right; i++) {
			for (int j = i; j > left; j--) {
				comparisons++;
				if (Utils.less(source[j], source[j - 1])) {
					Utils.swapInPlace(source, j, j - 1);
				}
			}
		}
		return comparisons;
	}

	public static <T extends Comparable<T>> int sort(T[] a) {
		int left = 0;
		int right = a.length - 1;
		return sort(a, left, right);
	}
}
