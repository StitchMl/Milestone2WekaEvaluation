package com.milestone2;

import java.util.Objects;

/**
 * Represents the result of a single fold of classification, including the classifier used,
 * the run and fold indices, and the evaluation metrics for that fold.
 */
public class PerFoldResult {
    private final String classifierName;
    private final int run;
    private final int fold;
    private final Metrics metrics;

    /**
     * Constructs a PerFoldResult with a given classifier, run, fold, and metrics.
     *
     * @param classifierName the name of the classifier
     * @param run            the run (repetition) number of the experiment
     * @param fold           the fold index of cross-validation
     * @param metrics        the evaluation metrics for this fold
     */
    public PerFoldResult(String classifierName,
                         int run,
                         int fold,
                         Metrics metrics) {
        this.classifierName = classifierName;
        this.run = run;
        this.fold = fold;
        this.metrics = metrics;
    }

    /** Returns the classifier name. */
    public String getClassifierName() {
        return classifierName;
    }

    /** Returns the run (repetition) number. */
    public int getRun() {
        return run;
    }

    /** Returns the fold index. */
    public int getFold() {
        return fold;
    }

    /** Returns the metrics for this fold. */
    public Metrics getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return "PerFoldResult{" +
                "classifierName='" + classifierName + '\'' +
                ", run=" + run +
                ", fold=" + fold +
                ", metrics=" + metrics +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PerFoldResult that = (PerFoldResult) o;
        return run == that.run &&
                fold == that.fold &&
                Objects.equals(classifierName, that.classifierName) &&
                Objects.equals(metrics, that.metrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classifierName, run, fold, metrics);
    }
}