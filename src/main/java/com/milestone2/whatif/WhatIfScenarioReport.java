package com.milestone2.whatif;

import com.milestone2.prediction.ScenarioSummary;

import java.util.List;

/**
 * Full what-if study outcome for one dataset.
 */
public class WhatIfScenarioReport {
 private final WhatIfFeatureSelection featureSelection;
 private final WhatIfClassifierSelection classifierSelection;
 private final List<ScenarioSummary> scenarioSummaries;
 private final WhatIfImpactSummary impactSummary;

 public WhatIfScenarioReport(WhatIfFeatureSelection featureSelection,
 WhatIfClassifierSelection classifierSelection,
 List<ScenarioSummary> scenarioSummaries,
 WhatIfImpactSummary impactSummary) {
 this.featureSelection = featureSelection;
 this.classifierSelection = classifierSelection;
 this.scenarioSummaries = List.copyOf(scenarioSummaries);
 this.impactSummary = impactSummary;
 }

 /**
 * Returns the feature selected for dataset manipulation.
 *
 * @return selected feature
 */
 public WhatIfFeatureSelection getFeatureSelection() {
 return featureSelection;
 }

 /**
 * Returns the classifier selected to generate the scenario predictions.
 *
 * @return selected classifier
 */
 public WhatIfClassifierSelection getClassifierSelection() {
 return classifierSelection;
 }

 /**
 * Returns the per-scenario prediction summaries.
 *
 * @return immutable scenario summaries list
 */
 public List<ScenarioSummary> getScenarioSummaries() {
 return scenarioSummaries;
 }

 /**
 * Returns the paired B+/B impact summary.
 *
 * @return impact summary
 */
 public WhatIfImpactSummary getImpactSummary() {
 return impactSummary;
 }
}

