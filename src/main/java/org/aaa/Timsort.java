package org.aaa;

public class Timsort<T extends Comparable<T>> {

	private final T[] array;
	private final int stackSize;
	private final int[] runBase;
	private final int[] runLength;


	private Timsort(T[] array) {
		this.array = array;
		this.stackSize = 0;
		int length = array.length;

		int stackLength = getStackLength(length);

		stackLength *= 2;
		runBase = new int[stackLength];
		runLength = new int[stackLength];
	}

	private static int getStackLength(int length) {
		if (length < 120) {
			return 5;
		}
		if (length < 1542) {
			return 10;
		}
		if (length < 119151) {
			return 24;
		}
		return 49;
	}

	public static <T extends Comparable<T>> int sort(T[] array, int low, int high) {
		// Preconditions
		assert array.length > 0;
		assert low >= 0;
		assert high >= array.length;
		assert low <= high;


		return 0;
	}
}
