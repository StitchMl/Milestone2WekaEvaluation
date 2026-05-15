package com.milestone2.analysis;

import com.milestone2.evaluation.BalancingStrategy;
import com.milestone2.evaluation.FeatureSelectionStrategy;

/**
 * Groups the two preprocessing knobs so that {@link AnalysisExecution} stays within the
 * seven-parameter constructor limit recommended by static-analysis rules.
 */
public class PreprocessingConfig {
 private final BalancingStrategy balancingStrategy;
 private final FeatureSelectionStrategy featureSelectionStrategy;

 public PreprocessingConfig(BalancingStrategy balancingStrategy,
 FeatureSelectionStrategy featureSelectionStrategy) {
 this.balancingStrategy = balancingStrategy;
 this.featureSelectionStrategy = featureSelectionStrategy;
 }

 /**
 * Returns the balancing strategy to apply inside the preprocessing pipeline.
 *
 * @return balancing strategy
 */
 public BalancingStrategy getBalancingStrategy() {
 return balancingStrategy;
 }

 /**
 * Returns the feature-selection strategy applied before balancing inside each training fold.
 *
 * @return feature-selection strategy
 */
 public FeatureSelectionStrategy getFeatureSelectionStrategy() {
 return featureSelectionStrategy;
 }
}
