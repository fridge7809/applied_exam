package org.aaa;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class DataGenerator {

	private static final Random RANDOM = new Random(1000);

	private static Integer[] generateIntegerArray(int n, Distribution distribution) {
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

	public static Comparable[] generateDataOfType(InputType type, Distribution distribution, StringContent content, int n) {
		return switch (type) {
			case INTS -> generateIntegerArray(n, distribution);
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
		DESCENDING
	}

	public enum StringContent {
		FIXED_LENGTH,
		VARIED_LENGTH,
		PREFIX
	}
}