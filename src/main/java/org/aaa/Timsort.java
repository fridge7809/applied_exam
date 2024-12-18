package org.aaa;


import java.lang.reflect.Array;

import static org.aaa.Utils.less;

public class Timsort<T extends Comparable<T>> {

	static int longestRunFound = Integer.MIN_VALUE; // for testing
	private final T[] source;
	private final T[] buffer;
	private final int[] runStart;
	private final int[] runLength;
	private final MergeRule mergeRule;
	private final boolean isAdaptive;
	private final int cutoff;
	private int comparisons;
	private int stackSize = 0;
	private int topLevel;

	@SuppressWarnings("unchecked")
	private Timsort(Class<?> clazz, T[] source, int cutoff, MergeRule mergeRule, boolean isAdaptive) {
		int length = source.length;
		int stackLength = 1000;
		this.cutoff = cutoff;
		this.source = source;
		this.buffer = (T[]) Array.newInstance(clazz, length);
		this.mergeRule = mergeRule;
		this.isAdaptive = isAdaptive;
		this.topLevel = Integer.MIN_VALUE;
		this.comparisons = 0;
		runStart = new int[stackLength];
		runLength = new int[stackLength];
	}

	public static <T extends Comparable<T>> void sort(T[] source, int low, int high, MergeRule mergeRule, boolean isAdaptive) {
		sort(source, low, high, 32, mergeRule, isAdaptive);
	}

	public static <T extends Comparable<T>> int sort(T[] source, int low, int high, int cutoff, MergeRule mergeRule, boolean isAdaptive) {
		// Preconditions
		if (source == null) {
			throw new IllegalArgumentException("source is null");
		}

		assert low >= 0;
		assert low <= high;
		assert high <= source.length;

		int elementsLeft = high - low;
		if (elementsLeft < 2) {
			return 0;
		}

		// state of the sort is contained within a private instance of the class
		Timsort<T> ts = new Timsort<>(source.getClass().componentType(), source, cutoff, mergeRule, isAdaptive);

		while (elementsLeft > 0) {
			int runLength = ts.extendRun(source, low, high);
			ts.pushRun(low, runLength);
			ts.mergeWithRule(mergeRule);
			low += runLength;
			elementsLeft -= runLength;
		}

		assert low == high;
		ts.forceMerge();
		assert ts.stackSize == 1;
		return ts.comparisons;
	}

	private static <T extends Comparable<T>> void reverseRangeInPlace(T[] source, int low, int high) {
		assert low < high;

		high--;
		while (low < high) {
			T temp = source[low];
			source[low++] = source[high];
			source[high--] = temp;
		}
	}

	public static <T extends Comparable<T>> int mergeRuns(T[] source, int low, int mid, int high, T[] buffer) {
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
			} else if (less(buffer[j], buffer[i])) {
				comparisons++;
				source[k] = buffer[j++];
			} else {
				source[k] = buffer[i++];
				comparisons++;
			}
		}
		return comparisons;
	}

	public static void main(String[] args) {
		Integer[] input = {1, 2, 3, 4};
		int comps = Timsort.sort(input, 0, input.length, 2, MergeRule.BINOMIALSORT, true);
		System.out.println(comps);
	}

	public static <T extends Comparable<T>> int sort(T[] array, MergeRule mergeRule, boolean isAdaptive, int cutoff) {
		if (cutoff < 1) {
			throw new IllegalArgumentException("cutoff must be greater than 0");
		}
		return sort(array, 0, array.length, cutoff, mergeRule, isAdaptive);
	}

	private <T extends Comparable<T>> int extendRun(T[] source, int low, int high) {
		assert low < high;
		int runHigh = low + 1;
		if (runHigh == high) {
			return 1;
		}

		boolean runIsDescending = less(source[runHigh++], source[low]);
		comparisons++;
		if (runIsDescending) {
			// strictly decreasing
			while (runHigh < high && less(source[runHigh], source[runHigh - 1])) {
				comparisons++;
				runHigh++;
			}
			reverseRangeInPlace(source, low, runHigh);
		} else {
			// weakly increasing
			while (runHigh < high && !less(source[runHigh], source[runHigh - 1])) {
				comparisons++;
				runHigh++;
			}
		}

		int localRunLength = runHigh - low;

		if (localRunLength >= cutoff) {
			return this.isAdaptive ? localRunLength : cutoff;
		}

		if (!this.isAdaptive && runHigh - low >= cutoff) {
			return cutoff;
		}

		// Run is smaller than minimum cutoff, extend using insertionssort
		// Cap extension to bounds of the source array
		int cappedUpperBound = Math.min(high, low + cutoff);
		// run insertionsort with run length as an offset
		comparisons += InsertionSort.sort(source, low, cappedUpperBound, localRunLength);
		int diff = high - low - 1;
		return Math.min(diff, cutoff);

	}

	private void pushRun(int runStart, int runLength) {
		this.runStart[stackSize] = runStart;
		this.runLength[stackSize] = runLength;
		longestRunFound = Math.max(longestRunFound, runLength);
		stackSize++;
	}

	private void mergeWithRule(MergeRule mergeRule) {
		switch (mergeRule) {
			case LENGTHTWO -> mergeLengthTwo();
			case LEVELSORT -> mergeLevelSort();
			case BINOMIALSORT -> mergeBinomialSort();
			case null, default -> {
				throw new IllegalStateException("Unexpected value: " + mergeRule);
			}
		}
	}

	private void mergeBinomialSort() {
		while (stackSize > 1) {
			int newRun = stackSize - 1;
			while (stackSize > 2 && (runLength[newRun - 1] < runLength[newRun])) {
				mergeAt(newRun - 2);
			}
			if (runLength[newRun - 1] < 2 * runLength[newRun]) {
				mergeAt(newRun - 1);
			} else {
				break;
			}
		}
	}

	private void mergeLevelSort() {
		while (stackSize > 1) {
			int newRun = stackSize - 1;
			int localLevel = computeLevel(newRun - 1);
			if (topLevel < localLevel) {
				mergeAt(newRun - 1);
				topLevel = localLevel;
			} else {
				break;
			}
		}
	}

	private void mergeLengthTwo() {
		while (stackSize > 1) {
			int n = stackSize - 1;
			if (runLength[n] == runLength[n - 1]) {
				mergeAt(n - 1);
			} else {
				break;
			}
		}
	}

	private void forceMerge() {
		if (this.mergeRule.equals(MergeRule.LEVELSORT) || this.mergeRule.equals(MergeRule.BINOMIALSORT)) {
			while (stackSize > 1) {
				int n = stackSize - 1;
				mergeAt(n - 1);
			}
		}
		if (this.mergeRule.equals(MergeRule.LENGTHTWO)) {
			while (stackSize > 1) {
				int n = stackSize - 2;
				if (n > 0 && n + 1 < stackSize && runLength[n - 1] < runLength[n + 1]) {
					n--;
				}
				mergeAt(n);
			}
		}
	}

	private void mergeAt(int i) {
		int firstRunStart = runStart[i];
		int firstRunLength = runLength[i];
		int secondRunStart = runStart[i + 1];
		int secondRunLength = runLength[i + 1];

		runLength[i] = firstRunLength + secondRunLength;
		if (i == stackSize - 3) {
			runStart[i + 1] = runStart[i + 2];
			runLength[i + 1] = runLength[i + 2];
		}

		stackSize--;

		mergeRuns(this.source, firstRunStart, secondRunStart - 1, secondRunStart + secondRunLength - 1, this.buffer);
	}

	private int computeLevel(int i) {
		assert i >= 0;
		long midLeft = (runStart[i] + (long) runLength[i + 1] - 1) / 2;
		long midRight = (runStart[i + 1] + (long) runLength[i + 2] - 1) / 2;
		return 64 - Long.numberOfLeadingZeros(midLeft ^ midRight);
	}
}