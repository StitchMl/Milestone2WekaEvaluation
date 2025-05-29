package com.milestone2.runner;

import com.milestone2.config.ConfigLoader;
import com.milestone2.classifier.ClassifierProvider;
import com.milestone2.classifier.ClassifierRegistry;
import com.milestone2.evaluation.MetricsCalculator;
import com.milestone2.pipeline.UnifiedPipeline;
import com.milestone2.visualization.MetricsPlotter;
import com.milestone2.tuning.ClassifierTuner;
import com.milestone2.data.DataManager;
import com.milestone2.evaluation.CrossValidator;
import com.milestone2.evaluation.CVResult;
import com.milestone2.io.CsvResultWriter;
import com.milestone2.io.ResultWriter;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

public class Milestone2Runner {
    private static final Logger logger = LoggerFactory.getLogger(Milestone2Runner.class);

    public static void main(String[] args) {
        // 1) Disabilita completamente i logger di netlib-java
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("com.github.fommil.netlib").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("com.github.fommil.jni").setLevel(Level.SEVERE);
        try {
            logger.info("Starting Milestone2Runner...");
            ConfigLoader config = new ConfigLoader();
            List<String> datasets = config.getDatasetPaths();
            List<ClassifierProvider> providers = ClassifierRegistry.loadProviders(config);
            CrossValidator cv = new CrossValidator(10, 10);
            boolean preserveOrder = config.usePreserveOrder();          // flag da ConfigLoader
            double splitRatio = config.getHoldoutRatio();

            // Output CSV path
            String csvFile = "results.csv";
            try (ResultWriter writer = new CsvResultWriter(csvFile)) {
                writer.writeHeader();

                for (String path : datasets) {
                    String datasetName = Path.of(path).getFileName().toString().replaceFirst("\\.csv$", "");
                    logger.info("Loading dataset: {}", datasetName);
                    Instances data = DataManager.loadDataset(path);

                    for (ClassifierProvider prov : providers) {
                        String name = prov.name();
                        logger.info("Classifier evaluation start: {}", name);
                        Classifier tuned;

                        // 1) TUNING
                        if (name.startsWith("IBk")) {
                            logger.info("Tuning IBk...");
                            tuned = ClassifierTuner.tuneIBk(data);
                        } else if ("RandomForest".equals(name)) {
                            logger.info("Tuning RandomForest...");
                            tuned = ClassifierTuner.tuneRandomForest(data);
                        } else {
                            tuned = prov.create();
                        }

                        // 2) Pipeline (pre-processing + selection + calibrazione)
                        logger.info("Pipeline construction for {}", name);
                        Classifier pipeline = UnifiedPipeline.build(tuned, data);

                        CVResult res;
                        if (preserveOrder) {
                            // === Holdout con ordine preservato ===
                            int trainSize = (int) Math.round(data.numInstances() * splitRatio);
                            int testSize = data.numInstances() - trainSize;

                            // Trains and tests preserve the original order
                            Instances train = new Instances(data, 0, trainSize);
                            Instances test = new Instances(data, trainSize, testSize);

                            // Train on the train
                            logger.info("Training on training sets (holdout)...");
                            pipeline.buildClassifier(train);

                            // Evaluation on the test set
                            logger.info("Evaluation on test set (holdout)...");
                            Evaluation eval = new Evaluation(train);
                            eval.evaluateModel(pipeline, test);
                            extracted(eval);


                            // Estrai metriche da Evaluation
                            res = new CVResult(
                                    MetricsCalculator.precision(eval),
                                    MetricsCalculator.recall(eval),
                                    MetricsCalculator.auc(eval),
                                    MetricsCalculator.kappa(eval),
                                    MetricsCalculator.npOfBX(eval, 20.0)
                            );
                        } else {
                            // === Cross-validation stratificata 10×10-fold ===
                            logger.info("Cross-validation execution layered 10x10...");
                            res = cv.runRepeatedCV(pipeline, data);
                        }

                        // 4) Log e CSV
                        logger.info("Dataset={} Classifier={} → P={} R={} AUC={} K={} NPofB20={}",
                                path, name,
                                res.precision, res.recall, res.auc, res.kappa, res.npOfB20);
                        writer.writeResult(path, name, res);
                    }

                    // 5) Metrics box-plot generation
                    logger.info("Box-plot generation of metrics...");
                    String pngFile = "metrics_boxplot_" + datasetName + ".png";
                    MetricsPlotter.createBoxPlot(Path.of("results.csv"), Path.of(pngFile));
                    logger.info("Box-plot created: {}", pngFile);
                }
            }
        } catch (Exception e) {
            logger.error("Execution error", e);
        }
    }

    /**
     * Extracts false positives and false negatives from the evaluation results
     * and writes them to a CSV file.
     *
     * @param eval Evaluation object containing predictions
     * @throws FileNotFoundException if the output file cannot be created
     */
    private static void extracted(Evaluation eval) throws FileNotFoundException {
        List<Prediction> predictionResults = eval.predictions();
        List<NominalPrediction> nominals = predictionResults.stream()
                .filter(NominalPrediction.class::isInstance)
                .map(p -> (NominalPrediction) p)
                .collect(Collectors.toList());

        int posIndex = 1;  // positive class index
        List<Integer> fpIndices = new ArrayList<>();
        List<Integer> fnIndices = new ArrayList<>();

        for (int i = 0; i < nominals.size(); i++) {
            NominalPrediction np = nominals.get(i);
            if (np.predicted() == posIndex && np.actual() != posIndex) {
                fpIndices.add(i);
            } else if (np.predicted() != posIndex && np.actual() == posIndex) {
                fnIndices.add(i);
            }
        }
        try(PrintWriter pw = new PrintWriter("errors.csv")) {
            pw.println("Type,InstanceIndex,Actual,Predicted,Distribution");
            for(int idx : fpIndices) {
                NominalPrediction np = nominals.get(idx);
                pw.printf("FP,%d,%.0f,%.0f,%s%n",
                        idx, np.actual(), np.predicted(),
                        Arrays.toString(np.distribution()));
            }
            for(int idx : fnIndices) {
                NominalPrediction np = nominals.get(idx);
                pw.printf("FN,%d,%.0f,%.0f,%s%n",
                        idx, np.actual(), np.predicted(),
                        Arrays.toString(np.distribution()));
            }
        }
    }
}