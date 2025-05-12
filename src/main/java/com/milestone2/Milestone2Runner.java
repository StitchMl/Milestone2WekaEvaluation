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

import java.io.PrintWriter;

public class Milestone2Runner {
    private static final Logger logger = LoggerFactory.getLogger(Milestone2Runner.class);
    private static final String CONFIG_FILE = "datasets.txt";
    private static final String OUTPUT_CSV  = "results.csv";

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        logger.info("Working directory: {}", dir);

        // Open the CSV in try-with-resources: it will be closed automatically
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(Paths.get(OUTPUT_CSV)))) {
            // Header
            csv.println("Dataset,Classifier,Precision,Recall,AUC,Kappa,NPofB20");

            // Loading Paths
            String[] datasetPaths = loadDatasetPaths();
            if (datasetPaths.length == 0) {
                logger.error("No dataset found in {}", CONFIG_FILE);
                return;
            }

            // We process each dataset
            for (String path : datasetPaths) {
                processDataset(path, csv);
            }

            logger.info("All results written to {}", OUTPUT_CSV);
        } catch (IOException e) {
            logger.error("Error opening or writing to {}: {}", OUTPUT_CSV, e.getMessage());
        }
    }

    private static void processDataset(String path, PrintWriter csv) {
        try {
            Instances data = DataManager.loadDataset(path);
            for (Classifier cls : new Classifier[]{
                    ClassifierFactory.createRandomForest(),
                    ClassifierFactory.createNaiveBayes(),
                    ClassifierFactory.createIBk(5) }) {
                evaluateClassifier(cls, data, path, csv);
            }
        } catch (Exception e) {
            logger.error("Error processing “{}”: {}", path, e.getMessage());
        }
    }

    private static void evaluateClassifier(Classifier cls, Instances data, String path, PrintWriter csv) {
        try {
            String name = cls.getClass().getSimpleName();
            CVResult result = CrossValidator.runRepeatedCV(cls, data);
            logMetrics(path, name, result, csv);
        } catch (Exception e) {
            logger.error("Error evaluating “{}” on “{}”: {}",
                    cls.getClass().getSimpleName(), path, e.getMessage());
        }
    }

    // Signature modified to accept CVResult
    private static void logMetrics(String path, String name, CVResult result, PrintWriter csv) {
        // Log to console
        logger.info("{}[{}] P={} R={} AUC={} K={} NPofB20={}",
                path, name,
                result.precision,
                result.recall,
                result.auc,
                result.kappa,
                result.npOfB20);

        // Writes to CSV
        csv.printf("%s,%s,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                escape(path),
                name,
                result.precision,
                result.recall,
                result.auc,
                result.kappa,
                result.npOfB20);
        csv.flush();
    }

    /**
     * Load dataset paths from the CONFIG_FILE file.
     * If it does not exist, create it with an example comment.
     */
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

    /**
     * Minimal CSV-escaping: encloses in inverted commas if there are commas or superscripts
     */
    private static String escape(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}