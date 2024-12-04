package org.aaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BenchmarkMergesort {

	static StringBuilder builder = new StringBuilder();
	static int WARMUPS = 5;

	public static void main(String[] args) {
		ExecutionState state = new ExecutionState();
		double[] inputSizes = {Math.pow(10d, 3d), Math.pow(10d, 4d), Math.pow(10d, 5d)};
		int iterations = 5;

		String header = "name,comparisons,time,unit";
		System.out.println(header);
		builder.append(header).append("\n");

		for (double n : inputSizes) {
			int ni = (int) n;
			state.n = ni;
			state.setup();

			benchmark("IntsUniform", iterations, () -> state.incrementComp(MergeSort.sort(state.intsUniform)));
			benchmark("IntsAscending", iterations, () -> state.incrementComp(MergeSort.sort(state.intsAscending)));
			benchmark("IntsDescending", iterations, () -> state.incrementComp(MergeSort.sort(state.intsDescending)));
			benchmark("StringsFixedLength", iterations, () -> state.incrementComp(MergeSort.sort(state.stringsFixedLength)));
			benchmark("StringsVariedLength", iterations, () -> state.incrementComp(MergeSort.sort(state.stringsVariedLength)));
			benchmark("StringsFixedPrefix", iterations, () -> state.incrementComp(MergeSort.sort(state.stringsFixedPrefix)));

		}

		saveResults();

		System.out.println("done, results written to file results.csv");
	}

	public static void benchmark(String name, int iterations, Runnable task) {
		long sumTime = 0;
		for (int i = 0; i < iterations + WARMUPS; i++) {
			long startTime = System.nanoTime();
			task.run();
			long endTime = System.nanoTime();
			if (i > WARMUPS) {
				sumTime += endTime - startTime;
			}
		}
		sumTime /= iterations;
		String result = name + "," + ExecutionState.getComparisons() / iterations + "," + sumTime / 1_000_000d + "," + "ms";
		System.out.println(result);
		builder.append(result).append(System.lineSeparator());
		ExecutionState.resetComp();
	}

	private static void saveResults() {
		File file = new File("./output");
		if (!file.exists()) {
			file.mkdir();
		}
		try (FileWriter fw = new FileWriter("./output/results.csv")) {
			fw.write(builder.toString());
		} catch (IOException e) {
			throw new RuntimeException("Error writing to file", e);
		}
	}

	public static class ExecutionState {

		static int comparisons = 0;
		int n;
		// Int arrays
		Integer[] intsUniform;
		Integer[] intsAscending;
		Integer[] intsDescending;
		Double[] intsGaussian;
		Double[] intsExponential;
		// String arrays
		String[] stringsFixedLength;
		String[] stringsVariedLength;
		String[] stringsFixedPrefix;
		Random random = new Random(1000);

		public static void resetComp() {
			comparisons = 0;
		}

		public static int getComparisons() {
			return comparisons;
		}

		public void incrementComp(int amount) {
			comparisons += amount;
		}

		public void setup() {
			intsUniform = new Integer[n];
			intsAscending = new Integer[n];
			intsGaussian = new Double[n];
			intsExponential = new Double[n];

			byte[] bytesFixed = new byte[100];
			stringsFixedLength = new String[n];
			stringsVariedLength = new String[n];
			stringsFixedPrefix = new String[n];

			for (int i = 0; i < n; i++) {
				// Numeral data
				intsAscending[i] = i;
				intsUniform[i] = random.nextInt();
				intsGaussian[i] = random.nextGaussian();
				intsExponential[i] = random.nextDouble() * 10;

				// String data
				int length = random.nextInt(300);
				byte[] bytesVaried = new byte[length];
				random.nextBytes(bytesFixed);
				random.nextBytes(bytesVaried);
				stringsFixedLength[i] = new String(bytesFixed);
				stringsFixedPrefix[i] = "aaaa" + new String(bytesVaried);
				stringsVariedLength[i] = new String(bytesVaried);
			}

			// Reverse ascending to create descending
			List<Integer> ints = Arrays.asList(intsAscending);
			Collections.reverse(ints);
			intsDescending = ints.toArray(new Integer[0]);
		}
	}
}