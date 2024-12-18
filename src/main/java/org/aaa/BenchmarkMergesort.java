package org.aaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.aaa.DataGenerator.*;

public class BenchmarkMergesort {

	static StringBuilder builder = new StringBuilder();
	static int WARMUPS = 5;

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException("Usage: java BenchmarkMergesort <10,100,1000...N> <iterations> " +
					"\n example: java BenchmarkMergesort 100,200,300 10");
		}
		String[] tokens = args[0].split(",");
		int[] inputSizes = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			inputSizes[i] = Integer.parseInt(tokens[i]);
		}
		int iterations = Integer.parseInt(args[1]);

		System.out.println("task 2 start:   problem sizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations);
		String header = "name,comparisons,time,n,unit";
		builder.append(header).append("\n");

		for (int n : inputSizes) {
			System.out.println("task 2 progress:    starting N:     " + n);

			// the parameters for generatedataoftype are misleading, beware
			for (Distribution distribution : Distribution.values()) {
				benchmark(InputType.INTS.name() + "_" + distribution, n, iterations, () -> MergeSort.sort(generateDataOfType(InputType.INTS, distribution, StringContent.FIXED_LENGTH, n)));
			}

			for (StringContent content : StringContent.values()) {
				benchmark(InputType.STRINGS.name() + "_" + content, n, iterations, () -> MergeSort.sort(generateDataOfType(InputType.STRINGS, Distribution.ASCENDING, content, n)));
			}

			System.out.println("task 2 progress:    completed N:    " + n);
		}

		saveResults();
		System.out.println("task 2 done:    results written to ./output/task2results.csv");
	}

	public static void benchmark(String name, int n, int iterations, Callable<?> task) throws Exception {
		int[] comparisons = new int[iterations];
		long[] times = new long[iterations];
		for (int i = 0; i < iterations + WARMUPS; i++) {
			long startTime = System.nanoTime();
			int count = (int) task.call();
			long endTime = System.nanoTime();
			if (i > WARMUPS - 1) {
				comparisons[i - WARMUPS] = count;
				times[i - WARMUPS] = (endTime - startTime);
			}
		}
		Arrays.sort(comparisons);
		Arrays.sort(times);
		long medianComps = comparisons[(comparisons.length / 2) % 2 == 0 ? comparisons.length / 2 + 1 : comparisons.length / 2];
		long medianTime = times[(times.length / 2) % 2 == 0 ? times.length / 2 + 1 : times.length / 2];

		String result = name + "," + medianComps + "," + medianTime / 1_000_000d + "," + n + "," + "ms";
		builder.append(result).append(System.lineSeparator());
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