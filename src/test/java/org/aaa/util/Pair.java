package org.aaa.util;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return left.equals(pair.left) && right.equals(pair.right);
	}

	@Override
	public int hashCode() {
		return 31 * left.hashCode() + right.hashCode();
	}

	@Override
	public int compareTo(Pair<L, R> o) {
		return this.left.compareTo(o.left);
	}
}