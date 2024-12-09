package org.aaa;


import java.lang.reflect.Array;

import static org.aaa.Utils.less;

public class Timsort<T extends Comparable<T>> {

	private static int minimumRunLength;
	private final T[] source;
	private final T[] buffer;
	private final int[] runStart;
	private final int[] runLength;
	private final MergeRule mergeRule;
	private int stackSize = 0;
	private int topLevel;

	@SuppressWarnings("unchecked")
	private Timsort(Class<?> clazz, T[] source, int minimumRunLength, MergeRule mergeRule) {
		int length = source.length;
		Timsort.minimumRunLength = minimumRunLength;
		this.source = source;
		this.buffer = (T[]) Array.newInstance(clazz, length);
		this.mergeRule = mergeRule;
		int stackLength = 200;
		runStart = new int[stackLength];
		runLength = new int[stackLength];
		this.topLevel = Integer.MIN_VALUE;
	}

	public static <T extends Comparable<T>> void sort(T[] source, int left, int right, MergeRule mergeRule) {
		sort(source, left, right, 32, mergeRule);
	}

	public static <T extends Comparable<T>> void sort(T[] source, int left, int right, int minimumRunLength, MergeRule mergeRule) {
		// Preconditions
		if (source == null) {
			throw new IllegalArgumentException("source is null");
		}
		assert left >= 0;
		assert left <= right;
		assert right <= source.length;

		int elementsLeft = right - left;
		if (elementsLeft < 2) {
			return;
		}

		Timsort<T> ts = new Timsort<>(source.getClass().componentType(), source, minimumRunLength, mergeRule);

		while (elementsLeft > 0) {
			int runLength = extendRun(source, left, right);
			ts.pushRun(left, runLength);
			ts.mergeWithRule();
			for (int i = 0; i < ts.stackSize; ++i)
				System.out.print(ts.runLength[i] + "  ");
			System.out.println();
			left += runLength;
			elementsLeft -= runLength;
		}

		assert left == right;
		ts.forceMerge();
		assert ts.stackSize == 1;
	}

	static <T extends Comparable<T>> int extendRun(T[] source, int low, int high) {
		assert low < high;
		int runHigh = low + 1;
		if (runHigh == high) {
			return 1;
		}

		if (less(source[runHigh++], source[low])) {
			// descending
			while (runHigh < high && less(source[runHigh], source[runHigh - 1])) {
				runHigh++;
			}
			reverseRangeInPlace(source, low, runHigh);
		} else {
			// ascending
			while (runHigh < high && !less(source[runHigh], source[runHigh - 1])) {
				runHigh++;
			}
		}

		int runLength = runHigh - low;

		if (runLength < minimumRunLength) {
			// cap extension to bounds of the source array
			int extendTo = Math.min(high, low + minimumRunLength);
			InsertionSort.sort(source, low, extendTo, runLength);
			return Math.min(high - low - 1, minimumRunLength);
		}

		return runLength;
	}

	private static <T extends Comparable<T>> void reverseRangeInPlace(T[] source, int lo, int hi) {
		hi--;
		while (lo < hi) {
			T temp = source[lo];
			source[lo++] = source[hi];
			source[hi--] = temp;
		}
	}

	public static <T extends Comparable<T>> void mergeRuns(T[] source, int low, int mid, int high, T[] buffer) {
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
				source[k] = buffer[j++];
			} else {
				source[k] = buffer[i++];
			}
		}
	}

	public static void main(String[] args) {
		Double[] input = {1.2, 1.1, 1.19, 1.20, 1.10, 0.2};
		Timsort.sort(input, 0, input.length, MergeRule.LENGTHTWO);
		for (Double d : input) {
			System.out.println(d);
		}
	}

	private void pushRun(int runStart, int runLength) {
		this.runStart[stackSize] = runStart;
		this.runLength[stackSize] = runLength;
		stackSize++;
	}

	private void mergeWithRule() {
		if (this.mergeRule.equals(MergeRule.LENGTHTWO)) {
			while (stackSize > 1) {
				int n = stackSize - 1;
				if (runLength[n] == runLength[n - 1]) {
					mergeAt(n - 1);
				} else {
					break;
				}
			}
		}
		if (this.mergeRule.equals(MergeRule.LEVELSORT)) {
			while (stackSize > 1) {
				int n = stackSize - 1;
				if (topLevel < computeLevel(n - 1)) {
					mergeAtLevel(n - 1);
					topLevel = computeLevel(n - 1);
				} else {
					break;
				}
			}
		}
	}

	private void forceMerge() {
		if (this.mergeRule.equals(MergeRule.LEVELSORT)) {
			while (stackSize > 1) {
				int n = stackSize - 1;
				mergeAtLevel(n - 1);
			}
		} else {
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
		assert stackSize >= 2;
		assert i >= 0;
		assert i == stackSize - 2 || i == stackSize - 3;

		int firstRunStart = runStart[i];
		int firstRunLength = runLength[i];
		int secondRunStart = runStart[i + 1];
		int secondRunLength = runLength[i + 1];
		assert firstRunLength > 0 && secondRunLength > 0;
		assert firstRunStart + firstRunLength == secondRunStart;

		runLength[i] = firstRunLength + secondRunLength;
		stackSize--;

		mergeRuns(this.source, firstRunStart, secondRunStart - 1, secondRunStart + secondRunLength - 1, this.buffer);
	}

	private void mergeAtLevel(int i) {
		int firstRunStart = runStart[i];
		int firstRunLength = runLength[i];
		int secondRunStart = runStart[i + 1];
		int secondRunLength = runLength[i + 1];

		runLength[i] = firstRunLength + secondRunLength;
		stackSize--;

		mergeRuns(this.source, firstRunStart, secondRunStart - 1, secondRunStart + secondRunLength - 1, this.buffer);
	}

	private int computeLevel(int i) {
		long midLeft = (runStart[i] + (long) runLength[i + 1] - 1) / 2;
		long midRight = (runStart[i + 1] + (long) runLength[i + 2] - 1) / 2;
		return 64 - Long.numberOfLeadingZeros(midLeft ^ midRight);
	}
}