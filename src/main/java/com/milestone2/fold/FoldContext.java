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

    /**
     * Returns the repeated-run index associated with the split.
     *
     * @return run index, or {@code 0} for walk-forward validation
     */
    public int getRunIndex() {
        return runIndex;
    }

    /**
     * Returns the fold or window index inside the validation strategy.
     *
     * @return fold index
     */
    public int getFoldIndex() {
        return foldIndex;
    }

    /**
     * Returns the label of the accumulated training window, when available.
     *
     * @return training window label, or {@code null} for cross-validation
     */
    public String getTrainingWindowLabel() {
        return trainingWindowLabel;
    }

    /**
     * Returns the label of the test window, when available.
     *
     * @return test window label, or {@code null} for cross-validation
     */
    public String getTestWindowLabel() {
        return testWindowLabel;
    }

    /**
     * Returns how many instances belong to the training split.
     *
     * @return training instance count
     */
    public int getTrainingInstances() {
        return trainingInstances;
    }

    /**
     * Returns how many instances belong to the test split.
     *
     * @return test instance count
     */
    public int getTestInstances() {
        return testInstances;
    }
}
