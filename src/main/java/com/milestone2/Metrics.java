package com.milestone2;

/**
 * Simple value object che contiene tutte le metriche di un fold.
 */
public class Metrics {
    public final double accuracy;
    public final double precision;
    public final double recall;
    public final double f1;
    public final double kappa;
    public final double auc;
    public final double npOfb20;

    public Metrics(double accuracy,
                   double precision,
                   double recall,
                   double f1,
                   double kappa,
                   double auc,
                   double npOfb20) {
        this.accuracy  = accuracy;
        this.precision = precision;
        this.recall    = recall;
        this.f1        = f1;
        this.kappa     = kappa;
        this.auc       = auc;
        this.npOfb20   = npOfb20;
    }
}