package com.milestone2.whatif;

import com.milestone2.analysis.AnalysisPaths;
import com.milestone2.analysis.OutputCloseSupport;
import com.milestone2.feature.FeatureCorrelationWriter;

import java.io.IOException;

/**
 * Opens and closes the optional CSV writers used by the what-if workflow.
 */
public class WhatIfOutputs implements AutoCloseable {
 private static final String CLOSE_MESSAGE = "Failed while closing what-if outputs";

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
 failure = OutputCloseSupport.closeQuietly(featureCorrelationWriter, failure, CLOSE_MESSAGE);
 failure = OutputCloseSupport.closeQuietly(whatIfSummaryWriter, failure, CLOSE_MESSAGE);
 if (failure != null) {
 throw failure;
 }
 }
}
