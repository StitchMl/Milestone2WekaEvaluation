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

    public static WhatIfOutputs open(AnalysisPaths paths) throws IOException {
        return new WhatIfOutputs(
                new FeatureCorrelationWriter(paths.getFeatureCorrelationsCsv()),
                new WhatIfSummaryWriter(paths.getWhatIfSummaryCsv())
        );
    }

    public FeatureCorrelationWriter getFeatureCorrelationWriter() {
        return featureCorrelationWriter;
    }

    public WhatIfSummaryWriter getWhatIfSummaryWriter() {
        return whatIfSummaryWriter;
    }

    @Override
    public void close() throws IOException {
        IOException failure = null;
        failure = close(featureCorrelationWriter, failure);
        failure = close(whatIfSummaryWriter, failure);
        if (failure != null) {
            throw failure;
        }
    }

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

