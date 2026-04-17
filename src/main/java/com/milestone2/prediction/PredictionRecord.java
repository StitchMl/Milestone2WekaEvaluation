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

    /**
     * Indicates whether the instance truly belongs to the positive class.
     *
     * @return {@code true} when the instance is actually positive
     */
    public boolean isActualPositive() {
        return actualPositive;
    }

    /**
     * Indicates whether the model predicted the positive class for the instance.
     *
     * @return {@code true} when the prediction is positive
     */
    public boolean isPredictedPositive() {
        return predictedPositive;
    }

    /**
     * Returns the predicted probability assigned to the positive class.
     *
     * @return positive-class probability
     */
    public double getPositiveProbability() {
        return positiveProbability;
    }
}

