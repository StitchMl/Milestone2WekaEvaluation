package com.milestone2.classifier;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads classifier catalog properties from disk.
 */
public class ClassifierPropertiesLoader {
    /**
     * Reads the classifier catalog properties file using UTF-8.
     *
     * @param propertiesPath catalog file path
     * @return loaded properties
     * @throws IOException when the file cannot be opened or parsed
     */
    public Properties load(Path propertiesPath) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(propertiesPath, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }
}

