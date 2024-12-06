package org.aaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class BenchmarkMergesort {

	static StringBuilder builder = new StringBuilder();
	static int WARMUPS = 5;

	public static void main(String[] args) {
		ExecutionState state = new ExecutionState();
		if (args.length != 3) {
			throw new IllegalArgumentException("Usage: java BenchmarkMergesort <10,100,1000...N> <iterations> <warmups>" +
					"\n example: java BenchmarkMergesort 100,200,300 10 5");
		}
		String[] tokens = args[0].split(",");
		int[] inputSizes = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			inputSizes[i] = Integer.parseInt(tokens[i]);
		}
		int iterations = Integer.parseInt(args[1]);
		WARMUPS = Integer.parseInt(args[2]);

		System.out.println("task 2 start:   problem sizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations + " warmups: " + WARMUPS);
		String header = "name,comparisons,time,unit";
		builder.append(header).append("\n");
		for (int n : inputSizes) {
			System.out.println("task 2 progress:    starting N: " + n);
			benchmark("IntsUniform", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.intsUniform)));
			benchmark("IntsAscending", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.intsAscending)));
			benchmark("IntsDescending", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.intsDescending)));
			benchmark("StringsFixedLength", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.stringsFixedLength)));
			benchmark("StringsVariedLength", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.stringsVariedLength)));
			benchmark("StringsFixedPrefix", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.stringsFixedPrefix)));
			System.out.println("task 2 progress:    completed N:    " + n);
		}

		saveResults();

		System.out.println("task 2 done:    results written to ./output/task2results.csv");
	}

	public static void benchmark(String name, int n, int iterations, Runnable task) {
		long sumTime = 0;
		for (int i = 0; i < iterations + WARMUPS; i++) {
			ExecutionState.generateNewData(n);
			long startTime = System.nanoTime();
			task.run();
			long endTime = System.nanoTime();
			if (i > WARMUPS) {
				sumTime += endTime - startTime;
			}
		}
		sumTime /= iterations;
		String result = name + "," + ExecutionState.getComparisons() / iterations + "," + sumTime / 1_000_000d + "," + "ms";
		// System.out.println(result);
		builder.append(result).append(System.lineSeparator());
		ExecutionState.resetComp();
	}

	private static void saveResults() {
		File file = new File("./output");
		if (!file.exists()) {
			file.mkdir();
		}
		try (FileWriter fw = new FileWriter("./output/task2results.csv")) {
			fw.write(builder.toString());
		} catch (IOException e) {
			throw new RuntimeException("Error writing to file", e);
		}
	}

}