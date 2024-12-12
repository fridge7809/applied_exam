//package org.aaa;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//
//
//public class DataGenerators {
//
//	// Helper class to emulate the Value class from Python
//	public static class Value implements Comparable<Value> {
//		public static int comparisons = 0;
//		private final double value;
//
//		public Value(double value) {
//			this.value = value;
//		}
//
//		@Override
//		public int compareTo(Value other) {
//			comparisons++;
//			return Double.compare(this.value, other.value);
//		}
//
//		@Override
//		public String toString() {
//			return Double.toString(value);
//		}
//
//		public static void resetCount() {
//			comparisons = 0;
//		}
//	}
//
//	public static <T extends Comparable<T>> T[] isolatedPairs(int n, int r) {
//		List<Integer> pivots = IntStream.range(1, n - 2 * r).boxed().collect(Collectors.toList());
//		Collections.shuffle(pivots);
//
//		List<Integer> selectedPivots = new ArrayList<>();
//		selectedPivots.add(0);
//		selectedPivots.addAll(pivots.subList(0, r - 1));
//		selectedPivots.add(n - 2 * r);
//		selectedPivots.sort(Integer::compareTo);
//
//		List<Integer> cuts = new ArrayList<>();
//		for (int i = 0; i < selectedPivots.size(); i++) {
//			cuts.add(selectedPivots.get(i) + 2 * i);
//		}
//
//		List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
//		List<List<Integer>> segments = new ArrayList<>();
//		for (int i = 0; i < cuts.size() - 1; i++) {
//			segments.add(new ArrayList<>(list.subList(cuts.get(i), cuts.get(i + 1))));
//		}
//
//		Collections.shuffle(segments);
//		for (int i = 1; i < r; i += 2) {
//			Collections.reverse(segments.get(i));
//		}
//
//		List<Integer> result = new ArrayList<>();
//		for (List<Integer> segment : segments) {
//			result.addAll(segment);
//		}
//		return result;
//	}
//
//	public static  <T extends Comparable<T>> T[] uniformData(int n) {
//		Random random = new Random();
//		return IntStream.range(0, n).mapToObj(i -> random.nextDouble() * n).collect(Collectors.toList());
//	}
//
//	public static  <T extends Comparable<T>> T[] superNesting(int n, int r) {
//		List<Integer> list = new ArrayList<>();
//		for (int i = n / 2 - 1; i >= 0; i--) {
//			list.add(i);
//		}
//		for (int i = 1; i <= n / 2; i++) {
//			list.add(-i);
//		}
//
//		int elemsPerRun = n / r;
//		for (int i = 0; i < r; i++) {
//			for (int j = elemsPerRun * i; j < elemsPerRun * (i + 1); j++) {
//				list.set(j, (int) Math.pow(-1, i) * list.get(j));
//			}
//		}
//		return list;
//	}
//
//	public static  <T extends Comparable<T>> T[] ultraNesting(int n) {
//		List<Integer> result = new ArrayList<>();
//		for (int i = 0; i < n; i++) {
//			result.add((int) Math.pow(-1, i) * (n / 2 - i));
//		}
//		return result;
//	}
//
//	public static <T extends Comparable<T>> T[] staircase(int n, int r) {
//		List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
//		int shortRun = 3;
//		int longRun = 2 * n / r - shortRun;
//
//		List<List<Integer>> segments = new ArrayList<>();
//		int index = 0;
//		int run = 0;
//
//		while (index < n) {
//			if (run % 2 == 0) {
//				int end = Math.min(index + shortRun, n);
//				segments.add(new ArrayList<>(list.subList(index, end)));
//				index += shortRun;
//			} else {
//				int end = Math.min(index + longRun, n);
//				List<Integer> reversed = new ArrayList<>(list.subList(index, end));
//				Collections.reverse(reversed);
//				segments.add(reversed);
//				index += longRun;
//			}
//			run++;
//		}
//
//		List<Integer> result = new ArrayList<>();
//		for (List<Integer> segment : segments) {
//			result.addAll(segment);
//		}
//		return result.toArray();
//	}
//
//	public static void main(String[] args) {
//		List<Integer> data = isolatedPairs(100, 5);
//		System.out.println("Isolated Pairs: " + data);
//
//		List<Double> uniformData = uniformData(100);
//		System.out.println("Uniform Data: " + uniformData);
//
//		List<Integer> staircaseData = staircase(100, 10);
//		System.out.println("Staircase: " + staircaseData);
//	}
//}