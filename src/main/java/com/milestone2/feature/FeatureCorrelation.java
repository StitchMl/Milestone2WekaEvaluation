package com.milestone2.feature;

/**
 * Correlation between one numeric feature and the binary bug label.
 */
public class FeatureCorrelation {
    private final String featureName;
    private final double correlation;
    private final int nonMissingValueCount;
    private final int zeroValueCount;
    private final int positiveValueCount;

    public FeatureCorrelation(String featureName,
                              double correlation,
                              int nonMissingValueCount,
                              int zeroValueCount,
                              int positiveValueCount) {
        this.featureName = featureName;
        this.correlation = correlation;
        this.nonMissingValueCount = nonMissingValueCount;
        this.zeroValueCount = zeroValueCount;
        this.positiveValueCount = positiveValueCount;
    }

    public String getFeatureName() {
        return featureName;
    }

    public double getCorrelation() {
        return correlation;
    }

    public double getAbsoluteCorrelation() {
        return Math.abs(correlation);
    }

    public int getNonMissingValueCount() {
        return nonMissingValueCount;
    }

    public int getZeroValueCount() {
        return zeroValueCount;
    }

    public int getPositiveValueCount() {
        return positiveValueCount;
    }

    public boolean isZeroable() {
        return zeroValueCount > 0 && positiveValueCount > 0;
    }
}

