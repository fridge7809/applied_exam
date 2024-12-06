package org.aaa;

public class MergeSortAugmented<T extends Comparable<T>> {

	private static final int c = 14;

	private MergeSortAugmented() {
	}

	private static <T extends Comparable<T>> int merge(T[] source, T[] buffer, int low, int mid, int high) {
		// Precondition, merge operation expects sorted subarrays.
		assert Utils.isSorted(source, low, mid);
		assert Utils.isSorted(source, mid + 1, high);
		int comparisons = 0;

		// Copy values to the buffer.
		// The buffer remains stable while writing back to the source array.
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

		assert Utils.isSorted(source, low, high);
		return comparisons;
	}

	private static <T extends Comparable<T>> int sort(T[] source, T[] buffer, int low, int high, int insertionThreshold) {
		// Use insertion sort if below threshold.
		if (high <= low + insertionThreshold) {
			return InsertionSort.sort(source, low, high);
		}

		// Recursively split in halves.
		int mid = low + (high - low) / 2;
		sort(source, buffer, low, mid, insertionThreshold);
		sort(source, buffer, mid + 1, high, insertionThreshold);

		return merge(source, buffer, low, mid, high);
	}

	private static <T extends Comparable<T>> int sortInternal(T[] a, int insertionSortThreshold) {
		if (a == null || a.length == 0) {
			throw new IllegalArgumentException("Array is null or empty");
		}

		T[] aux = (T[]) new Comparable[a.length];
		int comparisons = sort(a, aux, 0, a.length - 1, insertionSortThreshold);
		assert Utils.isSorted(a);
		return comparisons;
	}

	public static <t extends Comparable<t>> int sort(t[] a, int insertionThreshold) {
		return sortInternal(a, insertionThreshold);
	}

	public static <t extends Comparable<t>> int sort(t[] a) {
		return sortInternal(a, c);
	}

}