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

    /**
     * Returns the zero-based index of the walk-forward window.
     *
     * @return window index
     */
    public int getFoldIndex() {
        return foldIndex;
    }

    /**
     * Returns the label describing the accumulated training periods.
     *
     * @return training window label
     */
    public String getTrainingWindowLabel() {
        return trainingWindowLabel;
    }

    /**
     * Returns the label of the future period used for testing.
     *
     * @return test window label
     */
    public String getTestWindowLabel() {
        return testWindowLabel;
    }

    /**
     * Returns the dataset built from the accumulated training history.
     *
     * @return training data
     */
    public Instances getTrainingData() {
        return trainingData;
    }

    /**
     * Returns the dataset containing the future test period.
     *
     * @return test data
     */
    public Instances getTestData() {
        return testData;
    }
}
