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

    /**
     * Returns how many B+/B prediction pairs were compared.
     *
     * @return paired instance count
     */
    public int getPairedInstanceCount() {
        return pairedInstanceCount;
    }

    /**
     * Returns how many paired instances are actually buggy.
     *
     * @return actual buggy count
     */
    public int getActualBuggyCount() {
        return actualBuggyCount;
    }

    /**
     * Returns how many instances switched from predicted buggy in B+ to predicted clean in B.
     *
     * @return predicted relieved count
     */
    public int getPredictedRelievedCount() {
        return predictedRelievedCount;
    }

    /**
     * Returns how many actually buggy instances are among the relieved predictions.
     *
     * @return avoidable buggy count
     */
    public int getAvoidableBuggyCount() {
        return avoidableBuggyCount;
    }

    /**
     * Returns the share of actual buggy instances that become avoidable in the paired comparison.
     *
     * @return avoidable buggy share
     */
    public double getAvoidableBuggyShare() {
        return avoidableBuggyShare;
    }

    /**
     * Returns the mean reduction in positive-class probability between B+ and B.
     *
     * @return average probability reduction
     */
    public double getAveragePositiveProbabilityReduction() {
        return averagePositiveProbabilityReduction;
    }
}

