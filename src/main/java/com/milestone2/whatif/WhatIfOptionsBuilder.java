package com.milestone2.whatif;

import com.milestone2.analysis.CliArgument;
import com.milestone2.analysis.Config;

/**
 * Collects CLI values that control the optional what-if workflow.
 */
public class WhatIfOptionsBuilder {
    private boolean enabled = Config.DEFAULT_WHAT_IF_ENABLED;
    private String featureName;
    private String classifierId;

    public boolean apply(CliArgument argument) {
        switch (argument.getKey()) {
            case "whatif":
                enabled = Boolean.parseBoolean(argument.getValue());
                return true;
            case "whatif-feature":
                featureName = emptyToNull(argument.getValue());
                return true;
            case "whatif-classifier":
                classifierId = emptyToNull(argument.getValue());
                return true;
            default:
                return false;
        }
    }

    public WhatIfOptions build() {
        return new WhatIfOptions(enabled, featureName, classifierId);
    }

    private String emptyToNull(String raw) {
        return raw == null || raw.isBlank() ? null : raw;
    }
}

