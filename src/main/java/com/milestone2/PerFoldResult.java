package com.milestone2;

/**
 * Contains the results of a single run/fold.
 * The constructor now has only 4 parameters.
 */
public class PerFoldResult {
    public final String classifier;
    public final int run;
    public final int fold;
    public final Metrics metrics;

    public PerFoldResult(String classifier,
                         int run,
                         int fold,
                         Metrics metrics) {
        this.classifier = classifier;
        this.run        = run;
        this.fold       = fold;
        this.metrics    = metrics;
    }
}