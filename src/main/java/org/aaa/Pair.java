package org.aaa;

// Class Pair for sorting left values of the pair, while keeping track of index (right value).
// This is used to test stability for all implementations
public class Pair<L extends Comparable, R extends Comparable> implements Comparable<Pair<L, R>> {
	private final L left;
	private final R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	@Override
	public String toString() {
		return "(" + left + ", " + right + ")";
	}

	// Override to only compare left values when checking equality
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return left.equals(pair.left);
	}

	@Override
	public int hashCode() {
		return 31 * left.hashCode() + right.hashCode();
	}

	// Override to only compare left values when sorting
	@Override
	public int compareTo(Pair<L, R> o) {
		return this.left.compareTo(o.left);
	}
}