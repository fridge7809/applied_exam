package org.aaa;

public class MergeSort<T extends Comparable<T>> {

	private static <T extends Comparable<T>> int merge(T[] source, T[] buffer, int low, int mid, int high) {
		// Precondition, merge operation expects sorted subarrays.
		assert Utils.isSorted(source, low, mid);
		assert Utils.isSorted(source, mid + 1, high);
		int comparisons = 0;

		if (high + 1 - low >= 0) {
			System.arraycopy(source, low, buffer, low, high + 1 - low);
		}

		int i = low;
		int j = mid + 1;
		for (int k = low; k <= high; k++) {
			if (i > mid) {
				source[k] = buffer[j++];
			} else if (j > high) {
				source[k] = buffer[i++];
			} else if (Utils.less(buffer[j], buffer[i])) {
				source[k] = buffer[j++];
				comparisons++;
			} else {
				source[k] = buffer[i++];
				comparisons++;
			}
		}

		// Postcondition
		assert Utils.isSorted(source, low, high);
		return comparisons;
	}

	private static <T extends Comparable<T>> int sort(T[] source, T[] buffer, int low, int high) {
		// Base case, array of one element is always sorted.
		if (high <= low) {
			return 0;
		}

		// Recursively split in halves.
		int mid = low + (high - low) / 2;
		sort(source, buffer, low, mid);
		sort(source, buffer, mid + 1, high);

		return merge(source, buffer, low, mid, high);
	}

	public static <T extends Comparable<T>> int sort(T[] source) {
		// Precondition
		if (source == null) {
			throw new IllegalArgumentException("Array is null or empty");
		}

		// An array of length one is sorted.
		if (source.length == 1) {
			return 0;
		}

		T[] destination = (T[]) new Comparable[source.length];
		int comparisons = sort(source, destination, 0, source.length - 1);

		// Postcondition
		assert Utils.isSorted(source);
		return comparisons;
	}

}