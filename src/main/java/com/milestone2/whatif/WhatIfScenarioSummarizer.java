package com.milestone2.whatif;

import com.milestone2.prediction.PredictionRecord;
import com.milestone2.prediction.ScenarioPredictionSummary;

import java.util.List;

/**
 * Aggregates raw what-if predictions into scenario and impact summaries.
 */
public class WhatIfScenarioSummarizer {
    /**
     * Summarizes the raw predictions produced for one scenario dataset.
     *
     * @param scenario    scenario identifier
     * @param predictions raw prediction records
     * @return aggregated scenario summary
     */
    public ScenarioPredictionSummary summarize(WhatIfScenario scenario, List<PredictionRecord> predictions) {
        int actualBuggyCount = 0;
        int predictedBuggyCount = 0;
        double totalPositiveProbability = 0.0;

        for (PredictionRecord prediction : predictions) {
            if (prediction.isActualPositive()) {
                actualBuggyCount++;
            }
            if (prediction.isPredictedPositive()) {
                predictedBuggyCount++;
            }
            totalPositiveProbability += prediction.getPositiveProbability();
        }

        double averagePositiveProbability = predictions.isEmpty()
                ? 0.0
                : totalPositiveProbability / predictions.size();
        return new ScenarioPredictionSummary(
                scenario,
                predictions.size(),
                actualBuggyCount,
                predictedBuggyCount,
                averagePositiveProbability
        );
    }

    /**
     * Compares paired B+ and B predictions to quantify the impact of zeroing the selected feature.
     *
     * @param bPlusPredictions predictions obtained on dataset B+
     * @param bPredictions     predictions obtained on dataset B
     * @return paired impact summary
     */
    public WhatIfImpactSummary summarizeImpact(List<PredictionRecord> bPlusPredictions,
                                               List<PredictionRecord> bPredictions) {
        int pairedInstances = Math.min(bPlusPredictions.size(), bPredictions.size());
        int actualBuggyCount = 0;
        int predictedRelievedCount = 0;
        int avoidableBuggyCount = 0;
        double probabilityReduction = 0.0;

        for (int index = 0; index < pairedInstances; index++) {
            PredictionRecord withFeature = bPlusPredictions.get(index);
            PredictionRecord withoutFeature = bPredictions.get(index);

            if (withFeature.isActualPositive()) {
                actualBuggyCount++;
            }
            if (withFeature.isPredictedPositive() && !withoutFeature.isPredictedPositive()) {
                predictedRelievedCount++;
                if (withFeature.isActualPositive()) {
                    avoidableBuggyCount++;
                }
            }
            probabilityReduction += withFeature.getPositiveProbability() - withoutFeature.getPositiveProbability();
        }

        double averageProbabilityReduction = pairedInstances == 0 ? 0.0 : probabilityReduction / pairedInstances;
        double avoidableBuggyShare = actualBuggyCount == 0 ? 0.0 : (double) avoidableBuggyCount / actualBuggyCount;
        return new WhatIfImpactSummary(
                pairedInstances,
                actualBuggyCount,
                predictedRelievedCount,
                avoidableBuggyCount,
                avoidableBuggyShare,
                averageProbabilityReduction
        );
    }
}

