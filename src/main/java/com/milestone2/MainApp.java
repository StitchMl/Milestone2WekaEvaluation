package com.milestone2;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** MainApp con tuning, SMOTE, per-fold logging e box-plot */
public class MainApp {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        // Completely disable netlib-java loggers
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("com.github.fommil.netlib").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("com.github.fommil.jni").setLevel(Level.SEVERE);
        log.info("Starting processing...");

        // Create folders
        Files.createDirectories(Paths.get(Config.OUTPUT_DIR));
        Files.createDirectories(Paths.get(Config.CHARTS_DIR));

        CsvDataLoader loader = new CsvDataLoader();
        Preprocessor pre = new Preprocessor();

        // CSV for aggregate and per-fold results
        try (ResultsWriter aggWriter = new ResultsWriter();
             BufferedWriter foldWriter = Files.newBufferedWriter(
                     Paths.get(Config.OUTPUT_DIR, "fold_metrics.csv"))) {

            // Header CSV fold
            foldWriter.write("Dataset,Classifier,Run,Fold,Accuracy,Precision,Recall,F1,Kappa,AUC,NPofB20\n");

            ChartGenerator chartGen = new ChartGenerator();

            for (File csv : Objects.requireNonNull(
                    new File(Config.DATA_DIR).listFiles(f -> f.getName().endsWith(".csv")))) {
                String ds = csv.getName().replace(".csv", "");
                Instances raw = pre.applyFilters(loader.load(csv.getName()));

                Map<String, Map<String, Double>> allMetrics = new LinkedHashMap<>();
                for (ClassifierType ct : ClassifierType.values()) {
                    log.info("Building tuned classifier for {} on {}", ct, ds);
                    FilteredClassifier cls = TunedClassifierFactory.buildTuned(ct, raw);
                    log.info("Classifier built: {}", cls);

                    // Measurement time training+CV
                    long start = System.nanoTime();
                    log.info("Starting evaluation for {} on {}", ct, ds);
                    // evaluate also returns per-fold metrics in a list
                    List<PerFoldResult> folds = ModelEvaluator.evaluateWithFolds(
                            cls, raw, 10);
                    long durationMs = (System.nanoTime() - start) / 1_000_000;
                    log.info("Training+CV {} ms", durationMs);

                    // Write per-fold results
                    for (PerFoldResult r : folds) {
                        Metrics m = r.metrics;

                        foldWriter.write(String.format(
                                "%s,%s,%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                                ds,
                                ct,
                                r.run,
                                r.fold,
                                m.accuracy,
                                m.precision,
                                m.recall,
                                m.f1,
                                m.kappa,
                                m.auc,
                                m.npOfb20
                        ));
                    }

                    // Calculates average over all fold/runs
                    Map<String, Double> avg = ModelEvaluator.aggregate(folds);
                    allMetrics.put(ct.name(), avg);

                    // Write aggregate result
                    aggWriter.write(ds, ct.name(), avg);
                }
                // Bar graph + box-plot
                chartGen.generate(ds.replace(".csv",""), allMetrics);
                log.info("Charts generated for dataset: {}", ds);
            }
        }
        log.info("Processing completed.");
    }
}