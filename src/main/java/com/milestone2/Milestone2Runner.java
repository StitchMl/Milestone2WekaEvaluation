package com.milestone2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;
import weka.classifiers.Classifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Collections;

import com.milestone2.CrossValidator.CVResult;

public class Milestone2Runner {
    private static final Logger logger = LoggerFactory.getLogger(Milestone2Runner.class);

    private static final String CONFIG_FILE = "datasets.txt";

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        logger.info("Working directory: {}", dir);

        try {
            String[] datasetPaths = loadDatasetPaths();
            if (datasetPaths.length == 0) {
                logger.error("No dataset found in {}", CONFIG_FILE);
                return;
            }
            for (String path : datasetPaths) {
                processDataset(path);
            }
        } catch (IOException e) {
            logger.error("Error reading '{}': {}", CONFIG_FILE, e.getMessage());
        }
    }

    private static void processDataset(String path) {
        try {
            Instances data = DataManager.loadDataset(path);
            for (Classifier cls : new Classifier[]{
                    ClassifierFactory.createRandomForest(),
                    ClassifierFactory.createNaiveBayes(),
                    ClassifierFactory.createIBk(5) }) {
                evaluateClassifier(cls, data, path);
            }
        } catch (Exception e) {
            logger.error("Error processing “{}”: {}", path, e.getMessage());
        }
    }

    private static void evaluateClassifier(Classifier cls, Instances data, String path) {
        try {
            String name = cls.getClass().getSimpleName();
            // We now receive a CVResult, no longer an Evaluation
            CVResult result = CrossValidator.runRepeatedCV(cls, data);
            logMetrics(path, name, result);
        } catch (Exception e) {
            logger.error("Error evaluating “{}” on “{}”: {}",
                    cls.getClass().getSimpleName(), path, e.getMessage());
        }
    }

    // Signature modified to accept CVResult
    private static void logMetrics(String path, String name, CVResult result) {
        logger.info("{}[{}] P={} R={} AUC={} K={} NPofB20={}",
                path, name,
                result.precision,
                result.recall,
                result.auc,
                result.kappa,
                result.npOfB20);
    }

    private static String[] loadDatasetPaths() throws IOException {
        Path configPath = Paths.get(CONFIG_FILE);
        if (!Files.exists(configPath)) {
            Files.write(configPath,
                    Collections.singletonList("# Enter paths to datasets here"));
            logger.info("Configuration file created: {}", configPath.toAbsolutePath());
        }
        List<String> lines = Files.readAllLines(configPath);
        return lines.stream()
                .map(String::trim)
                .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                .toArray(String[]::new);
    }
}