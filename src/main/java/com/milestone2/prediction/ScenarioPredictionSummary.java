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

    /**
     * Returns the what-if scenario summarized by this object.
     *
     * @return scenario identifier
     */
    public WhatIfScenario getScenario() {
        return scenario;
    }

    /**
     * Returns how many instances belong to the scenario dataset.
     *
     * @return scenario instance count
     */
    public int getInstanceCount() {
        return instanceCount;
    }

    /**
     * Returns how many instances are actually buggy inside the scenario dataset.
     *
     * @return actual buggy count
     */
    public int getActualBuggyCount() {
        return actualBuggyCount;
    }

    /**
     * Returns how many instances were predicted as buggy inside the scenario dataset.
     *
     * @return predicted buggy count
     */
    public int getPredictedBuggyCount() {
        return predictedBuggyCount;
    }

    /**
     * Returns the mean predicted probability of the positive class across the scenario dataset.
     *
     * @return average positive-class probability
     */
    public double getAveragePositiveProbability() {
        return averagePositiveProbability;
    }
}

