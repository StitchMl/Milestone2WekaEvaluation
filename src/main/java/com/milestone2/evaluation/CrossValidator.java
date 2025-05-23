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

    public CrossValidator(int folds, int repeats) {
        this.folds = folds;
        this.repeats = repeats;
    }

    public CVResult runRepeatedCV(Classifier baseCls, Instances data) throws Exception {
        logger.info("Execution {}×{}-fold CV for {}", repeats, folds, baseCls.getClass().getSimpleName());

        double sumP=0;
        double sumR=0;
        double sumA=0;
        double sumK=0;
        double sumN=0;
        for (int i = 0; i < repeats; i++) {
            Classifier cls = AbstractClassifier.makeCopy(baseCls);
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(cls, data, folds, new Random(i));
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