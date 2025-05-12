package com.milestone2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.Random;

/**
 * Performs 10×10-fold CV by cloning the classifier and using a new
 * Evaluation per run, so as not to accumulate gigs of predictions.
 */
public class CrossValidator {
    private static final Logger logger = LoggerFactory.getLogger(CrossValidator.class);
    private static final int FOLDS   = 10;
    private static final int REPEATS = 10;

    public static CVResult runRepeatedCV(Classifier baseCls, Instances data) throws Exception {
        logger.info("Starting {}×{}-fold CV for {}", REPEATS, FOLDS,
                baseCls.getClass().getSimpleName());

        double sumP=0, sumR=0, sumA=0, sumK=0, sumN=0;
        long maxMb = Runtime.getRuntime().maxMemory()/(1024*1024);
        logger.info("JVM max heap: {} MB", maxMb);

        for (int i = 0; i < REPEATS; i++) {
            logger.debug(" CV rep {}/{}", i+1, REPEATS);

            // 1) Clone the classifier so as not to fill the heap with previous trees
            Classifier cls = AbstractClassifier.makeCopy(baseCls);

            // 2) New Evaluation for this repetition
            Evaluation eval = new Evaluation(data);

            try {
                // 3) Cross-validateModel builds FOLDS models in-house
                eval.crossValidateModel(cls, data, FOLDS, new Random(i));
            } catch (OutOfMemoryError oom) {
                long freeMb  = Runtime.getRuntime().freeMemory()/(1024*1024);
                long totalMb = Runtime.getRuntime().totalMemory()/(1024*1024);
                logger.error("OOM at rep {}/{} for {}: free/total/max = {}/{}/{} MB",
                        i+1, REPEATS, cls.getClass().getSimpleName(),
                        freeMb, totalMb, maxMb);
                throw oom;
            }

            // 4) I extract metrics from this eval
            sumP += MetricsCalculator.precision(eval);
            sumR += MetricsCalculator.recall(eval);
            sumA += MetricsCalculator.auc(eval);
            sumK += MetricsCalculator.kappa(eval);
            sumN += MetricsCalculator.npOfBX(eval, 20.0);

            // 5) Free references and I invoke GC
            System.gc();

            long freeMb  = Runtime.getRuntime().freeMemory()/(1024*1024);
            long totalMb = Runtime.getRuntime().totalMemory()/(1024*1024);
            logger.trace("   After rep {}/{} free/total = {}/{} MB", i+1, REPEATS, freeMb, totalMb);
        }

        // Average of metrics over all 100 runs
        CVResult result = new CVResult(
                sumP/REPEATS, sumR/REPEATS,
                sumA/REPEATS, sumK/REPEATS,
                sumN/REPEATS
        );
        logger.info("Completed CV for {} → avg P={} R={} AUC={} K={} NPofB20={}",
                baseCls.getClass().getSimpleName(),
                result.precision, result.recall,
                result.auc, result.kappa, result.npOfB20);
        return result;
    }

    /** Container for average classifier metrics */
    public static class CVResult {
        public final double precision, recall, auc, kappa, npOfB20;
        public CVResult(double p,double r,double a,double k,double n){
            this.precision=p; this.recall=r;
            this.auc=a; this.kappa=k;
            this.npOfB20=n;
        }
    }
}