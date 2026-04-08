package com.milestone2.prediction;

/**
 * Prediction outcome for one instance in a what-if scenario.
 */
public class PredictionRecord {
    private final boolean actualPositive;
    private final boolean predictedPositive;
    private final double positiveProbability;

    public PredictionRecord(boolean actualPositive, boolean predictedPositive, double positiveProbability) {
        this.actualPositive = actualPositive;
        this.predictedPositive = predictedPositive;
        this.positiveProbability = positiveProbability;
    }

    public boolean isActualPositive() {
        return actualPositive;
    }

    public boolean isPredictedPositive() {
        return predictedPositive;
    }

    public double getPositiveProbability() {
        return positiveProbability;
    }
}

