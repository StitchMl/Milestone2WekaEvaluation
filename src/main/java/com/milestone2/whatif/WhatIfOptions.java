package com.milestone2.whatif;

/**
 * Configuration switches for the exam-oriented what-if analysis.
 */
public class WhatIfOptions {
    private final boolean enabled;
    private final String featureName;
    private final String classifierId;

    public WhatIfOptions(boolean enabled, String featureName, String classifierId) {
        this.enabled = enabled;
        this.featureName = featureName;
        this.classifierId = classifierId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getFeatureName() {
        return featureName;
    }

    public String getClassifierId() {
        return classifierId;
    }
}

