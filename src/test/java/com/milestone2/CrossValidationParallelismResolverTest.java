package com.milestone2;

import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.analysis.PreprocessingConfig;
import com.milestone2.analysis.ValidationConfig;
import com.milestone2.crossvalidation.CrossValidationParallelismResolver;
import com.milestone2.evaluation.BalancingStrategy;
import com.milestone2.evaluation.FeatureSelectionStrategy;
import com.milestone2.validation.ValidationStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrossValidationParallelismResolverTest {

 @Test
 void resolveUsesExplicitThreadLimitWhenProvided() {
 CrossValidationParallelismResolver resolver = new CrossValidationParallelismResolver(() -> 8);
 AnalysisExecution execution = new AnalysisExecution(
 "run", 10, 10, 42L, 3,
 new PreprocessingConfig(BalancingStrategy.NONE, FeatureSelectionStrategy.NONE),
 new ValidationConfig(ValidationStrategy.CROSS_VALIDATION, "ReleaseId", 1)
 );

 assertEquals(3, resolver.resolve(execution));
 }

 @Test
 void resolveFallsBackToCpuMinusOneWhenThreadsAreAutomatic() {
 CrossValidationParallelismResolver resolver = new CrossValidationParallelismResolver(() -> 8);
 AnalysisExecution execution = new AnalysisExecution(
 "run", 10, 10, 42L, 0,
 new PreprocessingConfig(BalancingStrategy.NONE, FeatureSelectionStrategy.NONE),
 new ValidationConfig(ValidationStrategy.CROSS_VALIDATION, "ReleaseId", 1)
 );

 assertEquals(7, resolver.resolve(execution));
 }

 @Test
 void resolveNeverExceedsFoldCount() {
 CrossValidationParallelismResolver resolver = new CrossValidationParallelismResolver(() -> 16);
 AnalysisExecution execution = new AnalysisExecution(
 "run", 10, 4, 42L, 12,
 new PreprocessingConfig(BalancingStrategy.NONE, FeatureSelectionStrategy.NONE),
 new ValidationConfig(ValidationStrategy.CROSS_VALIDATION, "ReleaseId", 1)
 );

 assertEquals(4, resolver.resolve(execution));
 }
}

