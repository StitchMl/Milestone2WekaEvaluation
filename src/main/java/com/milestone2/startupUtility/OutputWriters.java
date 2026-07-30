package com.milestone2.startupUtility;

import com.milestone2.foldMetadata.FoldResultsWriter;
import com.milestone2.summary.SummaryWriter;
import com.milestone2.csvExporter.ResultsWriter;
import com.milestone2.whatif.WhatIfOutputs;

import java.io.IOException;

/**
 * Opens and closes the CSV writers used by one analysis execution.
 */
public class OutputWriters implements AutoCloseable {
 private static final String CLOSE_MESSAGE = "Failed while closing analysis outputs";

 private final ResultsWriter resultsWriter;
 private final FoldResultsWriter foldResultsWriter;
 private final SummaryWriter milestone2SummaryWriter;
 private final WhatIfOutputs whatIfOutputs;

 private OutputWriters(ResultsWriter resultsWriter,
 FoldResultsWriter foldResultsWriter,
 SummaryWriter milestone2SummaryWriter,
 WhatIfOutputs whatIfOutputs) {
 this.resultsWriter = resultsWriter;
 this.foldResultsWriter = foldResultsWriter;
 this.milestone2SummaryWriter = milestone2SummaryWriter;
 this.whatIfOutputs = whatIfOutputs;
 }

 /**
 * Opens all writers required by the configured analysis, including optional what-if outputs.
 *
 * @param config immutable analysis configuration
 * @return opened output bundle
 * @throws IOException when any writer cannot be created
 */
 public static OutputWriters open(RunConfig config) throws IOException {
 ResolvedPaths paths = config.getPaths();
 return new OutputWriters(
 new ResultsWriter(paths.getResultsCsv()),
 new FoldResultsWriter(paths.getFoldCsv()),
 new SummaryWriter(paths.getMilestone2SummaryCsv()),
 config.getWhatIfOptions().isEnabled() ? WhatIfOutputs.open(paths) : null
 );
 }

 /**
 * Returns the CSV writer for aggregate evaluation results.
 *
 * @return results writer
 */
 public ResultsWriter getResultsWriter() {
 return resultsWriter;
 }

 /**
 * Returns the CSV writer for per-fold validation metrics.
 *
 * @return fold results writer
 */
 public FoldResultsWriter getFoldResultsWriter() {
 return foldResultsWriter;
 }

 /**
 * Returns the CSV writer for milestone-oriented summary rows.
 *
 * @return milestone summary writer
 */
 public SummaryWriter getMilestone2SummaryWriter() {
 return milestone2SummaryWriter;
 }

 /**
 * Indicates whether optional what-if writers were opened for the current run.
 *
 * @return {@code true} when what-if outputs are available
 */
 public boolean hasWhatIfOutputs() {
 return whatIfOutputs != null;
 }

 /**
 * Returns the what-if output bundle.
 *
 * @return what-if writers bundle
 * @throws IllegalStateException when what-if outputs are disabled
 */
 public WhatIfOutputs getWhatIfOutputs() {
 if (whatIfOutputs == null) {
 throw new IllegalStateException("What-if outputs are not enabled for this analysis run");
 }
 return whatIfOutputs;
 }

 /**
 * Closes every opened writer, preserving the first failure and suppressing the rest.
 *
 * @throws IOException when one or more outputs fail to close
 */
 @Override
 public void close() throws IOException {
 IOException failure = null;
 failure = WriterCloseSupport.closeQuietly(resultsWriter, failure, CLOSE_MESSAGE);
 failure = WriterCloseSupport.closeQuietly(foldResultsWriter, failure, CLOSE_MESSAGE);
 failure = WriterCloseSupport.closeQuietly(milestone2SummaryWriter, failure, CLOSE_MESSAGE);
 failure = WriterCloseSupport.closeQuietly(whatIfOutputs, failure, CLOSE_MESSAGE);
 if (failure != null) {
 throw failure;
 }
 }
}
