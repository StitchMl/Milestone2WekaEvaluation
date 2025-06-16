package com.milestone2;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** MainApp con tuning, SMOTE, per-fold logging e box-plot */
public class MainApp {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        log.info("=== Milestone2Runner avviato ===");

        try {
            // 0) Disabling noisy logs from netlib-java
            LogManager.getLogManager().reset();
            java.util.logging.Logger.getLogger("com.github.fommil.netlib")
                    .setLevel(Level.SEVERE);
            java.util.logging.Logger.getLogger("com.github.fommil.jni")
                    .setLevel(Level.SEVERE);
            log.debug("Netlib-java loggers set to SEVERE");

            // 1) Output directory creation
            log.info("Creation of output folders: '{}' and '{}'.",
                    Config.OUTPUT_DIR, Config.CHARTS_DIR);
            Files.createDirectories(Paths.get(Config.OUTPUT_DIR));
            Files.createDirectories(Paths.get(Config.CHARTS_DIR));
            log.debug("Folders created correctly");

            CsvDataLoader loader = new CsvDataLoader();
            Preprocessor pre      = new Preprocessor();
            ChartGenerator chartGen = new ChartGenerator();

            // 2) Initialise CSV writer for aggregate and fold results
            try (ResultsWriter aggWriter = new ResultsWriter();
                 BufferedWriter foldWriter = Files.newBufferedWriter(
                         Paths.get(Config.OUTPUT_DIR, "fold_metrics.csv"))) {

                // header for fold_metrics.csv
                foldWriter.write("Dataset,Classifier,Run,Fold,Accuracy,Precision,Recall,F1,Kappa,AUC,NPofB20\n");
                log.debug("Header fold_metrics.csv written");

                // 3) Cycle on all CSV input
                File dataDir = new File(Config.DATA_DIR);
                File[] csvFiles = dataDir.listFiles((d, name) -> name.endsWith(".csv"));
                if (csvFiles == null || csvFiles.length == 0) {
                    log.warn("No CSV file found in '{}'.", Config.DATA_DIR);
                } else {
                    log.info("Found {} dataset in '{}'.", csvFiles.length, Config.DATA_DIR);
                }

                for (File csv : Objects.requireNonNull(csvFiles)) {
                    String ds = csv.getName().replace(".csv", "");
                    log.info("--- Processing dataset '{}' ---", ds);

                    // 3.1) Load and preprocessing
                    long t0 = System.nanoTime();
                    Instances raw = loader.load(csv.getName());
                    log.debug("[{}] Loaded raw data: {} instances × {} attributes",
                            ds, raw.numInstances(), raw.numAttributes());
                    Instances ready = pre.applyFilters(raw);
                    long prepMs = (System.nanoTime() - t0) / 1_000_000;
                    log.info("[{}] Preprocessing completed in {} ms", ds, prepMs);

                    // 3.2) For each classifier
                    Map<String, Map<String, Double>> allMetrics = new LinkedHashMap<>();
                    for (ClassifierType ct : ClassifierType.values()) {
                        log.info("[{}] ===== Classifier: {} =====", ds, ct);
                        // 3.2.1) Tuning and build
                        long tBuild = System.nanoTime();
                        FilteredClassifier cls = TunedClassifierFactory.buildTuned(ct, ready);
                        long buildMs = (System.nanoTime() - tBuild) / 1_000_000;
                        log.info("[{}, {}] Classifier built in {} ms", ds, ct, buildMs);

                        // 3.2.2) Training + CV
                        log.info("[{}, {}] Start training + CV", ds, ct);
                        long tCV = System.nanoTime();
                        List<PerFoldResult> folds = ModelEvaluator.evaluateWithFolds(cls, ready, 10);
                        long cvMs = (System.nanoTime() - tCV) / 1_000_000;
                        log.info("[{}, {}] Training+CV completed in {} ms ({} resulting fold)",
                                ds, ct, cvMs, folds.size());

                        // 3.2.3) Writing per-fold results
                        for (PerFoldResult r : folds) {
                            Metrics m = r.metrics;
                            foldWriter.write(String.format(Locale.US,
                                    "%s,%s,%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                                    ds, ct, r.run, r.fold,
                                    m.accuracy, m.precision, m.recall,
                                    m.f1, m.kappa, m.auc, m.npOfb20));
                        }
                        foldWriter.flush();
                        log.debug("[{}, {}] Per-fold results written to CSV", ds, ct);

                        // 3.2.4) Aggregation and writing overall results
                        Map<String, Double> avg = ModelEvaluator.aggregate(folds);
                        allMetrics.put(ct.name(), avg);
                        aggWriter.write(ds, ct.name(), avg);
                        log.info("[{}, {}] Aggregate results written: {}", ds, ct, avg);
                    }

                    // 3.3) Generazione grafici
                    log.info("[{}] Generation of bar+box graphics", ds);
                    chartGen.generate(ds, allMetrics);
                    log.info("[{}] Graphs saved in '{}'.", ds, Config.CHARTS_DIR);
                }
            }

            log.info("=== Milestone2Runner successfully completed ===");
        } catch (Exception e) {
            log.error("Fatal mistake in MainApp:", e);
            System.exit(1);
        }
    }
}