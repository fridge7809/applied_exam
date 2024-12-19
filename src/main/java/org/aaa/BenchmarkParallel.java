package org.aaa;

import org.aaa.DataGenerator.Distribution;
import org.aaa.DataGenerator.InputType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.aaa.DataGenerator.StringContent.VARIED_LENGTH;
import static org.aaa.DataGenerator.generateDataOfType;

public class BenchmarkParallel {

	static StringBuilder builder = new StringBuilder();
	static int WARMUPS = 2;

	public static void main(String[] args) {
		if (args.length != 3) {
			throw new IllegalArgumentException("Usage: java BenchmarkParallel <inputsizeCommaSeperated> <iterations> <parallelCutOffRange> \n example java BenchmarkParallel 100000 15 1,100");
		}

		// 65536 5 13
		String[] sizes = args[0].split(",");

		double[] inputSizes = new double[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			inputSizes[i] = Double.parseDouble(sizes[i]);
		}

		String[] tokens = args[2].split(",");
		int[] cutoffRange = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			cutoffRange[i] = Integer.parseInt(tokens[i]);
		}


		int iterations = Integer.parseInt(args[1]);

		System.out.println("task 12 start:   for: " + ", inputsizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations);
		String header = "algorithm,n,time,parallelcutoff";
		builder.append(header).append("\n");


		for (double n : inputSizes) {
			int ni = (int) n;
			List<Comparable[]> uniform = new ArrayList<>();
			for (int i = 0; i < iterations; i++) {
				uniform.add(generateDataOfType(InputType.INTS, Distribution.UNIFORM, VARIED_LENGTH, ni, 25));
			}

			IntStream.range(cutoffRange[0], cutoffRange[1]).forEach(i -> {
				benchmark(iterations, ni, uniform, (currentData) -> MergeSortParallel.sortParallel(currentData, i), "MergeSortParallel", i);
			});

			benchmark(iterations, ni, uniform, (currentData) -> MergeSort.sort(currentData), "MergeSort", 0);
			benchmark(iterations, ni, uniform, (currentData) -> MergeSort.sort(currentData, 16, true), "MergeSortWithInsertion", 0);

			System.out.println("task 12 progress:    N: " + ni + "   iterations: " + iterations);
		}

		saveResults("");
	}

	public static void benchmark(
			int iterations,
			int n,
			List<Comparable[]> data,
			Function<Comparable[], Integer> taskGenerator, String name, int cutoff) {
		try {
			long[] times = new long[iterations];
			for (int i = 0; i < data.size() + WARMUPS; i++) {
				Comparable[] currentData = (i < WARMUPS) ? data.getFirst() : data.get(i - WARMUPS);
				Comparable[] toSort = new Comparable[currentData.length];
				System.arraycopy(currentData, 0, toSort, 0, currentData.length);
				long startTime = System.nanoTime();
				taskGenerator.apply(toSort);
				long endTime = System.nanoTime();
				if (i >= WARMUPS) {
					times[i - WARMUPS] = endTime - startTime;
				}
			}

			Arrays.sort(times);

			long medianTime = times[times.length / 2];

			String result = String.join(",",
					name,
					Integer.toString(n),
					Double.toString(medianTime / 1_000_000d),
					Integer.toString(cutoff));

			builder.append(result).append(System.lineSeparator());
		} catch (Exception e) {
			throw new RuntimeException("Benchmark failed", e);
		}
	}


	private static void saveResults(String algorithmName) {
		File file = new File("./output");
		if (!file.exists()) {
			file.mkdir();
		}
		try (FileWriter fw = new FileWriter("./output/task12" + algorithmName + "results.csv")) {
			fw.write(builder.toString());
		} catch (IOException e) {
			throw new RuntimeException("Error writing to file", e);
		}
	}

}
