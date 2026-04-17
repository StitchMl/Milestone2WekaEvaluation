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

    /**
     * Applies one CLI argument related to the optional what-if workflow.
     *
     * @param argument parsed CLI argument
     * @return {@code true} when the argument belongs to this builder, {@code false} otherwise
     */
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

    /**
     * Creates the immutable what-if options snapshot.
     *
     * @return what-if options
     */
    public WhatIfOptions build() {
        return new WhatIfOptions(enabled, featureName, classifierId);
    }

    /**
     * Converts blank CLI values to {@code null} so downstream selection logic can apply defaults.
     *
     * @param raw raw CLI value
     * @return semantic value, or {@code null} when blank
     */
    private String emptyToNull(String raw) {
        return raw == null || raw.isBlank() ? null : raw;
    }
}

