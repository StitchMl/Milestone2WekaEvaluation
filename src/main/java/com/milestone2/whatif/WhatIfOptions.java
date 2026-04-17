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

    /**
     * Indicates whether the what-if workflow should run.
     *
     * @return {@code true} when what-if analysis is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the optional feature name explicitly requested by the user.
     *
     * @return requested feature name, or {@code null}
     */
    public String getFeatureName() {
        return featureName;
    }

    /**
     * Returns the optional classifier identifier explicitly requested by the user.
     *
     * @return requested classifier identifier, or {@code null}
     */
    public String getClassifierId() {
        return classifierId;
    }
}

