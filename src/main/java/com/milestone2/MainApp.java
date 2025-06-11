package com.milestone2;

import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.LogManager;
import java.util.logging.Level;

public class MainApp {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        // 0) Completely disable netlib-java loggers
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("com.github.fommil.netlib").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("com.github.fommil.jni").setLevel(Level.SEVERE);
        log.info("Starting processing...");

        // 1) Create folders securely
        Path outDir   = Paths.get(Config.OUTPUT_DIR);
        Path chartDir = Paths.get(Config.CHARTS_DIR);
        Files.createDirectories(outDir);
        Files.createDirectories(chartDir);
        log.info("Output directories created.");

        CsvDataLoader loader = new CsvDataLoader();
        Preprocessor  pre    = new Preprocessor();
        ModelEvaluator evaluator    = new ModelEvaluator();
        try (ResultsWriter writer = new ResultsWriter()) {
            log.info("Processing started.");
            ChartGenerator chartGen = new ChartGenerator();

            File folder = new File(Config.DATA_DIR);
            for (File csv : Objects.requireNonNull(folder.listFiles(f -> f.getName().endsWith(".csv")))) {
                log.info("Processing dataset: {}", csv.getName());
                String ds = csv.getName();
                Instances raw = loader.load(ds);
                Instances ready = pre.applyFilters(raw);

                Map<String, Map<String,Double>> allMetrics = new LinkedHashMap<>();
                log.info("Dataset {} loaded and preprocessed.", ds);

                for (ClassifierType ct : ClassifierType.values()) {
                    log.info("Training classifier: {}", ct.name());
                    Classifier cls = ct.build();
                    Map<String,Double> met = evaluator.evaluate(cls, ready, 10);
                    allMetrics.put(ct.name(), met);
                    writer.write(ds, ct.name(), met);
                    log.info("Metrics for {}: {}", ct.name(), met);
                }
                chartGen.generate(ds.replace(".csv",""), allMetrics);
                log.info("Charts generated for dataset: {}", ds);
            }
        }
        log.info("Processing completed.");
    }
}