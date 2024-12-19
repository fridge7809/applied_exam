package org.aaa;

import org.aaa.DataGenerator.Distribution;
import org.aaa.DataGenerator.InputType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.aaa.DataGenerator.StringContent.VARIED_LENGTH;
import static org.aaa.DataGenerator.generateDataOfType;

public class HorseRace {

	static StringBuilder builder = new StringBuilder();
	static ExecutorService service = Executors.newCachedThreadPool();
	static int WARMUPS = 1;

	public static void main(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException("Usage: java HorseRace <inputsizeRangeCommaSeperated> <iterations> \n example java HorseRace 16384,32768,65536,131072 5");
		}

		// 2^14..2^16
		String[] inputSize = args[0].split(",");
		double[] inputSizes = Arrays.stream(inputSize).map(Double::parseDouble).mapToDouble(d -> d).toArray();

		int iterations = Integer.parseInt(args[1]);

		System.out.println("task 10 start:  " + " inputsizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations);
		String header = "algorithm,n,time";
		builder.append(header).append("\n");


		for (double n : inputSizes) {
			int ni = (int) n;
			int runs = 25;

			List<Comparable[]> stairCase = new ArrayList<>();
			for (int i = 0; i < iterations; i++) {
				stairCase.add(generateDataOfType(InputType.INTS, Distribution.PRESORTED, VARIED_LENGTH, ni, runs));
			}

			int cutoff = 16;

			// RUN EXPLORATIONS
			benchmark("LevelSortAdaptive", iterations, ni, stairCase, currentData -> Timsort.sort(currentData, MergeRule.LEVELSORT, true, cutoff));
			benchmark("BinomialSortAdaptive", iterations, ni, stairCase, currentData -> Timsort.sort(currentData, MergeRule.BINOMIALSORT, true, cutoff));
			benchmark("EqualLengthAdaptive", iterations, ni, stairCase, currentData -> Timsort.sort(currentData, MergeRule.EQUALLENGTH, true, cutoff));
			// NON ADAPTIVE
			benchmark("LevelSort", iterations, ni, stairCase, currentData -> Timsort.sort(currentData, MergeRule.LEVELSORT, false, cutoff));
			benchmark("BinomialSort", iterations, ni, stairCase, currentData -> Timsort.sort(currentData, MergeRule.BINOMIALSORT, false, cutoff));
			benchmark("EqualLength", iterations, ni, stairCase, currentData -> Timsort.sort(currentData, MergeRule.EQUALLENGTH, false, cutoff));
			// OTHERS
			benchmark("InsertionSort", iterations, ni, stairCase, currentData -> InsertionSort.sort(currentData));
			benchmark("JavaArraysSort", iterations, ni, stairCase, currentData -> {
				Arrays.sort(currentData);
				return 0;
			});
			benchmark("MergeSort", iterations, ni, stairCase, currentData -> MergeSort.sort(currentData, 16, true));

			System.out.println("task 10 progress:    completed benchmark: " + "N:" + ni);
		}

		saveResults("");
	}

	public static void benchmark(
			String algorithmName,
			int iterations,
			int n,
			List<Comparable[]> data,
			Function<Comparable[], Integer> taskGenerator) {
		try {
			long[] times = new long[iterations];
			for (int i = 0; i < data.size() + WARMUPS; i++) {
				Comparable[] currentData = (i < WARMUPS) ? data.getFirst() : data.get(i - WARMUPS);
				Comparable[] toSort = new Comparable[currentData.length];
				System.arraycopy(currentData, 0, toSort, 0, currentData.length);

				Callable<Integer> callable = new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						return taskGenerator.apply(toSort);
					}
				};
				Future<Integer> value = service.submit(callable);
				try {
					long startTime = System.nanoTime();
					value.get(30, TimeUnit.SECONDS);
					long endTime = System.nanoTime();
					if (i >= WARMUPS) {
						times[i - WARMUPS] = endTime - startTime;
					}
				} catch (TimeoutException e) {
					System.out.println("Timed out for N:" + n + " for: " + algorithmName);
				}
			}

			Arrays.sort(times);

			long medianTime = times[times.length / 2];

			String result = String.join(",",
					algorithmName,
					Integer.toString(n),
					Double.toString(medianTime / 1_000_000d));

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
		try (FileWriter fw = new FileWriter("./output/task10" + algorithmName + "results.csv")) {
			fw.write(builder.toString());
		} catch (IOException e) {
			throw new RuntimeException("Error writing to file", e);
		}
	}

}
