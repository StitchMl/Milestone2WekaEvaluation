package com.milestone2.whatif;

import com.milestone2.feature.FeatureCorrelation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Correlation study plus the optional what-if scenario results.
 */
public class WhatIfAnalysisReport {
    private final List<FeatureCorrelation> featureCorrelations;
    private final WhatIfScenarioReport scenarioReport;

    public WhatIfAnalysisReport(List<FeatureCorrelation> featureCorrelations,
                                WhatIfScenarioReport scenarioReport) {
        this.featureCorrelations = Collections.unmodifiableList(new ArrayList<>(featureCorrelations));
        this.scenarioReport = scenarioReport;
    }

    public List<FeatureCorrelation> getFeatureCorrelations() {
        return featureCorrelations;
    }

    public WhatIfScenarioReport getScenarioReport() {
        return scenarioReport;
    }

    public boolean hasScenarioReport() {
        return scenarioReport != null;
    }
}

