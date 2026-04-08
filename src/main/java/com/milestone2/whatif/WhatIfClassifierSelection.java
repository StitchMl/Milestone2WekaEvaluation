package com.milestone2.whatif;

import com.milestone2.classifier.ClassifierDefinition;

/**
 * Selected classifier for the what-if prediction scenarios.
 */
public class WhatIfClassifierSelection {
    private final ClassifierDefinition definition;
    private final String reason;

    public WhatIfClassifierSelection(ClassifierDefinition definition, String reason) {
        this.definition = definition;
        this.reason = reason;
    }

    public ClassifierDefinition getDefinition() {
        return definition;
    }

    public String getReason() {
        return reason;
    }
}

