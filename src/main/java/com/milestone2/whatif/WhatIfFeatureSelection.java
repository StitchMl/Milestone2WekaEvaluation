package com.milestone2.whatif;

import com.milestone2.feature.FeatureCorrelation;

/**
 * Selected feature for the what-if scenario generation.
 */
public class WhatIfFeatureSelection {
    private final FeatureCorrelation correlation;
    private final String reason;

    public WhatIfFeatureSelection(FeatureCorrelation correlation, String reason) {
        this.correlation = correlation;
        this.reason = reason;
    }

    @SuppressWarnings("unused")
    public FeatureCorrelation getCorrelation() {
        return correlation;
    }

    public String getFeatureName() {
        return correlation.getFeatureName();
    }

    public String getReason() {
        return reason;
    }
}

