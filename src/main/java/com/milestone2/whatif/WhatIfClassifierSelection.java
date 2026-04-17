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

    /**
     * Returns the classifier selected for scenario prediction.
     *
     * @return selected classifier definition
     */
    public ClassifierDefinition getDefinition() {
        return definition;
    }

    /**
     * Returns the explanation of how the classifier was selected.
     *
     * @return selection reason
     */
    public String getReason() {
        return reason;
    }
}

