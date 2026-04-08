package com.milestone2.fold;

import com.milestone2.metric.Metrics;

import java.util.Objects;

/**
 * Result of one fold inside one repeated cross-validation run.
 */
public class PerFoldResult {
    private final int run;
    private final int fold;
    private final Metrics metrics;

    public PerFoldResult(int run,
                         int fold,
                         Metrics metrics) {
        this.run = run;
        this.fold = fold;
        this.metrics = metrics;
    }

    public int getRun() {
        return run;
    }

    public int getFold() {
        return fold;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return "PerFoldResult{" +
                "run=" + run +
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
                Objects.equals(metrics, that.metrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(run, fold, metrics);
    }
}


