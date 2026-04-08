package com.milestone2.whatif;

/**
 * Named datasets used by the exam what-if study.
 */
public enum WhatIfScenario {
    A("A"),
    B_PLUS("B+"),
    B("B"),
    C("C");

    private final String displayName;

    WhatIfScenario(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

