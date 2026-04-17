package com.milestone2.whatif;

import com.milestone2.analysis.AnalysisPaths;
import com.milestone2.feature.FeatureCorrelationWriter;

import java.io.IOException;

/**
 * Opens and closes the optional CSV writers used by the what-if workflow.
 */
public class WhatIfOutputs implements AutoCloseable {
    private final FeatureCorrelationWriter featureCorrelationWriter;
    private final WhatIfSummaryWriter whatIfSummaryWriter;

    private WhatIfOutputs(FeatureCorrelationWriter featureCorrelationWriter,
                          WhatIfSummaryWriter whatIfSummaryWriter) {
        this.featureCorrelationWriter = featureCorrelationWriter;
        this.whatIfSummaryWriter = whatIfSummaryWriter;
    }

    /**
     * Opens the CSV writers used by the optional what-if workflow.
     *
     * @param paths analysis output paths
     * @return opened what-if output bundle
     * @throws IOException when a writer cannot be created
     */
    public static WhatIfOutputs open(AnalysisPaths paths) throws IOException {
        return new WhatIfOutputs(
                new FeatureCorrelationWriter(paths.getFeatureCorrelationsCsv()),
                new WhatIfSummaryWriter(paths.getWhatIfSummaryCsv())
        );
    }

    /**
     * Returns the writer used to export feature correlations.
     *
     * @return feature correlation writer
     */
    public FeatureCorrelationWriter getFeatureCorrelationWriter() {
        return featureCorrelationWriter;
    }

    /**
     * Returns the writer used to export what-if scenario summaries.
     *
     * @return what-if summary writer
     */
    public WhatIfSummaryWriter getWhatIfSummaryWriter() {
        return whatIfSummaryWriter;
    }

    /**
     * Closes both writers, preserving the first failure and suppressing any additional ones.
     *
     * @throws IOException when one or more writers fail to close
     */
    @Override
    public void close() throws IOException {
        IOException failure = null;
        failure = close(featureCorrelationWriter, failure);
        failure = close(whatIfSummaryWriter, failure);
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Closes one resource while accumulating failures for deferred propagation.
     *
     * @param closeable resource to close
     * @param failure   previously captured failure, if any
     * @return updated failure accumulator
     */
    private IOException close(AutoCloseable closeable, IOException failure) {
        try {
            closeable.close();
            return failure;
        } catch (Exception exception) {
            if (failure == null && exception instanceof IOException) {
                return (IOException) exception;
            }
            if (failure == null) {
                return new IOException("Failed while closing what-if outputs", exception);
            }
            failure.addSuppressed(exception);
            return failure;
        }
    }
}

