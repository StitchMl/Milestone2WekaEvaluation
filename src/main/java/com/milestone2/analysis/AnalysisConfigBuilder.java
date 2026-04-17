package com.milestone2.analysis;

import com.milestone2.classifier.ClassifierIdParser;
import com.milestone2.whatif.WhatIfOptionsBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Builds a validated {@link AnalysisConfig} while keeping CLI defaults in one place.
 */
public class AnalysisConfigBuilder {
    private static final DateTimeFormatter RUN_ID_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final AnalysisPathsBuilder pathsBuilder = new AnalysisPathsBuilder();
    private final AnalysisSelectionBuilder selectionBuilder = new AnalysisSelectionBuilder(new ClassifierIdParser());
    private final AnalysisExecutionBuilder executionBuilder = new AnalysisExecutionBuilder();
    private final WhatIfOptionsBuilder whatIfOptionsBuilder = new WhatIfOptionsBuilder();

    /**
     * Applies one parsed CLI argument to the first builder that recognizes it.
     *
     * @param argument parsed CLI argument
     */
    public void apply(CliArgument argument) {
        if (pathsBuilder.apply(argument)
                || selectionBuilder.apply(argument)
                || executionBuilder.apply(argument)
                || whatIfOptionsBuilder.apply(argument)) {
            return;
        }
        throw new IllegalArgumentException("Unknown argument: --" + argument.getKey());
    }

    /**
     * Materializes the final immutable configuration, injecting the generated run identifier.
     *
     * @return fully populated analysis configuration
     */
    public AnalysisConfig build() {
        return new AnalysisConfig(
                pathsBuilder.build(),
                selectionBuilder.build(),
                executionBuilder.build(RUN_ID_FORMAT.format(ZonedDateTime.now())),
                whatIfOptionsBuilder.build()
        );
    }
}
