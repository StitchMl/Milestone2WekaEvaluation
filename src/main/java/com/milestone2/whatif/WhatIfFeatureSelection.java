package com.milestone2.whatif;

import com.milestone2.featureAnalysis.Correlation;

/**
 * Selected feature for the what-if scenario generation.
 */
public class WhatIfFeatureSelection {
 private final Correlation correlation;
 private final String reason;

 public WhatIfFeatureSelection(Correlation correlation, String reason) {
 this.correlation = correlation;
 this.reason = reason;
 }

 /**
 * Returns the full correlation entry associated with the selected feature.
 *
 * @return selected feature correlation
 */
 @SuppressWarnings("unused")
 public Correlation getCorrelation() {
 return correlation;
 }

 /**
 * Returns the name of the selected feature.
 *
 * @return selected feature name
 */
 public String getFeatureName() {
 return correlation.getFeatureName();
 }

 /**
 * Returns the explanation of how the feature was selected.
 *
 * @return selection reason
 */
 public String getReason() {
 return reason;
 }
}
