package com.milestone2.whatif;

import com.milestone2.feature.FeatureCorrelation;

import java.util.List;

/**
 * Correlation study plus the optional what-if scenario results.
 */
public class WhatIfAnalysisReport {
    private final List<FeatureCorrelation> featureCorrelations;
    private final WhatIfScenarioReport scenarioReport;

    public WhatIfAnalysisReport(List<FeatureCorrelation> featureCorrelations,
                                WhatIfScenarioReport scenarioReport) {
        this.featureCorrelations = List.copyOf(featureCorrelations);
        this.scenarioReport = scenarioReport;
    }

    /**
     * Returns the ranked feature-correlation study produced for the dataset.
     *
     * @return immutable feature correlations list
     */
    public List<FeatureCorrelation> getFeatureCorrelations() {
        return featureCorrelations;
    }

    /**
     * Returns the optional scenario report produced by the what-if workflow.
     *
     * @return what-if scenario report, or {@code null} when scenario generation was skipped
     */
    public WhatIfScenarioReport getScenarioReport() {
        return scenarioReport;
    }

    /**
     * Indicates whether a scenario report is available.
     *
     * @return {@code true} when what-if scenarios were evaluated
     */
    public boolean hasScenarioReport() {
        return scenarioReport != null;
    }
}

