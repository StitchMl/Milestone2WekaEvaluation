package com.milestone2;

/**
 * Configuration with design constants.
 */
public class Config {

    private Config() {
        // Prevent instantiation
    }

    public static final String DATA_DIR = "src/main/resources/data/";
    public static final String OUTPUT_DIR = "output/";
    public static final String RESULTS_CSV = OUTPUT_DIR + "results.csv";
    public static final String FOLD_CSV = OUTPUT_DIR + "fold_metrics.csv";
    public static final String CHARTS_DIR = OUTPUT_DIR + "charts/";

    // Number of executions and number of folds for cross-validation
    public static final int N_RUNS = 10;
    public static final int N_FOLDS = 10;
}