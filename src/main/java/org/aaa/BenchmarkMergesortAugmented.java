package org.aaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

public class BenchmarkMergesortAugmented {

	static StringBuilder builder = new StringBuilder();
	static int WARMUPS = 10;

	public static void main(String[] args) {
		if (args.length != 3) {
			throw new IllegalArgumentException("Usage: java BenchmarkMergesortAugmented <algorithm> <iterations> <parameterUpperBound> \n example java BenchmarkMergesortAugmented timsort 200 40");
		}
		String algorithm = args[0];
		if (!algorithm.equals("mergesort") && !algorithm.equals("timsort")) {
			throw new IllegalArgumentException("Invalid algorithm: " + algorithm + "\n valid algorithms: <mergesort,timsort>");
		}

		ExecutionState state = new ExecutionState();
		double[] inputSizes = {Math.pow(10d, 4d)};
		int c = Integer.parseInt(args[2]);
		int[] range = IntStream.range(0, c + 1).toArray();

		int iterations = Integer.parseInt(args[1]);

		System.out.println("task 4 start:   for: " + algorithm + ", inputsizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations + " " + "parameter upper bound: " + (c));
		String header = "name,time,c";
		//System.out.println(header);
		builder.append(header).append("\n");

		for (double n : inputSizes) {
			int ni = (int) n;
			ExecutionState.setup(ni);

			for (int threshold : range) {
				// this could be a lot prettier with an interface,
				// but the algorithms have different apis,
				// and we would rather focus our time on the algorithms
				// rather than best practice java inheritance
				if (algorithm.equals("timsort")) {
					benchmark("IntsUniform", ni, iterations, () -> Timsort.sort(ExecutionState.intsUniform, 0, ExecutionState.intsUniform.length, threshold, MergeRule.LENGTHTWO, false), threshold);
					benchmark("StringsVariedLength", ni, iterations, () -> Timsort.sort(ExecutionState.stringsVariedLength, 0, ExecutionState.stringsVariedLength.length, threshold, MergeRule.LENGTHTWO, false), threshold);
				} else {
					benchmark("IntsUniform", ni, iterations, () -> MergeSortAugmented.sort(ExecutionState.intsUniform, threshold), threshold);
					benchmark("StringsVariedLength", ni, iterations, () -> MergeSortAugmented.sort(ExecutionState.stringsVariedLength, threshold), threshold);
				}
				System.out.println("task 4 progress:    completed benchmark for algorithm: " + algorithm + ", iterations: " + iterations + " " + "threshold: " + threshold);
			}
		}

		saveResults(algorithm);
	}

	public static void benchmark(String name, int n, int iterations, Runnable task, int threshold) {
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
		String result = name + "," + sumTime / 1_000_000d + "," + threshold;
		//System.out.println(result);
		builder.append(result).append(System.lineSeparator());
	}

	private static void saveResults(String algorithmName) {
		File file = new File("./output");
		if (!file.exists()) {
			file.mkdir();
		}
		try (FileWriter fw = new FileWriter("./output/task4" + algorithmName + "results.csv")) {
			fw.write(builder.toString());
		} catch (IOException e) {
			throw new RuntimeException("Error writing to file", e);
		}
	}

}