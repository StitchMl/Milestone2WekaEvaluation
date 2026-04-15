package com.milestone2.fold;

/**
 * Immutable metadata for one evaluated validation split.
 */
public class FoldContext {
    private final int runIndex;
    private final int foldIndex;
    private final String trainingWindowLabel;
    private final String testWindowLabel;
    private final int trainingInstances;
    private final int testInstances;

    public FoldContext(int runIndex,
                       int foldIndex,
                       String trainingWindowLabel,
                       String testWindowLabel,
                       int trainingInstances,
                       int testInstances) {
        this.runIndex = runIndex;
        this.foldIndex = foldIndex;
        this.trainingWindowLabel = trainingWindowLabel;
        this.testWindowLabel = testWindowLabel;
        this.trainingInstances = trainingInstances;
        this.testInstances = testInstances;
    }

    /**
     * Builds context metadata for randomized cross-validation.
     *
     * @param runIndex          repeated run index
     * @param foldIndex         fold index inside the run
     * @param trainingInstances number of rows in the training split
     * @param testInstances     number of rows in the test split
     * @return fold context for cross-validation
     */
    public static FoldContext crossValidation(int runIndex,
                                              int foldIndex,
                                              int trainingInstances,
                                              int testInstances) {
        return new FoldContext(runIndex, foldIndex, null, null, trainingInstances, testInstances);
    }

    /**
     * Builds context metadata for one walk-forward split.
     *
     * @param foldIndex            walk-forward window index
     * @param trainingWindowLabel  label describing the accumulated training periods
     * @param testWindowLabel      label describing the future test period
     * @param trainingInstances    number of rows in the training split
     * @param testInstances        number of rows in the test split
     * @return fold context for walk-forward validation
     */
    public static FoldContext walkForward(int foldIndex,
                                          String trainingWindowLabel,
                                          String testWindowLabel,
                                          int trainingInstances,
                                          int testInstances) {
        return new FoldContext(0, foldIndex, trainingWindowLabel, testWindowLabel,
                trainingInstances, testInstances);
    }

    public int getRunIndex() {
        return runIndex;
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

    public int getTrainingInstances() {
        return trainingInstances;
    }

    public int getTestInstances() {
        return testInstances;
    }
}
