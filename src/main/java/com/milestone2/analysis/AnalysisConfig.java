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

    public static AnalysisConfig fromArgs(String[] args) {
        return new AnalysisArgumentsParser().parse(args);
    }

    public AnalysisPaths getPaths() {
        return paths;
    }

    public AnalysisSelection getSelection() {
        return selection;
    }

    public AnalysisExecution getExecution() {
        return execution;
    }

    public WhatIfOptions getWhatIfOptions() {
        return whatIfOptions;
    }
}

