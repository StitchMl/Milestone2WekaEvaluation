package com.milestone2.startupUtility;

import com.milestone2.classifier.IdParser;
import com.milestone2.whatif.WhatIfOptionsBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Builds a validated {@link RunConfig} while keeping CLI defaults in one place.
 */
public class RunConfigBuilder {
 private static final DateTimeFormatter RUN_ID_FORMAT =
 DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

 private final ResolvedPathsBuilder pathsBuilder = new ResolvedPathsBuilder();
 private final SelectionSettingsBuilder selectionBuilder = new SelectionSettingsBuilder(new IdParser());
 private final ExecutionSettingsBuilder executionBuilder = new ExecutionSettingsBuilder();
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
 public RunConfig build() {
 return new RunConfig(
 pathsBuilder.build(),
 selectionBuilder.build(),
 executionBuilder.build(RUN_ID_FORMAT.format(ZonedDateTime.now(ZoneId.systemDefault()))),
 whatIfOptionsBuilder.build()
 );
 }
}
