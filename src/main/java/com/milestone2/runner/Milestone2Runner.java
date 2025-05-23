package com.milestone2.runner;

import com.milestone2.config.ConfigLoader;
import com.milestone2.classifier.ClassifierProvider;
import com.milestone2.classifier.ClassifierRegistry;
import com.milestone2.data.DataManager;
import com.milestone2.evaluation.CrossValidator;
import com.milestone2.evaluation.CVResult;
import com.milestone2.io.CsvResultWriter;
import com.milestone2.io.ResultWriter;
import weka.core.Instances;
import weka.classifiers.Classifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Milestone2Runner {
    private static final Logger logger = LoggerFactory.getLogger(Milestone2Runner.class);

    public static void main(String[] args) {
        try {
            ConfigLoader config = new ConfigLoader();
            List<String> datasets = config.getDatasetPaths();
            List<ClassifierProvider> providers = ClassifierRegistry.loadProviders(config);
            CrossValidator cv = new CrossValidator(10, 10);

            try (ResultWriter writer = new CsvResultWriter("results.csv")) {
                writer.writeHeader();
                for (String path : datasets) {
                    Instances data = DataManager.loadDataset(path);
                    for (ClassifierProvider prov : providers) {
                        Classifier cls = prov.create();
                        CVResult res = cv.runRepeatedCV(cls, data);
                        String name = prov.name();
                        logger.info("Dataset={} Classifier={} → P={} R={} AUC={} K={} NPofB20={}",
                                path, name,
                                res.precision, res.recall, res.auc, res.kappa, res.npOfB20);
                        writer.writeResult(path, prov.name(), res);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error in execution: ", e);
        }
    }
}