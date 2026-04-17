package com.milestone2.prediction;

/**
 * One prediction annotated with inspection cost and whether it is a true positive.
 */
public class RankedPrediction {
    private final double probability;
    private final int size;
    private final boolean positive;

    public RankedPrediction(double probability, int size, boolean positive) {
        this.probability = probability;
        this.size = size;
        this.positive = positive;
    }

    /**
     * Returns the ranking score, typically the predicted probability of being buggy.
     *
     * @return ranking probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Returns the inspection cost associated with the prediction.
     *
     * @return entity size or inspection cost
     */
    public int getSize() {
        return size;
    }

    /**
     * Indicates whether the ranked entity is actually positive.
     *
     * @return {@code true} when the entity is positive
     */
    public boolean isPositive() {
        return positive;
    }
}

