package org.aaa;

public class RankSelectLookup implements RankSelect {
	private final int[] inputVector;
	private final int[] precomputedRanks;

	public RankSelectLookup(int[] inputVector) {
		if (inputVector == null || inputVector.length == 0) {
			throw new IllegalArgumentException("Input vector is null or empty");
		}
		this.inputVector = inputVector;
		this.precomputedRanks = new int[inputVector.length];
		RankSelectNaive naive = new RankSelectNaive(inputVector);
		for (int i = 0; i < inputVector.length; i++) {
			precomputedRanks[i] = naive.rank(i);
		}
	}

	@Override
	public int rank(int i) {
		if (i < 1 || i > inputVector.length) {
			throw new IllegalArgumentException("Index must be between 1 and " + inputVector.length);
		}
		return precomputedRanks[i];
	}

	@Override
	public int select(int r) {
		if (r < 1 || r > inputVector.length) {
			throw new IllegalArgumentException("Index must be between 1 and " + inputVector.length);
		}
		int low = 0;
		int high = inputVector.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			if (inputVector[mid] < r) {
				low = mid + 1;
			} else if (inputVector[mid] > r) {
				high = mid - 1;
			} else {
				return inputVector[mid];
			}
		}
		return -1;
	}

	public int[] getPrecomputedRanks() {
		return precomputedRanks;
	}
}