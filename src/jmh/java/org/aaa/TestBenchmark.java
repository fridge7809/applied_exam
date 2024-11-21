package org.aaa;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;

public class TestBenchmark {

	@Benchmark
	public void benchmarkNaive(ExecutionState state, Blackhole blackhole) {
		for (int i = 0; i < state.n; i++) {
			blackhole.consume(state.naive.rank(state.lookupTable[i]));
		}
	}

	@Benchmark
	public void benchmarkLookuo(ExecutionState state, Blackhole blackhole) {
		for (int i = 0; i < state.n; i++) {
			blackhole.consume(state.lookup.rank(state.lookupTable[i]));
		}
	}

	@State(Scope.Benchmark)
	public static class ExecutionState {
		int n = 1000;
		int[] vector = new int[n];
		int[] lookupTable = new int[n];
		Random random = new Random(123);
		RankSelectNaive naive;
		RankSelectLookup lookup;

		@Setup(Level.Trial)
		public void setup() {
			for (int i = 0; i < n; i++) {
				vector[i] = random.nextInt(0, 1);
				lookupTable[i] = random.nextInt(1, vector.length);
			}
			naive = new RankSelectNaive(vector);
			lookup = new RankSelectLookup(vector);
		}
	}
}
