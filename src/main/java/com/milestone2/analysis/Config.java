package com.milestone2.analysis;

import com.milestone2.validation.ValidationStrategy;

/**
 * Configuration with design constants.
 */
public class Config {

    private Config() {
        // Prevent instantiation
    }

    public static final String DATA_DIR = "src/main/resources/data";
    public static final String OUTPUT_DIR = "output";
    public static final String RESULTS_CSV = "results.csv";
    public static final String FOLD_CSV = "fold_metrics.csv";
    public static final String MILESTONE2_SUMMARY_CSV = "milestone2_summary.csv";
    public static final String FEATURE_CORRELATIONS_CSV = "feature_correlations.csv";
    public static final String WHAT_IF_SUMMARY_CSV = "what_if_summary.csv";
    public static final String CHARTS_DIR = "charts";
    public static final String CLASSIFIERS_CONFIG = "classifiers.properties";
    public static final String DEFAULT_SIZE_ATTRIBUTE = "LOC";
    public static final String DEFAULT_WHAT_IF_FEATURE = "NSmells";
    public static final String DEFAULT_TEMPORAL_ATTRIBUTE = "ReleaseId";

    // Default execution settings
    public static final int DEFAULT_RUNS = 1;
    public static final int DEFAULT_FOLDS = 10;
    public static final long DEFAULT_SEED = 42L;
    public static final int DEFAULT_MAX_PARALLELISM = 0;
    public static final boolean DEFAULT_APPLY_SMOTE = false;
    public static final boolean DEFAULT_WHAT_IF_ENABLED = true;
    public static final int DEFAULT_MINIMUM_TRAINING_PERIODS = 1;
    public static final ValidationStrategy DEFAULT_VALIDATION_STRATEGY = ValidationStrategy.WALK_FORWARD;
    public static final AnalysisGranularity DEFAULT_GRANULARITY = AnalysisGranularity.CLASS;
}

