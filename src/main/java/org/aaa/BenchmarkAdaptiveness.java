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

public class BenchmarkAdaptiveness {

	static StringBuilder builder = new StringBuilder();
	static int WARMUPS = 1;

	public static void main(String[] args) {
		if (args.length != 3) {
			throw new IllegalArgumentException("Usage: java BenchmarkAdaptiveness <inputsize> <iterations> <exponentUpperBound> \n example java BenchmarkAdaptiveness 100000 5 15");
		}

		// 65536 5 13
		double inputSize = Double.parseDouble(args[0]);
		double[] inputSizes = new double[]{inputSize};
		int exponent = Integer.parseInt(args[2]);
		int[] range = IntStream.range(0, exponent + 1).asDoubleStream().map(i -> Math.pow(2, i)).mapToInt(d -> (int) d).toArray();

		int iterations = Integer.parseInt(args[1]);

		System.out.println("task 9 start:   for: " + ", inputsizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations + " " + "parameter upper bound: " + (exponent));
		String header = "mergerule,isadaptive,inputdistribution,c,comparisons,time";
		builder.append(header).append("\n");


		for (double n : inputSizes) {
			int ni = (int) n;
			List<Comparable[]> adaptive = new ArrayList<>();
			for (int i = 0; i < iterations; i++) {
				adaptive.add(generateDataOfType(InputType.INTS, Distribution.ADAPTIVE, VARIED_LENGTH, ni));
			}
			List<Comparable[]> uniform = new ArrayList<>();
			for (int i = 0; i < iterations; i++) {
				uniform.add(generateDataOfType(InputType.INTS, Distribution.UNIFORM, VARIED_LENGTH, ni));
			}

			for (int cutoff : range) {
				for (MergeRule rule : MergeRule.values()) {
					benchmark(
							iterations,
							rule,
							false,
							Distribution.UNIFORM,
							cutoff,
							ni,
							adaptive,
							(currentData) -> Timsort.sort(currentData, rule, false, cutoff)
					);

					benchmark(
							iterations,
							rule,
							true,
							Distribution.UNIFORM,
							cutoff,
							ni,
							uniform,
							(currentData) -> Timsort.sort(currentData, rule, true, cutoff)
					);

					benchmark(
							iterations,
							rule,
							true,
							Distribution.ADAPTIVE,
							cutoff,
							ni,
							adaptive,
							(currentData) -> Timsort.sort(currentData, rule, true, cutoff)
					);

					benchmark(
							iterations,
							rule,
							false,
							Distribution.ADAPTIVE,
							cutoff,
							ni,
							uniform,
							(currentData) -> Timsort.sort(currentData, rule, false, cutoff)
					);
				}
				System.out.println("task 9 progress:    completed benchmark for algorithm: " + ", iterations: " + iterations + " " + "threshold: " + cutoff);
			}
		}

		saveResults("");
	}

	public static void benchmark(
			int iterations,
			MergeRule rule,
			boolean isAdaptive,
			Distribution dist,
			int cutoff,
			int n,
			List<Comparable[]> data,
			Function<Comparable[], Integer> taskGenerator) {
		try {
			int[] comparisons = new int[iterations];
			long[] times = new long[iterations];
			for (int i = 0; i < data.size() + WARMUPS; i++) {
				Comparable[] currentData = (i < WARMUPS) ? data.getFirst() : data.get(i - WARMUPS);
				Comparable[] toSort = new Comparable[currentData.length];
				System.arraycopy(currentData, 0, toSort, 0, currentData.length);
				long startTime = System.nanoTime();
				int count = taskGenerator.apply(toSort);
				long endTime = System.nanoTime();
				if (i >= WARMUPS) {
					times[i - WARMUPS] = endTime - startTime;
					comparisons[i - WARMUPS] = count;
				}
			}

			Arrays.sort(comparisons);
			Arrays.sort(times);

			int medianComparisons = comparisons[comparisons.length / 2];
			long medianTime = times[times.length / 2];

			String result = String.join(",",
					rule.name(),
					Boolean.toString(isAdaptive),
					dist.toString(),
					Integer.toString(cutoff),
					Integer.toString(medianComparisons),
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
		try (FileWriter fw = new FileWriter("./output/task9" + algorithmName + "results.csv")) {
			fw.write(builder.toString());
		} catch (IOException e) {
			throw new RuntimeException("Error writing to file", e);
		}
	}

}
