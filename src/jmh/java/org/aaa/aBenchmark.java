package org.aaa;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;

public class aBenchmark {

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void benchmarkA(ExecutionState state, Blackhole blackhole) {
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

	}
}
