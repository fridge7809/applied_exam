//package org.aaa;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Arrays;
//
//public class BenchmarkTimsort {
//
//	static StringBuilder builder = new StringBuilder();
//
//	public static void main(String[] args) {
//		ExecutionState state = new ExecutionState();
//		if (args.length != 2) {
//			throw new IllegalArgumentException("Usage: java BenchmarkMergesort <10,100,1000...N> <iterations> " +
//					"\n example: java BenchmarkMergesort 100,200,300 10");
//		}
//		String[] tokens = args[0].split(",");
//		int[] inputSizes = new int[tokens.length];
//		for (int i = 0; i < tokens.length; i++) {
//			inputSizes[i] = Integer.parseInt(tokens[i]);
//		}
//		int iterations = Integer.parseInt(args[1]);
//
//		System.out.println("task 9 start:   problem sizes: " + Arrays.toString(inputSizes) + " iterations: " + iterations);
//		String header = "name,isAdaptive,comparisons,n,r,c,time";
//		builder.append(header).append("\n");
//		for (int n : inputSizes) {
//			ExecutionState.setup(n);
//			System.out.println("task 9 progress:    starting N: " + n);
//			benchmark("IntsUniform", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.intsUniform)));
//			benchmark("IntsUniform", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.intsUniform)));
//			benchmark("IntsUniform", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.intsUniform)));
//			benchmark("IntsUniform", n, iterations, () -> state.incrementComp(MergeSort.sort(ExecutionState.intsUniform)));
//			System.out.println("task 2 progress:    completed N:    " + n);
//		}
//
//		saveResults();
//
//		System.out.println("task 2 done:    results written to ./output/task2results.csv");
//	}
//
//	public static void benchmark(String name, int n, int iterations, Runnable task) {
//		long sumTime = 0;
//		for (int i = 0; i < iterations; i++) {
//			ExecutionState.generateNewData(n);
//			long startTime = System.nanoTime();
//			task.run();
//			long endTime = System.nanoTime();
//			sumTime += (endTime - startTime);
//		}
//		sumTime /= (double) iterations;
//		String result = name + "," + ExecutionState.getComparisons() / iterations + "," + sumTime / 1_000_000d + "," + "ms";
//		// System.out.println(result);
//		builder.append(result).append(System.lineSeparator());
//		ExecutionState.resetComp();
//	}
//
//	private static void saveResults() {
//		File file = new File("./output");
//		if (!file.exists()) {
//			file.mkdir();
//		}
//		try (FileWriter fw = new FileWriter("./output/task2results.csv")) {
//			fw.write(builder.toString());
//		} catch (IOException e) {
//			throw new RuntimeException("Error writing to file", e);
//		}
//	}
//
//}