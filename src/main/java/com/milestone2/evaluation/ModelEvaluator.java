package com.milestone2.evaluation;

import com.milestone2.startupUtility.RunConfig;
import com.milestone2.startupUtility.ExecutionSettings;
import com.milestone2.classifier.Definition;
import com.milestone2.dataset.ValidationService;
import com.milestone2.foldMetadata.FoldEvaluator;
import com.milestone2.foldMetadata.FoldResult;
import com.milestone2.metric.MetricAggregator;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.validationStrategy.ValidationExecutor;
import com.milestone2.validationStrategy.ExecutorSelector;
import com.milestone2.validationStrategy.ValidationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.List;
import java.util.Map;

/**
 * Validates datasets, coordinates cross-validation and exposes aggregate metrics.
 */
public class ModelEvaluator {
 private static final Logger log = LoggerFactory.getLogger(ModelEvaluator.class);

 private final PositiveClassResolver positiveClassResolver;
 private final ValidationService datasetValidationService;
 private final MetricAggregator metricAggregator;
 private final ExecutorSelector validationExecutorSelector;
 private final FoldEvaluator foldEvaluationService;

 public ModelEvaluator() {
 this(new PositiveClassResolver(),
 new ValidationService(),
 new MetricAggregator(),
 new ExecutorSelector(),
 new FoldEvaluator());
 }

 ModelEvaluator(PositiveClassResolver positiveClassResolver,
 ValidationService datasetValidationService,
 MetricAggregator metricAggregator,
 ExecutorSelector validationExecutorSelector,
 FoldEvaluator foldEvaluationService) {
 this.positiveClassResolver = positiveClassResolver;
 this.datasetValidationService = datasetValidationService;
 this.metricAggregator = metricAggregator;
 this.validationExecutorSelector = validationExecutorSelector;
 this.foldEvaluationService = foldEvaluationService;
 }

 /**
 * Validates the dataset, executes the configured validation strategy and returns the collected fold results.
 *
 * @param definition classifier definition to evaluate
 * @param data dataset to evaluate
 * @param config immutable analysis configuration
 * @param preprocessor preprocessing pipeline builder
 * @return per-fold evaluation results
 * @throws Exception when validation or fold evaluation fails
 */
 public List<FoldResult> evaluateWithFolds(Definition definition,
 Instances data,
 RunConfig config,
 Preprocessor preprocessor) throws Exception {
 ExecutionSettings execution = config.getExecution();
 datasetValidationService.validate(data, config);

 logValidationStart(execution, definition.getDisplayName());
 ValidationExecutor validationExecutor =
 validationExecutorSelector.select(execution.getValidationStrategy());

 List<FoldResult> results = validationExecutor.execute(
 data,
 config,
 (train, test, context) -> foldEvaluationService.evaluate(
 definition,
 config,
 preprocessor,
 train,
 test,
 context
 )
 );

 log.info("Collected {} fold results for {}", results.size(), definition.getDisplayName());
 return results;
 }

 /**
 * Aggregates fold metrics into dataset-level averages.
 *
 * @param results fold-level results
 * @return aggregate metrics map
 */
 public Map<MetricDefinition, Double> aggregate(List<FoldResult> results) {
 return metricAggregator.aggregate(results);
 }

 /**
 * Resolves the positive class label that should be used for binary evaluation metrics.
 *
 * @param data dataset being evaluated
 * @param config immutable analysis configuration
 * @return positive class label
 */
 public String resolvePositiveClassValue(Instances data, RunConfig config) {
 return positiveClassResolver.resolvePositiveClassValue(data.classAttribute(), config);
 }

 /**
 * Emits a strategy-specific log message before validation starts.
 *
 * @param execution execution settings
 * @param classifierName classifier display name
 */
 private void logValidationStart(ExecutionSettings execution, String classifierName) {
 switch (execution.getValidationStrategy()) {
 case CROSS_VALIDATION:
 log.info("=== Starting {}x{}-fold cross-validation for {} ===",
 execution.getRuns(),
 execution.getFolds(),
 classifierName);
 break;
 case ORDERED_HOLDOUT:
 log.info("=== Starting ordered 80/20 holdout for {} ===", classifierName);
 break;
 default:
 log.info("=== Starting walk-forward validation for {} using temporal attribute '{}' ===",
 classifierName,
 execution.getTemporalAttributeName());
 break;
 }
 }
}
