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

    /**
     * Returns the numeric feature name that was analyzed.
     *
     * @return feature name
     */
    public String getFeatureName() {
        return featureName;
    }

    /**
     * Returns the signed correlation with the binary bug label.
     *
     * @return correlation value
     */
    public double getCorrelation() {
        return correlation;
    }

    /**
     * Returns the absolute correlation used for ranking.
     *
     * @return absolute correlation value
     */
    public double getAbsoluteCorrelation() {
        return Math.abs(correlation);
    }

    /**
     * Returns how many instances contributed to the correlation after missing values were skipped.
     *
     * @return non-missing value count
     */
    public int getNonMissingValueCount() {
        return nonMissingValueCount;
    }

    /**
     * Returns how many analyzed instances have value zero for the feature.
     *
     * @return zero-valued instance count
     */
    public int getZeroValueCount() {
        return zeroValueCount;
    }

    /**
     * Returns how many analyzed instances have a strictly positive value for the feature.
     *
     * @return positive-valued instance count
     */
    public int getPositiveValueCount() {
        return positiveValueCount;
    }

    /**
     * Indicates whether the feature can be used in the what-if workflow, which requires both zero and positive values.
     *
     * @return {@code true} when the feature is zeroable
     */
    public boolean isZeroable() {
        return zeroValueCount > 0 && positiveValueCount > 0;
    }
}

