package com.milestone2;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application with tuning, SMOTE, per-fold logging and box-plot.
 */
public class MainApp {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        log.info("=== Milestone2Runner started ===");

        try {
            // 0) Disable noisy logs from netlib-java
            LogManager.getLogManager().reset();
            java.util.logging.Logger.getLogger("com.github.fommil.netlib")
                    .setLevel(Level.SEVERE);
            java.util.logging.Logger.getLogger("com.github.fommil.jni")
                    .setLevel(Level.SEVERE);
            log.debug("Netlib-java loggers set to SEVERE");

            // 1) Creating output folders
            log.info("Creation of output folders: '{}' and '{}'.",
                    Config.OUTPUT_DIR, Config.CHARTS_DIR);
            Files.createDirectories(Paths.get(Config.OUTPUT_DIR));
            Files.createDirectories(Paths.get(Config.CHARTS_DIR));
            log.debug("Folders created correctly");

            // 1.1) Reading existing results to avoid duplicates
            Set<String> existingAgg = new HashSet<>();
            Path resPath = Paths.get(Config.RESULTS_CSV);
            if (Files.exists(resPath)) {
                try (BufferedReader br = Files.newBufferedReader(resPath, StandardCharsets.UTF_8)) {
                    // Skip header
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",", -1);
                        if (parts.length >= 2) {
                            String key = parts[0] + "," + parts[1];
                            existingAgg.add(key);
                        }
                    }
                }
            }
            Set<String> existingFold = new HashSet<>();
            Path foldPath = Paths.get(Config.FOLD_CSV);
            if (Files.exists(foldPath)) {
                try (BufferedReader br = Files.newBufferedReader(foldPath, StandardCharsets.UTF_8)) {
                    // Skip header
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",", -1);
                        if (parts.length >= 4) {
                            String key = parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3];
                            existingFold.add(key);
                        }
                    }
                }
            }

            GenericDataLoader loader = new GenericDataLoader();
            Preprocessor pre      = new Preprocessor();
            ChartGenerator chartGen = new ChartGenerator();

            // 2) Initialise CSV writer for aggregate and fold results
            try (ResultsWriter aggWriter = new ResultsWriter();
                 BufferedWriter foldWriter = Files.newBufferedWriter(
                         foldPath,
                         StandardCharsets.UTF_8,
                         StandardOpenOption.CREATE,
                         StandardOpenOption.APPEND)) {

                // Writes CSV header of folds if a new file
                boolean writeFoldHeader = Files.size(foldPath) == 0;
                if (writeFoldHeader) {
                    foldWriter.write("Dataset,Classifier,Run,Fold,Accuracy,Precision,Recall,F1,Kappa,AUC,NPofB20\n");
                    log.debug("Header fold_metrics.csv written");
                }

                // 3) Cycle on datasets in the data folder (CSV and ARFF)
                processDatasets(loader, pre, existingFold, foldWriter, existingAgg, aggWriter, chartGen);
            }

            log.info("=== Milestone2Runner successfully completed ===");
        } catch (Exception e) {
            log.error("Fatal mistake in MainApp:", e);
            System.exit(1);
        }
    }

    private static void processDatasets(GenericDataLoader loader, Preprocessor pre, Set<String> existingFold, BufferedWriter foldWriter, Set<String> existingAgg, ResultsWriter aggWriter, ChartGenerator chartGen) throws Exception {
        File dataDir = new File(Config.DATA_DIR);
        File[] dataFiles = dataDir.listFiles((d, name) -> name.endsWith(".csv") || name.endsWith(".arff"));
        if (dataFiles == null || dataFiles.length == 0) {
            log.warn("No CSV/ARFF file found in '{}'.", Config.DATA_DIR);
        } else {
            log.info("Found {} dataset in '{}'.", dataFiles.length, Config.DATA_DIR);
        }

        for (File file : Objects.requireNonNull(dataFiles)) {
            String ds = file.getName();
            log.info("--- Processing dataset '{}' ---", ds);

            // 3.1) Caricaturing and preprocessing
            long t0 = System.nanoTime();
            Instances raw = loader.load(ds);
            log.debug("[{}] Raw data loaded: {} instances × {} attributes",
                    ds, raw.numInstances(), raw.numAttributes());
            Instances ready = pre.applyFilters(raw);
            long prepMs = (System.nanoTime() - t0) / 1_000_000;
            log.info("[{}] Preprocessing completed in {} ms", ds, prepMs);

            // 3.2) For each classifier
            Map<String, Map<String, Double>> allMetrics = evaluateClassifiers(existingFold, foldWriter, existingAgg, aggWriter, ds, ready);

            // 3.3) Graph generation
            log.info("[{}] Generating bar charts and box plots", ds);
            chartGen.generate(ds, allMetrics);
            log.info("[{}] Graphs saved in '{}'.", ds, Config.CHARTS_DIR);

            // 3.4) Print the best classifier for each metric
            calculateAndLogBestMetrics(allMetrics, ds);
        }
    }

    private static void calculateAndLogBestMetrics(Map<String, Map<String, Double>> allMetrics, String ds) {
        if (!allMetrics.isEmpty()) {
            Set<String> metrics = allMetrics.values().iterator().next().keySet();
            for (String metric : metrics) {
                String bestClf = null;
                double bestVal = Double.NEGATIVE_INFINITY;
                for (Map.Entry<String, Map<String, Double>> entry : allMetrics.entrySet()) {
                    double val = entry.getValue().get(metric);
                    if (val > bestVal) {
                        bestVal = val;
                        bestClf = entry.getKey();
                    }
                }
                String bV = String.format(Locale.US, "%.4f", bestVal);
                log.info("[{}] Best classifier for {}: {} (value = {})",
                        ds, metric, bestClf, bV);
            }
        }
    }

    private static Map<String, Map<String, Double>> evaluateClassifiers(Set<String> existingFold, BufferedWriter foldWriter, Set<String> existingAgg, ResultsWriter aggWriter, String ds, Instances ready) throws Exception {
        Map<String, Map<String, Double>> allMetrics = new LinkedHashMap<>();
        for (ClassifierType ct : ClassifierType.values()) {
            log.info("[{}] ===== Classifier: {} =====", ds, ct);
            // 3.2.1) Tuning and build
            long tBuild = System.nanoTime();
            FilteredClassifier cls = TunedClassifierFactory.buildTuned(ct, ready);
            long buildMs = (System.nanoTime() - tBuild) / 1_000_000;
            log.info("[{}, {}] Classifier trained in {} ms", ds, ct, buildMs);

            // 3.2.2) Training + CV
            log.info("[{}, {}] Start of training + CV", ds, ct);
            long tCV = System.nanoTime();
            List<PerFoldResult> folds = ModelEvaluator.evaluateWithFolds(cls, ready, Config.N_FOLDS);
            long cvMs = (System.nanoTime() - tCV) / 1_000_000;
            log.info("[{}, {}] Training+CV completed in {} ms ({} fold obtained)",
                    ds, ct, cvMs, folds.size());

            // 3.2.3) Writing results per fold
            for (PerFoldResult r : folds) {
                Metrics m = r.getMetrics();
                String key = ds + "," + ct + "," + r.getRun() + "," + r.getFold();
                if (!existingFold.contains(key)) {
                    foldWriter.write(String.format(Locale.US,
                            "%s,%s,%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                            ds, ct, r.getRun(), r.getFold(),
                            m.getAccuracy(), m.getPrecision(), m.getRecall(),
                            m.getF1(), m.getKappa(), m.getAUC(), m.getNpOfb20()));
                    existingFold.add(key);
                }
                foldWriter.flush();
            }
            log.debug("[{}, {}] Per-fold results written to CSV", ds, ct);

            // 3.2.4) Aggregation and writing overall results
            Map<String, Double> avg = ModelEvaluator.aggregate(folds);
            allMetrics.put(ct.name(), avg);
            String aggKey = ds + "," + ct;
            if (!existingAgg.contains(aggKey)) {
                aggWriter.write(ds, ct.name(), avg);
                existingAgg.add(aggKey);
                log.info("[{}, {}] Aggregate results written: {}", ds, ct, avg);
            } else {
                log.info("[{}, {}] Aggregate results already present, skip writing", ds, ct);
            }
        }
        return allMetrics;
    }
}