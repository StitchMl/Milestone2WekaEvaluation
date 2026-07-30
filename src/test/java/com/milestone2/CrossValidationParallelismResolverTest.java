package com.milestone2;

import com.milestone2.startupUtility.ExecutionSettings;
import com.milestone2.startupUtility.PreprocessingConfig;
import com.milestone2.startupUtility.ValidationConfig;
import com.milestone2.crossValidation.ParallelismResolver;
import com.milestone2.evaluation.BalancingStrategy;
import com.milestone2.evaluation.FeatureSelectionStrategy;
import com.milestone2.validationStrategy.ValidationStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrossValidationParallelismResolverTest {

 @Test
 void resolveUsesExplicitThreadLimitWhenProvided() {
 ParallelismResolver resolver = new ParallelismResolver(() -> 8);
 ExecutionSettings execution = new ExecutionSettings(
 "run", 10, 10, 42L, 3,
 new PreprocessingConfig(BalancingStrategy.NONE, FeatureSelectionStrategy.NONE),
 new ValidationConfig(ValidationStrategy.CROSS_VALIDATION, "ReleaseId", 1)
 );

 assertEquals(3, resolver.resolve(execution));
 }

 @Test
 void resolveFallsBackToCpuMinusOneWhenThreadsAreAutomatic() {
 ParallelismResolver resolver = new ParallelismResolver(() -> 8);
 ExecutionSettings execution = new ExecutionSettings(
 "run", 10, 10, 42L, 0,
 new PreprocessingConfig(BalancingStrategy.NONE, FeatureSelectionStrategy.NONE),
 new ValidationConfig(ValidationStrategy.CROSS_VALIDATION, "ReleaseId", 1)
 );

 assertEquals(7, resolver.resolve(execution));
 }

 @Test
 void resolveNeverExceedsFoldCount() {
 ParallelismResolver resolver = new ParallelismResolver(() -> 16);
 ExecutionSettings execution = new ExecutionSettings(
 "run", 10, 4, 42L, 12,
 new PreprocessingConfig(BalancingStrategy.NONE, FeatureSelectionStrategy.NONE),
 new ValidationConfig(ValidationStrategy.CROSS_VALIDATION, "ReleaseId", 1)
 );

 assertEquals(4, resolver.resolve(execution));
 }
}

