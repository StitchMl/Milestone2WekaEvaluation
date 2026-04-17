package com.milestone2.analysis;

import com.milestone2.fold.FoldResultsWriter;
import com.milestone2.summary.Milestone2SummaryWriter;
import com.milestone2.report.ResultsWriter;
import com.milestone2.whatif.WhatIfOutputs;

import java.io.IOException;

/**
 * Opens and closes the CSV writers used by one analysis execution.
 */
public class AnalysisOutputs implements AutoCloseable {
    private final ResultsWriter resultsWriter;
    private final FoldResultsWriter foldResultsWriter;
    private final Milestone2SummaryWriter milestone2SummaryWriter;
    private final WhatIfOutputs whatIfOutputs;

    private AnalysisOutputs(ResultsWriter resultsWriter,
                            FoldResultsWriter foldResultsWriter,
                            Milestone2SummaryWriter milestone2SummaryWriter,
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
    public static AnalysisOutputs open(AnalysisConfig config) throws IOException {
        AnalysisPaths paths = config.getPaths();
        return new AnalysisOutputs(
                new ResultsWriter(paths.getResultsCsv()),
                new FoldResultsWriter(paths.getFoldCsv()),
                new Milestone2SummaryWriter(paths.getMilestone2SummaryCsv()),
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
    public Milestone2SummaryWriter getMilestone2SummaryWriter() {
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
        failure = close(resultsWriter, failure);
        failure = close(foldResultsWriter, failure);
        failure = close(milestone2SummaryWriter, failure);
        failure = close(whatIfOutputs, failure);
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Closes one output resource while accumulating failures instead of aborting immediately.
     *
     * @param closeable resource to close, possibly {@code null}
     * @param failure   previously captured failure, if any
     * @return updated failure accumulator
     */
    private IOException close(AutoCloseable closeable, IOException failure) {
        if (closeable == null) {
            return failure;
        }
        try {
            closeable.close();
            return failure;
        } catch (Exception exception) {
            if (failure == null && exception instanceof IOException) {
                return (IOException) exception;
            }
            if (failure == null) {
                return new IOException("Failed while closing analysis outputs", exception);
            }
            failure.addSuppressed(exception);
            return failure;
        }
    }
}

