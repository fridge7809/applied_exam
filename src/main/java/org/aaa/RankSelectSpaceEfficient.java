package org.aaa;

import java.util.BitSet;

public class RankSelectSpaceEfficient {
	private final BitSet bitSet;
	private final int n;
	private final int b; // Block size
	private final int s; // Superblock size
	private final int[] Rs; // Rank of superblocks
	private final int[] Rb; // Rank offsets of blocks

	public RankSelectSpaceEfficient(BitSet bitSet, int n) {
		this.bitSet = bitSet;
		this.n = n;
		this.b = (int) (Math.log(n) / Math.log(2)) / 2;
		this.s = b * (int) (Math.log(n) / Math.log(2));
		this.Rs = new int[(n + s - 1) / s]; // Number of superblocks
		this.Rb = new int[(n + b - 1) / b]; // Number of blocks

		precomputeRanks();
	}

	private void precomputeRanks() {
		int totalRank = 0;
		for (int i = 0; i < Rs.length; i++) {
			Rs[i] = totalRank;
			int superblockEnd = Math.min(n, (i + 1) * s);

			for (int j = i * s; j < superblockEnd; j += b) {
				Rb[j / b] = totalRank - Rs[i];
				for (int k = j; k < Math.min(superblockEnd, j + b); k++) {
					if (bitSet.get(k)) {
						totalRank++;
					}
				}
			}
		}
	}

	public int rank(int position) {
		if (position < 0 || position >= n) {
			throw new IllegalArgumentException("Position out of bounds.");
		}

		int superblockIndex = position / s;
		int blockIndex = position / b;

		int rank = Rs[superblockIndex] + Rb[blockIndex];
		int start = blockIndex * b;

		// Count 1s within the block up to the position (inclusive)
		for (int i = start; i <= position; i++) {
			if (bitSet.get(i)) {
				rank++;
			}
		}
		return rank;
	}

	public int select(int rank) {
		if (rank < 1 || rank > rank(n - 1)) {
			throw new IllegalArgumentException("Rank out of bounds.");
		}

		// Binary search on superblocks
		int low = 0, high = Rs.length - 1;
		while (low < high) {
			int mid = (low + high) / 2;
			if (Rs[mid] < rank) {
				low = mid + 1;
			} else {
				high = mid;
			}
		}
		int superblockIndex = low;

		// Binary search on blocks within the superblock
		low = superblockIndex * s / b;
		high = Math.min((superblockIndex + 1) * s / b, Rb.length) - 1;
		while (low < high) {
			int mid = (low + high) / 2;
			if (Rs[superblockIndex] + Rb[mid] < rank) {
				low = mid + 1;
			} else {
				high = mid;
			}
		}
		int blockIndex = low;

		// Linear search within the block
		int currentRank = Rs[superblockIndex] + Rb[blockIndex];
		int start = blockIndex * b;

		for (int i = start; i < n; i++) {
			if (bitSet.get(i)) {
				currentRank++;
			}
			if (currentRank == rank) {
				return i;
			}
		}

		throw new IllegalStateException("Rank not found.");
	}

	public static void main(String[] args) {
		int n = 128; // Size of the bit array
		BitSet bitSet = new BitSet(n);

		// Set some bits
		bitSet.set(3);
		bitSet.set(7);
		bitSet.set(10);
		bitSet.set(50);
		bitSet.set(100);

		RankSelectSpaceEfficient rankSelect = new RankSelectSpaceEfficient(bitSet, n);

		// Test rank queries
		System.out.println("Rank(3): " + rankSelect.rank(3));  // Should output 1
		System.out.println("Rank(10): " + rankSelect.rank(10)); // Should output 3
		System.out.println("Rank(50): " + rankSelect.rank(50)); // Should output 4

		// Test select queries
		System.out.println("Select(1): " + rankSelect.select(1)); // Should output 3
		System.out.println("Select(3): " + rankSelect.select(3)); // Should output 10
		System.out.println("Select(4): " + rankSelect.select(4)); // Should output 50
	}
}
