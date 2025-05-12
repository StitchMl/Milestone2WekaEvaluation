package com.milestone2;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import weka.classifiers.evaluation.Prediction;

public class MetricsCalculator {

    /**
     * Calculate the percentage of true positives (TP) in the predictions.
     *
     * @param eval The evaluation object containing predictions.
     * @return The percentage of true positives.
     */
    public static List<NominalPrediction> getNominalPredictions(Evaluation eval) {
        List<NominalPrediction> nominals = new ArrayList<>();
        for (Prediction p : eval.predictions()) {
            if (p instanceof NominalPrediction) {
                nominals.add((NominalPrediction) p);
            }
        }
        return nominals;
    }

    /** Precision per positive class (index=1) */
    public static double precision(Evaluation eval) {
        return eval.precision(1);
    }

    /** Recall for positive class (index=1) */
    public static double recall(Evaluation eval) {
        return eval.recall(1);
    }

    /** AUC (area sotto la curva ROC) per classe positiva */
    public static double auc(Evaluation eval) {
        return eval.areaUnderROC(1);
    }

    /** Kappa statistic */
    public static double kappa(Evaluation eval) {
        return eval.kappa();
    }

    /**
     * NPofBX: percentage of defects found in the first X% of ordered instances
     * by descending probability, normalized on X.
     */
    public static double npOfBX(Evaluation eval, double X) {
        List<NominalPrediction> predictionsList = getNominalPredictions(eval);
        List<Double[]> list = new ArrayList<>();
        for (NominalPrediction np : predictionsList) {
            double probPos = np.distribution()[1];
            list.add(new Double[]{probPos, np.actual()});
        }
        // sort by decreasing probPos
        list.sort(Comparator.comparing((Double[] a) -> a[0]).reversed());
        int cutoff = (int) Math.ceil(list.size() * X / 100.0);
        int found = 0, totalPos = 0;
        for (Double[] p : list) {
            if (p[1] == 1.0) totalPos++;
        }
        for (int i = 0; i < cutoff; i++) {
            if (list.get(i)[1] == 1.0) found++;
        }
        double pofBX = (double) found / totalPos * 100.0;
        return pofBX / X;
    }
}
