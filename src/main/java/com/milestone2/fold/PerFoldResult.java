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

    public int getRun() {
        return run;
    }

    public int getFold() {
        return fold;
    }

    public String getTrainingWindowLabel() {
        return trainingWindowLabel;
    }

    public String getTestWindowLabel() {
        return testWindowLabel;
    }

    public int getTrainingInstances() {
        return trainingInstances;
    }

    public int getTestInstances() {
        return testInstances;
    }

    public Metrics getMetrics() {
        return metrics;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(run, fold, trainingWindowLabel, testWindowLabel,
                trainingInstances, testInstances, metrics);
    }
}

