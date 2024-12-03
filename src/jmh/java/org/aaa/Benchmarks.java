package org.aaa;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Benchmarks {

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkInts(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.comparisons += MergeSortAugmented.sort(state.intsRandom, state.c));
	}

	@State(Scope.Benchmark)
	public static class ExecutionState {

		@Param({"10000", "100000", "1000000"})
		int n;

		@Param({"5", "10", "20"})
		int c;

		Random random = new Random(1000);
		Integer[] intsRandom;
		int comparisons;

		@Setup(Level.Trial)
		public void setup() {
			intsRandom = new Integer[n];
		}

		@Setup(Level.Iteration)
		public void setupIteration() {
			comparisons = 0;
		}

		@TearDown(Level.Iteration)
		public void tearDown() {
			File file = new File("test2.md");
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
