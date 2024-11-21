package org.aaa;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.Tuple.Tuple3;
import net.jqwik.api.constraints.WithNull;

import java.util.Arrays;
import java.util.BitSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class RankSelectTest {

	@Property
	void testRankOperation_yieldsIdenticalResultsAcrossImplementations(@ForAll("zeroArrWithAtLeastOneOne") Tuple2<int[], Integer> input) {
		int[] arr = input.get1();
		assert arr != null;
		long[] arr2 = Arrays.stream(arr).mapToLong(i -> (long) i).toArray();
		int idx = input.get2();
		RankSelectNaive naive = new RankSelectNaive(arr);
		RankSelectLookup lookup = new RankSelectLookup(arr);
		RankSelectSpaceEfficient spaceEfficient = new RankSelectSpaceEfficient(BitSet.valueOf(arr2), arr2.length, 2);
		assertThat(naive.rank(idx)).isEqualTo(lookup.rank(idx));
		assertThat(naive.rank(idx)).isEqualTo(spaceEfficient.rank(idx));
	}

	@Property
	void testSelectOperation_yieldsIdenticalResultsAcrossImplementations(@ForAll("zeroArrWithAtLeastOneOne") Tuple2<int[], Integer> input) {
		int[] arr = input.get1();
		int idx = input.get2();
		RankSelectNaive naive = new RankSelectNaive(arr);
		RankSelectLookup lookup = new RankSelectLookup(arr);
		assertThat(naive.select(idx)).isEqualTo(lookup.select(idx));
	}

	@Example
	void testPrecomputedRanks() {
		RankSelectLookup rankSelectLookup = new RankSelectLookup(new int[]{1, 1, 1, 1});
		assertThat(rankSelectLookup.getPrecomputedRanks()).containsSequence(1, 2, 3, 4);
	}

	@Example
	void testRankSelectThrows_whenGivenNullOrEmptyVector(@ForAll @WithNull(1) int[] obj) {
		assertThatIllegalArgumentException().isThrownBy(() -> new RankSelectNaive(obj));
		assertThatIllegalArgumentException().isThrownBy(() -> new RankSelectLookup(obj));
	}

	@Example
	void testRankSelectThrows_whenGivenEmptyVector() {
		assertThatIllegalArgumentException().isThrownBy(() -> new RankSelectNaive(new int[]{}));
		assertThatIllegalArgumentException().isThrownBy(() -> new RankSelectLookup(new int[]{}));
	}

	@Property
	void testRankExampleCases(@ForAll("rankSelectImplementationProvider") Class<? extends RankSelect> implementationClass, @ForAll("exampleCasesProvider") Tuple3<int[], Integer, Integer> tuple3) {
		RankSelect rankSelect;
		try {
			rankSelect = implementationClass.getConstructor(int[].class).newInstance(tuple3.get1());
		} catch (Exception e) {
			throw new AssertionError("Failed to instantiate RankSelect implementation", e);
		}

		assertThat(rankSelect.rank(tuple3.get2())).isEqualTo(tuple3.get3());
	}

	@Provide
	Arbitrary<Tuple3<int[], Integer, Integer>> exampleCasesProvider() {
		// Tuple<array, index, expected>
		return Arbitraries.of(Tuple.of(new int[]{0, 1}, 1, 1), Tuple.of(new int[]{0, 1, 1}, 1, 1), Tuple.of(new int[]{0, 1, 1}, 2, 2));
	}

	@Property
	void testRankIsPositive_whenVectorContainsOne(@ForAll("zeroArrWithAtLeastOneOne") Tuple2<int[], Integer> input) {
		int[] vector = input.get1();
		int idx = input.get2();
		RankSelectNaive naive = new RankSelectNaive(vector);
		assertThat(naive.rank(idx)).isPositive();
	}

	@Property
	void testRankIsSum_whenVectorContainsOnes(@ForAll("oneArr") Tuple2<int[], Integer> input) {
		int[] vector = input.get1();
		int idx = input.get2();
		RankSelectNaive naive = new RankSelectNaive(vector);
		assertThat(naive.rank(idx)).isEqualTo(vector.length);
	}

	@Property
	void testRankIsZero_whenVectorContainsZeroes(@ForAll("zeroArr") Tuple2<int[], Integer> input) {
		int[] vector = input.get1();
		int idx = input.get2();
		RankSelectNaive naive = new RankSelectNaive(vector);
		assertThat(naive.rank(idx)).isZero();
	}

	@Provide
	Arbitrary<Class<? extends RankSelect>> rankSelectImplementationProvider() {
		return Arbitraries.of(RankSelectLookup.class, RankSelectNaive.class);
	}

	@Provide
	Arbitrary<Tuple2<int[], Integer>> oneArr() {
		Arbitrary<int[]> validArr = Arbitraries.just(1).array(int[].class).ofMinSize(2);

		return validArr.flatMap(arr -> {
			int max = arr.length;
			return Arbitraries.integers().between(1, max).map(range -> Tuple.of(arr, range)).injectNull(0);
		});
	}

	@Provide
	Arbitrary<Tuple2<int[], Integer>> zeroArr() {
		Arbitrary<int[]> validArr = Arbitraries.just(0).array(int[].class).ofMinSize(2);

		return validArr.flatMap(arr -> {
			int max = arr.length;
			return Arbitraries.integers().between(1, max).map(range -> Tuple.of(arr, range)).injectNull(0);
		});
	}

	@Provide
	Arbitrary<Integer> range(int max) {
		return Arbitraries.integers().between(1, max);
	}

	@Provide
	Arbitrary<Tuple2<int[], Integer>> zeroArrWithAtLeastOneOne() {
		Arbitrary<int[]> validArr = Arbitraries.integers().greaterOrEqual(0).lessOrEqual(1).array(int[].class).ofMinSize(2).map(arr -> {
			if (arr.length > 0) {
				arr[0] = 1; // Ensure the first element is 1
			}
			return arr;
		});

		return validArr.flatMap(arr -> {
			int maxRange = arr.length == 0 ? 1 : arr.length - 1;
			return Arbitraries.integers().between(1, maxRange).injectNull(0).map(range -> Tuple.of(arr, range)).injectNull(0);
		});
	}

	@Provide
	Arbitrary<int[]> maxArrProvider() {
		return Arbitraries.integers().filter(i -> i == 1).array(int[].class).ofSize(100_000);
	}

}