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

    public double getProbability() {
        return probability;
    }

    public int getSize() {
        return size;
    }

    public boolean isPositive() {
        return positive;
    }
}

