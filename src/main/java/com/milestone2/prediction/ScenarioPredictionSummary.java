package com.milestone2.prediction;

import com.milestone2.whatif.WhatIfScenario;

/**
 * Aggregated prediction view for one what-if scenario dataset.
 */
public class ScenarioPredictionSummary {
    private final WhatIfScenario scenario;
    private final int instanceCount;
    private final int actualBuggyCount;
    private final int predictedBuggyCount;
    private final double averagePositiveProbability;

    public ScenarioPredictionSummary(WhatIfScenario scenario,
                                     int instanceCount,
                                     int actualBuggyCount,
                                     int predictedBuggyCount,
                                     double averagePositiveProbability) {
        this.scenario = scenario;
        this.instanceCount = instanceCount;
        this.actualBuggyCount = actualBuggyCount;
        this.predictedBuggyCount = predictedBuggyCount;
        this.averagePositiveProbability = averagePositiveProbability;
    }

    public WhatIfScenario getScenario() {
        return scenario;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public int getActualBuggyCount() {
        return actualBuggyCount;
    }

    public int getPredictedBuggyCount() {
        return predictedBuggyCount;
    }

    public double getAveragePositiveProbability() {
        return averagePositiveProbability;
    }
}

