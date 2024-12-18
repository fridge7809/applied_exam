package org.aaa;

import org.aaa.DataGenerator.Distribution;
import org.aaa.DataGenerator.InputType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static org.aaa.DataGenerator.StringContent.VARIED_LENGTH;
import static org.aaa.DataGenerator.generateDataOfType;

public class BenchmarkMergesortAugmented {

	static StringBuilder builder = new StringBuilder();
	static int WARMUPS = 20;

	public static void main(String[] args) {
		if (args.length != 3) {
			throw new IllegalArgumentException("Usage: java BenchmarkMergesortAugmented <algorithm> <iterations> <parameterUpperBound> \n example java BenchmarkMergesortAugmented timsort 200 40");
		}
		String algorithm = args[0];
		if (!algorithm.equals("mergesort") && !algorithm.equals("timsort")) {
			throw new IllegalArgumentException("Invalid algorithm: " + algorithm + "\n valid algorithms: <mergesort,timsort>");
		}

		double[] inputSizes = {65_536D};
		int c = Integer.parseInt(args[2]);
		int[] range = IntStream.range(2, c + 1).toArray();

		int iterations = Integer.parseInt(args[1]);

		System.out.println("task 4 start:   for: " + algorithm + ", inputsizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations + " " + "parameter upper bound: " + (c));
		String header = "name,time,c";
		builder.append(header).append("\n");

		for (double n : inputSizes) {
			int ni = (int) n;

			for (int cutoff : range) {
					if (algorithm.equals("timsort")) {
						benchmark(InputType.INTS.name(), ni, iterations, () -> Timsort.sort(generateDataOfType(InputType.INTS, Distribution.UNIFORM, VARIED_LENGTH, ni), MergeRule.LENGTHTWO, false, cutoff), cutoff);
					} else if (((cutoff & (cutoff - 1)) == 0)) { // powers of two
						benchmark(InputType.INTS.name(), ni, iterations, () -> MergeSort.sort(generateDataOfType(InputType.INTS, Distribution.UNIFORM, VARIED_LENGTH, ni), cutoff, true), cutoff);
					}

				System.out.println("task 4 progress:    completed benchmark for algorithm: " + algorithm + ", iterations: " + iterations + " " + "threshold: " + cutoff);
			}
		}

		saveResults(algorithm);
	}

	public static void benchmark(String name, int n, int iterations, Callable<Integer> task, int threshold) {
		try {
			long[] times = new long[iterations];
			for (int i = 0; i < iterations + WARMUPS; i++) {
				long startTime = System.nanoTime();
				task.call();
				if (i > WARMUPS - 1) {
					long endTime = System.nanoTime();
					times[i - WARMUPS] = (endTime - startTime);
				}
			}
			Arrays.sort(times);
			long medianTime = times[(times.length / 2) % 2 == 0 ? times.length / 2 + 1 : times.length / 2];

			String result = name + "," + medianTime / 1_000_000d + "," + threshold;
			builder.append(result).append(System.lineSeparator());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
