package org.aaa;

public class MergeSort<T extends Comparable<T>> {

	private static final int DEFAULT_INSERTION_SORT_THRESHOLD = 14;

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
		if (high <= low) {
			return 0; // Base case, array of one element is always sorted.
		}

		int mid = low + (high - low) / 2;
		int left = sort(source, buffer, low, mid);
		int right = sort(source, buffer, mid + 1, high);

		return left + right + merge(source, buffer, low, mid, high);
	}

	private static <T extends Comparable<T>> int sort(T[] source, T[] buffer, int low, int high, int insertionThreshold) {
		if (high <= low + insertionThreshold) {
			return InsertionSort.sort(source, low, high); // Use insertion sort if below threshold.
		}

		int mid = low + (high - low) / 2;
		int left = sort(source, buffer, low, mid, insertionThreshold);
		int right = sort(source, buffer, mid + 1, high, insertionThreshold);

		return left + right + merge(source, buffer, low, mid, high);
	}

	private static <T extends Comparable<T>> int sortInternal(T[] source, int insertionSortThreshold) {
		if (source == null || source.length == 0) {
			throw new IllegalArgumentException("Array is null or empty");
		}

		T[] buffer = (T[]) new Comparable[source.length];
		int comparisons = sort(source, buffer, 0, source.length - 1, insertionSortThreshold);

		assert Utils.isSorted(source);
		return comparisons;
	}

	public static <T extends Comparable<T>> int sort(T[] source) {
		return sortInternal(source, 0);
	}

	public static <T extends Comparable<T>> int sort(T[] source, int insertionThreshold, boolean useThreshold) {
		return sortInternal(source, insertionThreshold);
	}

	public <T extends Comparable<T>> void sort(T[] array, MergeRule mergeRule, boolean isAdaptive, int cutoff) {
		sort(array);
	}
}