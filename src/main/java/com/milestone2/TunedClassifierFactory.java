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
        String dataset = data.relationName().split("-")[0];
        log.info("=== Building tuned classifier: {} on dataset '{}' ===", type, dataset);

        // 1) SMOTE
        log.debug("[{}] Initializing SMOTE filter", type);
        SMOTE smote = new SMOTE();
        smote.setInputFormat(data);

        // 2) CVParameterSelection
        log.debug("[{}] Configuring CVParameterSelection", type);
        CVParameterSelection paramSelector = new CVParameterSelection();
        switch (type) {
            case RANDOM_FOREST:
                log.info("[{}] Tuning RandomForest parameters", type);
                paramSelector.addCVParameter("I 100 500 5");   // numTrees
                paramSelector.addCVParameter("depth 0 20 5");  // maxDepth
                RandomForest rf = (RandomForest) type.createClassifier();
                int cores = Runtime.getRuntime().availableProcessors();
                log.debug("[{}] Setting RF numExecutionSlots = {}", type, cores);
                rf.setNumExecutionSlots(cores);
                paramSelector.setClassifier(rf);
                break;

            case K_NEAREST_NEIGHBORS:
                log.info("[{}] Tuning IBk parameters", type);
                paramSelector.addCVParameter("K 1 15 2");    // k
                paramSelector.addCVParameter("W 0 2 1");     // distanceWeighting
                paramSelector.setClassifier(type.createClassifier());
                break;

            case NAIVE_BAYES:
                log.info("[{}] Configuring NaiveBayes with KDE", type);
                NaiveBayes nb = (NaiveBayes) type.createClassifier();
                nb.setUseKernelEstimator(true);
                // no supervised discretization when KDE enabled
                paramSelector.setClassifier(nb);
                break;

            default:
                log.error("[{}] Unknown classifier type", type);
                throw new IllegalArgumentException("Unknown classifier type: " + type);
        }

        log.debug("[{}] Setting internal CV folds = {}", type, Config.N_FOLDS);
        paramSelector.setNumFolds(Config.N_FOLDS);

        log.info("[{}] Starting parameter search", type);
        long startSearch = System.currentTimeMillis();
        paramSelector.buildClassifier(data);
        long durationSearch = System.currentTimeMillis() - startSearch;
        log.info("[{}] Parameter search completed in {} ms", type, durationSearch);

        // 3) Feature selection
        log.info("[{}] Starting feature selection (CfsSubsetEval + BestFirst)", type);
        CfsSubsetEval eval = new CfsSubsetEval();
        BestFirst search = new BestFirst();
        AttributeSelectedClassifier asc = new AttributeSelectedClassifier();
        asc.setEvaluator(eval);
        asc.setSearch(search);
        asc.setClassifier(paramSelector);
        log.info("[{}] Feature selection wrapper configured", type);

        // 4) Final pipeline
        log.info("[{}] Building final FilteredClassifier pipeline", type);
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(smote);
        fc.setClassifier(asc);
        log.info("[{}] Final classifier built successfully", type);

        return fc;
    }
}