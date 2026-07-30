package com.milestone2.startupUtility;

import com.milestone2.whatif.WhatIfOptions;

/**
 * Immutable runtime configuration for a single analysis run.
 */
public class RunConfig {
 private final ResolvedPaths paths;
 private final SelectionSettings selection;
 private final ExecutionSettings execution;
 private final WhatIfOptions whatIfOptions;

 RunConfig(ResolvedPaths paths,
 SelectionSettings selection,
 ExecutionSettings execution,
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
 public static RunConfig fromArgs(String[] args) {
 return new CliArgumentsParser().parse(args);
 }

 /**
 * Returns the filesystem paths used by the current analysis run.
 *
 * @return analysis paths
 */
 public ResolvedPaths getPaths() {
 return paths;
 }

 /**
 * Returns the user-facing selection options that drive the analysis.
 *
 * @return analysis selection settings
 */
 public SelectionSettings getSelection() {
 return selection;
 }

 /**
 * Returns the execution parameters that control validation and runtime behavior.
 *
 * @return execution settings
 */
 public ExecutionSettings getExecution() {
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

