package com.milestone2.whatif;

/**
 * Paired comparison between B+ and B predictions.
 */
public class WhatIfImpactSummary {
    private final int pairedInstanceCount;
    private final int actualBuggyCount;
    private final int predictedRelievedCount;
    private final int avoidableBuggyCount;
    private final double avoidableBuggyShare;
    private final double averagePositiveProbabilityReduction;

    public WhatIfImpactSummary(int pairedInstanceCount,
                               int actualBuggyCount,
                               int predictedRelievedCount,
                               int avoidableBuggyCount,
                               double avoidableBuggyShare,
                               double averagePositiveProbabilityReduction) {
        this.pairedInstanceCount = pairedInstanceCount;
        this.actualBuggyCount = actualBuggyCount;
        this.predictedRelievedCount = predictedRelievedCount;
        this.avoidableBuggyCount = avoidableBuggyCount;
        this.avoidableBuggyShare = avoidableBuggyShare;
        this.averagePositiveProbabilityReduction = averagePositiveProbabilityReduction;
    }

    public int getPairedInstanceCount() {
        return pairedInstanceCount;
    }

    public int getActualBuggyCount() {
        return actualBuggyCount;
    }

    public int getPredictedRelievedCount() {
        return predictedRelievedCount;
    }

    public int getAvoidableBuggyCount() {
        return avoidableBuggyCount;
    }

    public double getAvoidableBuggyShare() {
        return avoidableBuggyShare;
    }

    public double getAveragePositiveProbabilityReduction() {
        return averagePositiveProbabilityReduction;
    }
}

