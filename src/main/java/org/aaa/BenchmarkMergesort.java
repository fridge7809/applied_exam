package org.aaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.aaa.DataGenerator.*;

public class BenchmarkMergesort {

	static StringBuilder builder = new StringBuilder();

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
			for (InputType inputType : InputType.values()) {
				for (Distribution distribution : Distribution.values()) {
					for (StringContent content : StringContent.values()) {
						benchmark(inputType.name(), n, iterations, () -> MergeSort.sort(generateDataOfType(inputType, distribution, content, n)));
						benchmark(inputType.name() + "TEST", n, iterations, () -> MergeSortParallel.sortParallel(generateDataOfType(inputType, distribution, content, n), 40));
					}
				}
			}

			System.out.println("task 2 progress:    completed N:    " + n);
		}

		saveResults();
		System.out.println("task 2 done:    results written to ./output/task2results.csv");
	}

	public static void benchmark(String name, int n, int iterations, Callable<?> task) throws Exception {
		int[] comparisons = new int[iterations];
		long[] times = new long[iterations];
		for (int i = 0; i < iterations; i++) {
			long startTime = System.nanoTime();
			int count = (int) task.call();
			long endTime = System.nanoTime();
			comparisons[i] = count;
			times[i] = (endTime - startTime);
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