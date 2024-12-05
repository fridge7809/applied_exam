package org.aaa;


import java.lang.reflect.Array;

import static org.aaa.Utils.less;

public class Timsort<T extends Comparable<T>> {

	private static final int MINIMUM_RUN_LENGTH = 32;
	private final T[] source;
	private final T[] buffer;
	private final int[] runStart;
	private final int[] runLength;
	private int stackSize = 0;

	@SuppressWarnings("unchecked")
	private Timsort(Class<?> clazz, T[] source) {
		int length = source.length;
		this.source = source;
		this.buffer = (T[]) Array.newInstance(clazz, length);
		int stackLength = 200;
		runStart = new int[stackLength];
		runLength = new int[stackLength];
	}

	public static <T extends Comparable<T>> void sort(T[] source, int left, int right) {
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

		Timsort<T> ts = new Timsort<>(source.getClass().componentType(), source);

		while (elementsLeft > 0) {
			int runLength = extendRun(source, left, right);
			ts.pushRun(left, runLength);
			ts.mergeCollapse();
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

	private static <T extends Comparable<T>> int extendRun(T[] source, int low, int high) {
		assert low < high;
		int runHigh = low + 1;
		if (runHigh == high) {
			return 1;
		}

//		boolean runIsStrictlyDescending = less(source[runHigh++], source[low]);
//		if (runIsStrictlyDescending) {
//			while (runHigh < high && less(source[runHigh], source[runHigh - 1])) {
//				runHigh++;
//			}
//			reverseRange(source, low, runHigh);
//		} else {
//			while (runHigh < high && !less(source[runHigh], source[runHigh - 1])) {
//				runHigh++;
//			}
//		}
//
//		boolean shouldExtendRun = (runHigh - low) < MINIMUM_RUN_LENGTH && MINIMUM_RUN_LENGTH + low < high;
//		if (shouldExtendRun) {
//			InsertionSort.sort(source, low, low + MINIMUM_RUN_LENGTH);
//			return MINIMUM_RUN_LENGTH;
//		}

		return 1; //runHigh - low;
	}

	private static <T extends Comparable<T>> void reverseRange(T[] source, int lo, int hi) {
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
		Timsort.sort(input, 0, input.length);
		for (Double d : input) {
			System.out.println(d);
		}
	}

	private void pushRun(int runStart, int runLength) {
		this.runStart[stackSize] = runStart;
		this.runLength[stackSize] = runLength;
		stackSize++;
	}

	private void mergeCollapse() {
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
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && n + 1 < stackSize && runLength[n - 1] < runLength[n + 1]) {
				n--;
			}
			mergeAt(n);
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
}