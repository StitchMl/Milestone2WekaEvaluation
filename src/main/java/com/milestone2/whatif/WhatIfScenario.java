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

    /**
     * Returns the stable label used in CSV exports and reports for the scenario.
     *
     * @return scenario display name
     */
    public String getDisplayName() {
        return displayName;
    }
}

