package org.aaa.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class TestDataGenerator {

	public static void generateFile(String fileName, String content) {
		try {
			String resourcesPath = Path.of("src", "test", "resources").toAbsolutePath().toString();
			File resourcesDir = new File(resourcesPath);

			if (!resourcesDir.exists()) {
				boolean created = resourcesDir.mkdirs();
				if (!created) {
					throw new IOException("Failed to create resources directory: " + resourcesPath);
				}
			}

			File file = new File(resourcesDir, fileName);
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(content);
			}

			System.out.println("Test data file created at: " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String fileName = "testData.txt";
		String content = "This is a sample test data file.\nLine 1\nLine 2\nLine 3";

		generateFile(fileName, content);
	}
}