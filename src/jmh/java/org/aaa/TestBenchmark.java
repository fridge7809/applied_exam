package org.aaa;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestBenchmark {

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkIntsRandom(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.comparisons += MergeSort.sort(state.intsRandom));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkIntsRandomAsc(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.comparisons += MergeSort.sort(state.intsRandomAsc));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkIntsRandomDesc(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.comparisons += MergeSort.sort(state.intsRandomDesc));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkStringsRandomLengthN(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.comparisons += MergeSort.sort(state.stringsLengthN));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkStringsRandomVariedLength(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.comparisons += MergeSort.sort(state.stringsVariedLength));
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkStringsFixedPrefix(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.comparisons += MergeSort.sort(state.stringsFixedPrefix));
	}

	@State(Scope.Benchmark)
	public static class ExecutionState {
		@Param({"10000", "100000", "1000000"})
		int n;
		// Int arrays
		Integer[] intsRandom;
		Integer[] intsRandomAsc;
		Integer[] intsRandomDesc;
		// String arrays
		String[] stringsLengthN;
		String[] stringsVariedLength;
		String[] stringsFixedPrefix;

		Random random = new Random(1000);
		int comparisons;

		@Setup(Level.Trial)
		public void setup() {
			intsRandom = new Integer[n];
			intsRandomAsc = new Integer[n];

			byte[] bytesFixed = new byte[n];
			stringsLengthN = new String[n];
			stringsVariedLength = new String[n];
			stringsFixedPrefix = new String[n];

			for (int i = 0; i < n; i++) {
				intsRandom[i] = random.nextInt();
				intsRandomAsc[i] = i;

				// String stuff
				byte[] bytesRandom = new byte[200];
				random.nextBytes(bytesFixed);
				random.nextBytes(bytesRandom);
				stringsLengthN[i] = new String(bytesFixed);
				stringsVariedLength[i] = new String(bytesRandom);
				stringsFixedPrefix[i] = "aaaa" + new String(bytesRandom);
			}

			// Reverse ascending to create descending
			List<Integer> ints = Arrays.asList(intsRandom);
			Collections.reverse(ints);
			intsRandomDesc = ints.toArray(new Integer[0]);
		}

		@Setup(Level.Iteration)
		public void setupIteration() {
			comparisons = 0;
		}

		@TearDown(Level.Iteration)
		public void tearDown() {
			File file = new File("test.md");
			try {
				FileWriter fw = new FileWriter(file);
				fw.append("n: " + n + " comparisons: " + comparisons);
				fw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			comparisons = 0;
		}
	}
}
