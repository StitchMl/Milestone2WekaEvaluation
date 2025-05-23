package com.milestone2.evaluation;

public class CVResult {
    public final double precision;
    public final double recall;
    public final double auc;
    public final double kappa;
    public final double npOfB20;

    public CVResult(double precision, double recall, double auc, double kappa, double npOfB20) {
        this.precision = precision;
        this.recall    = recall;
        this.auc       = auc;
        this.kappa     = kappa;
        this.npOfB20   = npOfB20;
    }
}