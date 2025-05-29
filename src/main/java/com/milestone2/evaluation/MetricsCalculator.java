package com.milestone2.evaluation;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;

import java.util.*;
import java.util.stream.Collectors;

public class MetricsCalculator {

    /**
     * Private constructor to avoid instantiation.
     * This class is a utility and should not be instantiated.
     */
    private MetricsCalculator() {}

    /**
     * Calculates the accuracy of the evaluation.
     *
     * @param eval Weka's Evaluation object
     * @return Accuracy as a double value
     */
    public static double precision(Evaluation eval) {
        return eval.precision(1);
    }

    /**
     * Calculates the F1 score of the evaluation.
     *
     * @param eval Weka's Evaluation object
     * @return F1 score as a double value
     */
    public static double f1Score(Evaluation eval) {
        return eval.fMeasure(1);
    }

    /**
     * Calculates the recall of the evaluation.
     *
     * @param eval Weka's Evaluation object
     * @return Recall as a double value
     */
    public static double recall(Evaluation eval) {
        return eval.recall(1);
    }

    /**
     * Calculates the Area Under the ROC Curve (AUC) for the evaluation.
     *
     * @param eval Weka's Evaluation object
     * @return AUC as a double value
     */
    public static double auc(Evaluation eval) {
        return eval.areaUnderROC(1);
    }

    /**
     * Calculates the Kappa statistic of the evaluation.
     *
     * @param eval Weka's Evaluation object
     * @return Kappa statistic as a double value
     */
    public static double kappa(Evaluation eval) {
        return eval.kappa();
    }

    /**
     * Calculates the Normalized Percentage of Defects at the top of the ranked predictions.
     * This is also known as PofB (Percentage of Defects at the Top).
     *
     * @param eval       Weka's Evaluation object
     * @param percentile the top-ranking percentage (e.g., 20.0 for Top-20%)
     * @return Normalized PofB(percentile)
     */
    public static double normalizedDefectsAtTop(Evaluation eval, double percentile) {
        List<NominalPrediction> predictionsList = eval.predictions().stream()
                .filter(NominalPrediction.class::isInstance)
                .map(p -> (NominalPrediction) p)
                .sorted(Comparator.comparing(np ->
                        -np.distribution()[1])).collect(Collectors.toList());

        // order by decreasing probability

        int cutoff = (int)Math.ceil(predictionsList.size() * percentile/100.0);
        long totalPos = predictionsList.stream().filter(np -> np.actual() == 1.0).count();
        long found = predictionsList.subList(0, cutoff).stream()
                .filter(np -> np.actual() == 1.0)
                .count();

        double rate = totalPos == 0 ? 0.0 : ((double)found / totalPos * 100.0);
        return rate / percentile;
    }

    /**
     * Aliases for compatibility: delegation to normalisedDefectsAtTop.
     *
     * @param eval       Weka's Evaluation object
     * @param percentile the top-ranking percentage (e.g., 20.0 for Top-20%)
     * @return Normalized PofB(percentile)
     */
    public static double npOfBX(Evaluation eval, double percentile) {
        return normalizedDefectsAtTop(eval, percentile);
    }
}