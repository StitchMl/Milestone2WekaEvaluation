package com.milestone2.fold;

import com.milestone2.metric.Metrics;

import java.util.Objects;

/**
 * Result of one fold inside one repeated cross-validation run.
 */
public class PerFoldResult {
    private final int run;
    private final int fold;
    private final String trainingWindowLabel;
    private final String testWindowLabel;
    private final int trainingInstances;
    private final int testInstances;
    private final Metrics metrics;

    public PerFoldResult(int run,
                         int fold,
                         Metrics metrics) {
        this(run, fold, null, null, -1, -1, metrics);
    }

    public PerFoldResult(int run,
                         int fold,
                         String trainingWindowLabel,
                         String testWindowLabel,
                         int trainingInstances,
                         int testInstances,
                         Metrics metrics) {
        this.run = run;
        this.fold = fold;
        this.trainingWindowLabel = trainingWindowLabel;
        this.testWindowLabel = testWindowLabel;
        this.trainingInstances = trainingInstances;
        this.testInstances = testInstances;
        this.metrics = metrics;
    }

    /**
     * Returns the repeated-run index associated with the split.
     *
     * @return run index
     */
    public int getRun() {
        return run;
    }

    /**
     * Returns the fold or walk-forward window index.
     *
     * @return fold index
     */
    public int getFold() {
        return fold;
    }

    /**
     * Returns the label of the training window, when available.
     *
     * @return training window label, or {@code null}
     */
    public String getTrainingWindowLabel() {
        return trainingWindowLabel;
    }

    /**
     * Returns the label of the test window, when available.
     *
     * @return test window label, or {@code null}
     */
    public String getTestWindowLabel() {
        return testWindowLabel;
    }

    /**
     * Returns the number of instances in the training split.
     *
     * @return training instance count, or {@code -1} when unavailable
     */
    public int getTrainingInstances() {
        return trainingInstances;
    }

    /**
     * Returns the number of instances in the test split.
     *
     * @return test instance count, or {@code -1} when unavailable
     */
    public int getTestInstances() {
        return testInstances;
    }

    /**
     * Returns the metric bundle computed for this split.
     *
     * @return split metrics
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Returns a debug-friendly textual representation of the split result.
     *
     * @return string representation of the fold result
     */
    @Override
    public String toString() {
        return "PerFoldResult{" +
                "run=" + run +
                ", fold=" + fold +
                ", trainingWindowLabel='" + trainingWindowLabel + '\'' +
                ", testWindowLabel='" + testWindowLabel + '\'' +
                ", trainingInstances=" + trainingInstances +
                ", testInstances=" + testInstances +
                ", metrics=" + metrics +
                '}';
    }

    /**
     * Compares this fold result with another object using every stored field.
     *
     * @param o object to compare with
     * @return {@code true} when both objects represent the same fold result
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PerFoldResult that = (PerFoldResult) o;
        return run == that.run &&
                fold == that.fold &&
                trainingInstances == that.trainingInstances &&
                testInstances == that.testInstances &&
                Objects.equals(trainingWindowLabel, that.trainingWindowLabel) &&
                Objects.equals(testWindowLabel, that.testWindowLabel) &&
                Objects.equals(metrics, that.metrics);
    }

    /**
     * Computes the hash code consistent with {@link #equals(Object)}.
     *
     * @return fold result hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(run, fold, trainingWindowLabel, testWindowLabel,
                trainingInstances, testInstances, metrics);
    }
}
