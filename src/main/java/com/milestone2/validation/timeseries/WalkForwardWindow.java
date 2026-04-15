package com.milestone2.validation.timeseries;

import weka.core.Instances;

/**
 * One walk-forward split made of accumulated history and the next future period.
 */
public class WalkForwardWindow {
    private final int foldIndex;
    private final String trainingWindowLabel;
    private final String testWindowLabel;
    private final Instances trainingData;
    private final Instances testData;

    public WalkForwardWindow(int foldIndex,
                             String trainingWindowLabel,
                             String testWindowLabel,
                             Instances trainingData,
                             Instances testData) {
        this.foldIndex = foldIndex;
        this.trainingWindowLabel = trainingWindowLabel;
        this.testWindowLabel = testWindowLabel;
        this.trainingData = trainingData;
        this.testData = testData;
    }

    public int getFoldIndex() {
        return foldIndex;
    }

    public String getTrainingWindowLabel() {
        return trainingWindowLabel;
    }

    public String getTestWindowLabel() {
        return testWindowLabel;
    }

    public Instances getTrainingData() {
        return trainingData;
    }

    public Instances getTestData() {
        return testData;
    }
}
