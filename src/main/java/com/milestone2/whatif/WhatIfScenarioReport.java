package com.milestone2.whatif;

import com.milestone2.prediction.ScenarioPredictionSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Full what-if study outcome for one dataset.
 */
public class WhatIfScenarioReport {
    private final WhatIfFeatureSelection featureSelection;
    private final WhatIfClassifierSelection classifierSelection;
    private final List<ScenarioPredictionSummary> scenarioSummaries;
    private final WhatIfImpactSummary impactSummary;

    public WhatIfScenarioReport(WhatIfFeatureSelection featureSelection,
                                WhatIfClassifierSelection classifierSelection,
                                List<ScenarioPredictionSummary> scenarioSummaries,
                                WhatIfImpactSummary impactSummary) {
        this.featureSelection = featureSelection;
        this.classifierSelection = classifierSelection;
        this.scenarioSummaries = Collections.unmodifiableList(new ArrayList<>(scenarioSummaries));
        this.impactSummary = impactSummary;
    }

    public WhatIfFeatureSelection getFeatureSelection() {
        return featureSelection;
    }

    public WhatIfClassifierSelection getClassifierSelection() {
        return classifierSelection;
    }

    public List<ScenarioPredictionSummary> getScenarioSummaries() {
        return scenarioSummaries;
    }

    public WhatIfImpactSummary getImpactSummary() {
        return impactSummary;
    }
}

