package com.milestone2.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String CLASSIFIERS_FILE = "classifiers.properties";
    private final Properties classifierProps = new Properties();
    private final List<String> datasetPaths;

    /**
     * Constructor that loads datasets and classifier properties.
     *
     * @throws IOException if there is an error reading the datasets or properties file.
     */
    public ConfigLoader() throws IOException {
        this.datasetPaths = loadDatasets();
        loadClassifierProperties();
    }

    /**
     * Loads dataset paths from the 'datasets' directory.
     * It looks for files with .arff or .csv extensions.
     *
     * @return a list of dataset file paths.
     * @throws IOException if there is an error reading the directory.
     */
    private List<String> loadDatasets() throws IOException {
        Path dir = Paths.get("src/main/resources/datasets");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            logger.info("Directory created: {}", dir.toAbsolutePath());
        }
        List<String> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{arff,csv}")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    paths.add(entry.toString());
                }
            }
        }
        if (paths.isEmpty()) {
            logger.warn("No .arff or .csv file found in the directory: {}", dir.toAbsolutePath());
        }
        return paths;
    }

    /**
     * Loads classifier properties from the 'classifiers.properties' file.
     * If the file does not exist, it creates an example file with default values.
     *
     * @throws IOException if there is an error, reading or writing the properties file.
     */
    private void loadClassifierProperties() throws IOException {
        Path path = Paths.get(CLASSIFIERS_FILE);
        try (InputStream in = Files.newInputStream(path)) {
            classifierProps.load(in);
        } catch (NoSuchFileException e) {
            // create an example
            Properties example = new Properties();
            example.setProperty("rf.numTrees", "50");
            example.setProperty("rf.maxDepth", "10");
            example.setProperty("rf.bagSizePercent", "50");
            example.setProperty("ibk.k", "5");
            try (OutputStream out = Files.newOutputStream(path)) {
                example.store(out, "Classifiers configuration");
            }
            logger.info("Classifiers.properties file created with example values");
            classifierProps.putAll(example);
        }
    }

    /**
     * Returns an unmodifiable list of dataset paths.
     *
     * @return a list of dataset file paths.
     */
    public List<String> getDatasetPaths() {
        return Collections.unmodifiableList(datasetPaths);
    }

    /**
     * Returns the value of a property by its key.
     *
     * @param key the property key.
     * @return the property value, or null if the key does not exist.
     */
    public String getProperty(String key) {
        return classifierProps.getProperty(key);
    }

    /**
     * Returns the value of a property as an integer.
     * If the property does not exist or cannot be parsed, it returns the default value.
     *
     * @param key          the property key.
     * @param defaultValue the default value to return if the property is not found or invalid.
     * @return the property value as an integer, or the default value.
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(classifierProps.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * If true, use split holdout preserving the order.
     * Reads the 'preserveOrder' property (default = false).
     */
    public boolean usePreserveOrder() {
        return Boolean.parseBoolean(classifierProps.getProperty("preserveOrder", "false"));
    }


    /**
     * Percentage of the dataset to be used as training in the holdout.
     * Reads the property 'holdoutRatio' (default = 0.8 = 80%).
     * It must return a value between 0.0 and 1.0.
     */
    public double getHoldoutRatio() {
        String s = classifierProps.getProperty("holdoutRatio", "0.8");
        try {
            double r = Double.parseDouble(s);
            if (r <= 0.0 || r >= 1.0) {
                throw new IllegalArgumentException(
                        "holdoutRatio must be between 0 and 1 (excluded): " + r);
            }
            return r;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "holdoutRatio is not a valid number: " + s, e);
        }
    }
}