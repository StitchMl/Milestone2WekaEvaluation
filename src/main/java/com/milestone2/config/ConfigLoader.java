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

    public ConfigLoader() throws IOException {
        this.datasetPaths = loadDatasets();
        loadClassifierProperties();
    }

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

    public List<String> getDatasetPaths() {
        return Collections.unmodifiableList(datasetPaths);
    }

    public String getProperty(String key) {
        return classifierProps.getProperty(key);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(classifierProps.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}