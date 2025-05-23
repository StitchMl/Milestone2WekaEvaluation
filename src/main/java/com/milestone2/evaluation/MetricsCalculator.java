package com.milestone2.evaluation;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;

import java.util.*;
import java.util.stream.Collectors;

public class MetricsCalculator {
    private MetricsCalculator() {}

    public static double precision(Evaluation eval) {
        return eval.precision(1);
    }

    public static double recall(Evaluation eval) {
        return eval.recall(1);
    }

    public static double auc(Evaluation eval) {
        return eval.areaUnderROC(1);
    }

    public static double kappa(Evaluation eval) {
        return eval.kappa();
    }

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
}