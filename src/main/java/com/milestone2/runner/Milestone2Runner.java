package com.milestone2.runner;

import com.milestone2.config.ConfigLoader;
import com.milestone2.classifier.ClassifierProvider;
import com.milestone2.classifier.ClassifierRegistry;
import com.milestone2.pipeline.PreprocessingPipeline;
import com.milestone2.visualization.MetricsPlotter;
import com.milestone2.tuning.ClassifierTuner;
import com.milestone2.data.DataManager;
import com.milestone2.evaluation.CrossValidator;
import com.milestone2.evaluation.CVResult;
import com.milestone2.io.CsvResultWriter;
import com.milestone2.io.ResultWriter;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

public class Milestone2Runner {
    private static final Logger logger = LoggerFactory.getLogger(Milestone2Runner.class);

    public static void main(String[] args) {
        try {
            ConfigLoader config = new ConfigLoader();
            List<String> datasets = config.getDatasetPaths();
            List<ClassifierProvider> providers = ClassifierRegistry.loadProviders(config);
            CrossValidator cv = new CrossValidator(10, 10);

            // Output CSV path
            String csvFile = "results.csv";
            try (ResultWriter writer = new CsvResultWriter(csvFile)) {
                writer.writeHeader();

                for (String path : datasets) {
                    Instances data = DataManager.loadDataset(path);

                    for (ClassifierProvider prov : providers) {
                        // 1) Classifier Tuning
                        Classifier tuned;
                        String name = prov.name();
                        if (name.startsWith("IBk")) {
                            tuned = ClassifierTuner.tuneIBk(data);
                        } else if ("RandomForest".equals(name)) {
                            tuned = ClassifierTuner.tuneRandomForest(data);
                        } else {
                            tuned = prov.create();
                        }

                        // 2) Pre-processing
                        FilteredClassifier fc = PreprocessingPipeline.build(tuned, data);

                        // 3) Repeated cross-validation
                        CVResult res = cv.runRepeatedCV(fc, data);

                        // 4) Log and CSV
                        logger.info("Dataset={} Classifier={} → P={} R={} AUC={} K={} NPofB20={}",
                                path, name,
                                res.precision, res.recall, res.auc, res.kappa, res.npOfB20);
                        writer.writeResult(path, name, res);
                    }
                }
            }

            // 5) Metrics box-plot generation
            String pngFile = "metrics_boxplot.png";
            MetricsPlotter.createBoxPlot(Path.of("results.csv"), Path.of(pngFile));
            logger.info("Box-plot created: {}", pngFile);

        } catch (Exception e) {
            logger.error("Execution error", e);
        }
    }
}