package org.aaa;

public class RankSelectNaive implements RankSelect {
	private final int[] inputVector;

	public RankSelectNaive(int[] inputVector) {
		if (inputVector == null || inputVector.length == 0) {
			throw new IllegalArgumentException("Input vector is null or empty");
		}
		this.inputVector = inputVector;
	}

	@Override
	public int rank(int i) {
		if (i > inputVector.length) {
			throw new IllegalArgumentException("Index must be between 0 and " + inputVector.length);
		}
		int rank = 0;
		for (int k = 0; k <= i; k++) {
			if (inputVector[k] == 1) {
				rank++;
			}
		}
		return rank;
	}

	@Override
	public int select(int r) {
		if (r < 1 || r > inputVector.length) {
			throw new IllegalArgumentException("Index must be between 1 and " + inputVector.length);
		}
		for (int i : inputVector) {
			if (i == 1) {
				r--;
			}
			if (r == 0) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {

		RankSelectNaive naive = new RankSelectNaive(new int[]{1, 1});

		// Test rank queries
		System.out.println("Rank(1): " + naive.rank(1)); // Should output 3
	}
}
