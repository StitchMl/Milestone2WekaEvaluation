package com.milestone2.analysis;

import com.milestone2.whatif.WhatIfOptions;

/**
 * Immutable runtime configuration for a single analysis run.
 */
public class AnalysisConfig {
    private final AnalysisPaths paths;
    private final AnalysisSelection selection;
    private final AnalysisExecution execution;
    private final WhatIfOptions whatIfOptions;

    AnalysisConfig(AnalysisPaths paths,
                   AnalysisSelection selection,
                   AnalysisExecution execution,
                   WhatIfOptions whatIfOptions) {
        this.paths = paths;
        this.selection = selection;
        this.execution = execution;
        this.whatIfOptions = whatIfOptions;
    }

    /**
     * Builds a validated configuration from CLI arguments.
     *
     * @param args CLI arguments in {@code --key=value} form
     * @return immutable analysis configuration
     */
    public static AnalysisConfig fromArgs(String[] args) {
        return new AnalysisArgumentsParser().parse(args);
    }

    /**
     * Returns the filesystem paths used by the current analysis run.
     *
     * @return analysis paths
     */
    public AnalysisPaths getPaths() {
        return paths;
    }

    /**
     * Returns the user-facing selection options that drive the analysis.
     *
     * @return analysis selection settings
     */
    public AnalysisSelection getSelection() {
        return selection;
    }

    /**
     * Returns the execution parameters that control validation and runtime behavior.
     *
     * @return execution settings
     */
    public AnalysisExecution getExecution() {
        return execution;
    }

    /**
     * Returns the optional what-if configuration associated with the run.
     *
     * @return what-if options
     */
    public WhatIfOptions getWhatIfOptions() {
        return whatIfOptions;
    }
}

