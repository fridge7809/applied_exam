package org.aaa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ExecutionState {

	static int comparisons = 0;
	static Integer[] intsUniform;
	static Integer[] intsAscending;
	static Integer[] intsDescending;
	static Double[] intsGaussian;
	static Double[] intsExponential;
	static String[] stringsFixedLength;
	static String[] stringsVariedLength;
	static String[] stringsFixedPrefix;
	static Random random = new Random(1000);

	public static void resetComp() {
		comparisons = 0;
	}

	public static int getComparisons() {
		return comparisons;
	}

	public static void generateNewData(int n) {
		byte[] bytesFixed = new byte[100];
		for (int i = 0; i < n; i++) {
			intsAscending[i] = i;
			intsUniform[i] = random.nextInt();
			intsGaussian[i] = random.nextGaussian();
			intsExponential[i] = random.nextDouble() * 10;

			int length = random.nextInt(300);
			byte[] bytesVaried = new byte[length];
			random.nextBytes(bytesFixed);
			random.nextBytes(bytesVaried);
			stringsFixedLength[i] = new String(bytesFixed);
			stringsFixedPrefix[i] = "aaaa" + new String(bytesVaried);
			stringsVariedLength[i] = new String(bytesVaried);
		}

		List<Integer> ints = Arrays.asList(intsAscending);
		Collections.reverse(ints);
		intsDescending = ints.toArray(new Integer[0]);
	}

	public static void setup(int n) {
		intsUniform = new Integer[n];
		intsAscending = new Integer[n];
		intsGaussian = new Double[n];
		intsExponential = new Double[n];

		stringsFixedLength = new String[n];
		stringsVariedLength = new String[n];
		stringsFixedPrefix = new String[n];
	}

	public void incrementComp(int amount) {
		comparisons += amount;
	}
}
