package com.milestone2;

import java.util.Objects;

/**
 * Represents evaluation metrics for a single classifier on one fold or run.
 * Includes metrics such as accuracy, precision, recall, F1-score, Cohen's kappa, AUC, and NPofB20.
 */
public class Metrics {
    private final double accuracy;
    private final double precision;
    private final double recall;
    private final double f1;
    private final double kappa;
    private final double auc;
    private final double npOfb20;

    /**
     * Constructs a Metrics object with all evaluation measures.
     *
     * @param accuracy  the accuracy (proportion of correctly classified instances)
     * @param precision the precision (proportion of positive predictions that are correct)
     * @param recall    the recall (proportion of actual positives correctly identified)
     * @param f1        the F1-score (harmonic mean of precision and recall)
     * @param kappa     Cohen's kappa statistic (chance-corrected accuracy)
     * @param auc       area under the ROC curve (measure of discriminative ability)
     * @param npOfb20   value of NPofB20 metric (domain-specific metric)
     */
    public Metrics(double accuracy,
                   double precision,
                   double recall,
                   double f1,
                   double kappa,
                   double auc,
                   double npOfb20) {
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
        this.kappa = kappa;
        this.auc = auc;
        this.npOfb20 = npOfb20;
    }

    /** Returns the accuracy metric. */
    public double getAccuracy() {
        return accuracy;
    }

    /** Returns the precision metric. */
    public double getPrecision() {
        return precision;
    }

    /** Returns the recall metric. */
    public double getRecall() {
        return recall;
    }

    /** Returns the F1-score metric. */
    public double getF1() {
        return f1;
    }

    /** Returns Cohen's kappa statistic. */
    public double getKappa() {
        return kappa;
    }

    /** Returns the AUC (Area Under ROC Curve) metric. */
    public double getAUC() {
        return auc;
    }

    /** Returns the NPofB20 metric (domain-specific measure). */
    public double getNpOfb20() {
        return npOfb20;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "accuracy=" + accuracy +
                ", precision=" + precision +
                ", recall=" + recall +
                ", f1=" + f1 +
                ", kappa=" + kappa +
                ", auc=" + auc +
                ", npOfb20=" + npOfb20 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metrics metrics = (Metrics) o;
        return Double.compare(metrics.accuracy, accuracy) == 0 &&
                Double.compare(metrics.precision, precision) == 0 &&
                Double.compare(metrics.recall, recall) == 0 &&
                Double.compare(metrics.f1, f1) == 0 &&
                Double.compare(metrics.kappa, kappa) == 0 &&
                Double.compare(metrics.auc, auc) == 0 &&
                Double.compare(metrics.npOfb20, npOfb20) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accuracy, precision, recall, f1, kappa, auc, npOfb20);
    }
}