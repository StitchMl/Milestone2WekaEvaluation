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

    public static AnalysisOutputs open(AnalysisConfig config) throws IOException {
        AnalysisPaths paths = config.getPaths();
        return new AnalysisOutputs(
                new ResultsWriter(paths.getResultsCsv()),
                new FoldResultsWriter(paths.getFoldCsv()),
                new Milestone2SummaryWriter(paths.getMilestone2SummaryCsv()),
                config.getWhatIfOptions().isEnabled() ? WhatIfOutputs.open(paths) : null
        );
    }

    public ResultsWriter getResultsWriter() {
        return resultsWriter;
    }

    public FoldResultsWriter getFoldResultsWriter() {
        return foldResultsWriter;
    }

    public Milestone2SummaryWriter getMilestone2SummaryWriter() {
        return milestone2SummaryWriter;
    }

    public boolean hasWhatIfOutputs() {
        return whatIfOutputs != null;
    }

    public WhatIfOutputs getWhatIfOutputs() {
        if (whatIfOutputs == null) {
            throw new IllegalStateException("What-if outputs are not enabled for this analysis run");
        }
        return whatIfOutputs;
    }

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

