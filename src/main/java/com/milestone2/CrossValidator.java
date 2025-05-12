package com.milestone2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.classifiers.AbstractClassifier;

import java.util.Random;

public class CrossValidator {
    private static final Logger logger = LoggerFactory.getLogger(CrossValidator.class);
    private static final int FOLDS   = 10;
    private static final int REPEATS = 10;

    public static CVResult runRepeatedCV(Classifier baseCls, Instances data) throws Exception {
        logger.info("Starting {}×{}-fold CV for {}", REPEATS, FOLDS, baseCls.getClass().getSimpleName());

        double sumP = 0, sumR = 0, sumAUC = 0, sumK = 0, sumNP = 0;
        long maxMb = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        logger.info("JVM max heap: {} MB", maxMb);

        for (int i = 0; i < REPEATS; i++) {
            logger.debug(" CV repetition {}/{}", i + 1, REPEATS);

            Classifier cls = AbstractClassifier.makeCopy(baseCls);  // clone
            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            try {
                eval.crossValidateModel(cls, data, FOLDS, new Random(i));  // 5-fold
            } catch (OutOfMemoryError oom) {
                long freeMb  = Runtime.getRuntime().freeMemory() / (1024 * 1024);
                long totalMb = Runtime.getRuntime().totalMemory() / (1024 * 1024);
                logger.error("OOM at rep {}/{} for {}: free/total/max = {}/{}/{} MB",
                        i+1, REPEATS, cls.getClass().getSimpleName(),
                        freeMb, totalMb, maxMb);
                throw oom;
            }

            sumP   += MetricsCalculator.precision(eval);
            sumR   += MetricsCalculator.recall(eval);
            sumAUC += MetricsCalculator.auc(eval);
            sumK   += MetricsCalculator.kappa(eval);
            sumNP  += MetricsCalculator.npOfBX(eval, 20.0);

            System.gc();

            long freeMb  = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            long totalMb = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            logger.trace("   After rep {}/{}: free/total = {}/{} MB", i+1, REPEATS, freeMb, totalMb);
        }

        CVResult result = new CVResult(
                sumP   / REPEATS,
                sumR   / REPEATS,
                sumAUC / REPEATS,
                sumK   / REPEATS,
                sumNP  / REPEATS
        );

        logger.info("Completed CV for {} → avg P={} R={} AUC={} K={} NPofB20={}",
                baseCls.getClass().getSimpleName(),
                result.precision, result.recall,
                result.auc, result.kappa, result.npOfB20);

        return result;
    }

    public static class CVResult {
        public final double precision, recall, auc, kappa, npOfB20;
        public CVResult(double p, double r, double a, double k, double np) {
            this.precision = p; this.recall = r;
            this.auc       = a; this.kappa  = k;
            this.npOfB20   = np;
        }
    }
}