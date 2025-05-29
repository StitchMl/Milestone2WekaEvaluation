package com.milestone2.evaluation;

public class CVResult {
    public final double precision;
    public final double recall;
    public final double auc;
    public final double kappa;
    public final double npOfB20;

    /**
     * Constructs a CVResult with the given metrics.
     *
     * @param precision  Precision of the classifier
     * @param recall     Recall of the classifier
     * @param auc        Area Under Curve (AUC) of the ROC curve
     * @param kappa      Kappa statistic for inter-rater agreement
     * @param npOfB20    Normalized PofB20 (percentage of defects in the top 20%)
     */
    public CVResult(double precision, double recall, double auc, double kappa, double npOfB20) {
        this.precision = precision;
        this.recall    = recall;
        this.auc       = auc;
        this.kappa     = kappa;
        this.npOfB20   = npOfB20;
    }
}