package org.aaa;

import java.util.*;

public class DataGenerator {

	private static final Random RANDOM = new Random(1000);

	// Generates data for benchmarking
	private static Integer[] generateIntegerArray(int n, Distribution distribution, int r) {
		Integer[] array = new Integer[n];
		switch (distribution) {
			case UNIFORM -> {
				for (int i = 0; i < n; i++) {
					array[i] = RANDOM.nextInt();
				}
			}
			case ASCENDING -> {
				for (int i = 0; i < n; i++) {
					array[i] = i;
				}
			}
			case DESCENDING -> {
				for (int i = 0; i < n; i++) {
					array[i] = i;
				}
				Arrays.sort(array, Collections.reverseOrder());
			}
			case PRESORTED -> {
				// ""
				List<Integer> list = new ArrayList<>();
				int elementsPerRun = n / r;

				Random rand = new Random();
				for (int i = 0; i < n; i++) {
					int length = rand.nextInt(elementsPerRun * 2);
					if (list.size() + length > n) {
						length = Math.abs(list.size() - n);
					}
					for (int j = 0; j < length / 2; j++) {
						list.add(j);
					}
					for (int j = length / 2; j > 0 / 2; j--) {
						list.add(j);
					}
				}

				return list.toArray(new Integer[0]);
			}
		}
		return array;
	}

	private static String generateRandomString(int length) {
		int leftLimit = 97; // 'a'
		int rightLimit = 122; // 'z'
		return RANDOM.ints(leftLimit, rightLimit + 1)
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	private static String[] generateStringArray(StringContent content, int n) {
		String[] array = new String[n];
		for (int i = 0; i < n; i++) {
			String baseString = generateRandomString(50);
			switch (content) {
				case FIXED_LENGTH -> array[i] = baseString;
				case VARIED_LENGTH ->
						array[i] = baseString.substring(0, RANDOM.nextInt(baseString.length())) + "a";
				case PREFIX -> array[i] = "a".repeat(1000) + baseString;
			}
		}
		return array;
	}

	public static Comparable[] generateDataOfType(InputType type, Distribution distribution, StringContent content, int n, int runs) {
		return switch (type) {
			case INTS -> generateIntegerArray(n, distribution, runs);
			case STRINGS -> generateStringArray(content, n);
		};
	}

	public enum InputType {
		INTS,
		STRINGS,
	}

	public enum Distribution {
		UNIFORM,
		ASCENDING,
		DESCENDING,
		PRESORTED
	}

	public enum StringContent {
		FIXED_LENGTH,
		VARIED_LENGTH,
		PREFIX
	}

	public static void main(String[] args) {
		int N = 1000; // Total number of elements
		int R = 3;  // Number of runs

		Integer[] array = generateIntegerArray(N, Distribution.PRESORTED, R);

		// Print the resulting array
		System.out.println(Arrays.toString(array));
	}
}