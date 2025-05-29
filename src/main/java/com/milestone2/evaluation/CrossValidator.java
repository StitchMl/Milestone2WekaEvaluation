package com.milestone2.evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.Random;

public class CrossValidator {
    private static final Logger logger = LoggerFactory.getLogger(CrossValidator.class);
    private final int folds;
    private final int repeats;

    /**
     * Constructs a CrossValidator with a specified number of folds and repeats.
     *
     * @param folds   Number of folds for cross-validation
     * @param repeats Number of times to repeat the cross-validation
     */
    public CrossValidator(int folds, int repeats) {
        this.folds = folds;
        this.repeats = repeats;
    }

    /**
     * Runs repeated k-fold cross-validation on the given classifier and dataset.
     *
     * @param baseCls Classifier to evaluate
     * @param data    Dataset to use for evaluation
     * @return CVResult containing average metrics across all folds and repeats
     * @throws Exception if an error occurs during evaluation
     */
    public CVResult runRepeatedCV(Classifier baseCls, Instances data) throws Exception {
        logger.info("Execution {}×{}-fold CV for {}", repeats, folds, baseCls.getClass().getSimpleName());

        double sumP=0;
        double sumR=0;
        double sumA=0;
        double sumK=0;
        double sumN=0;
        for (int i = 0; i < repeats; i++) {
            data.setClassIndex(data.numAttributes() - 1);
            Classifier cls = AbstractClassifier.makeCopy(baseCls);
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(cls, data, folds, new Random(i)); // Rimosso parametri non necessari
            sumP += MetricsCalculator.precision(eval);
            sumR += MetricsCalculator.recall(eval);
            sumA += MetricsCalculator.auc(eval);
            sumK += MetricsCalculator.kappa(eval);
            sumN += MetricsCalculator.normalizedDefectsAtTop(eval, 20.0);
        }

        return new CVResult(
                sumP/repeats,
                sumR/repeats,
                sumA/repeats,
                sumK/repeats,
                sumN/repeats
        );
    }
}