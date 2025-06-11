package com.milestone2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.supervised.instance.SMOTE;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.BestFirst;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;

/**
 * Factory che usa CVParameterSelection al posto di GridSearch,
 * mantenendo SMOTE e feature selection.
 */
public class TunedClassifierFactory {
    private static final Logger log = LoggerFactory.getLogger(TunedClassifierFactory.class);

    private TunedClassifierFactory() {}

    public static FilteredClassifier buildTuned(ClassifierType type,
                                                Instances data) throws Exception {
        // 1) SMOTE
        SMOTE smote = new SMOTE();                      // balancing
        smote.setInputFormat(data);

        // 2) Base classifier + CVParameterSelection
        CVParameterSelection paramSelector = new CVParameterSelection();
        switch (type) {
            case RANDOM_FOREST:
                logStart(type, data.relationName().split("-")[0]);
                paramSelector.addCVParameter("I 100 500 5");     // trees
                paramSelector.addCVParameter("depth 0 20 5");    // depth
                paramSelector.setClassifier(new RandomForest());
                logFinish(type, data.relationName().split("-")[0]);
                break;
            case IBK:
                logStart(type, data.relationName().split("-")[0]);
                paramSelector.addCVParameter("K 1 15 2");        // k‐NN
                paramSelector.addCVParameter("W 0 2 1");         // distanceWeighting
                paramSelector.setClassifier(new IBk());
                logFinish(type, data.relationName().split("-")[0]);
                break;
            case NAIVE_BAYES:
                logStart(type, data.relationName().split("-")[0]);
                NaiveBayes nb = new NaiveBayes();
                nb.setUseKernelEstimator(true);
                nb.setUseSupervisedDiscretization(true);
                paramSelector.setClassifier(nb);
                logFinish(type, data.relationName().split("-")[0]);
                break;
            default:
                throw new IllegalArgumentException("Unknown classifier type");
        }
        log.info("Parameter search start");
        paramSelector.setNumFolds(5);  // internal folds for tuning
        log.info("Parameter search done");
        // Start the buildClassifier in a separate thread
        Thread tuningThread = new Thread(() -> {
            try {
                paramSelector.buildClassifier(data);  // parameter search
            } catch (Exception e) {
                throw new ParameterSelectionException("Error during parameter search", e);
            }
        }, "ParamSelector-Thread");

        tuningThread.start();

        tuningThread.join();  // ensures that it is finished
        log.info("\rTuning parameters done!      \n");
        log.info("Parameter selection done for {}", type);

        // 3) Feature selection wrapper
        log.info("Feature selection start");
        CfsSubsetEval eval = new CfsSubsetEval();
        BestFirst search = new BestFirst();
        AttributeSelectedClassifier asc = new AttributeSelectedClassifier();
        asc.setEvaluator(eval);
        asc.setSearch(search);
        asc.setClassifier(paramSelector);
        log.info("Feature selection done");

        // 4) Final pipeline
        log.info("Building final classifier with SMOTE and feature selection");
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(smote);
        fc.setClassifier(asc);
        log.info("Final classifier built");
        return fc;
    }

    private static void logStart(ClassifierType type, String dataset) {
        log.info("Building tuned classifier for {} on {}", type, dataset);
    }

    private static void logFinish(ClassifierType type, String dataset) {
        log.info("Finished building tuned classifier for {} on {}", type, dataset);
    }
}