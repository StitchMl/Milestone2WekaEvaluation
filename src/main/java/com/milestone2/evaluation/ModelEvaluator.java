package com.milestone2.evaluation;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.dataset.DatasetValidationService;
import com.milestone2.fold.FoldEvaluationService;
import com.milestone2.fold.PerFoldResult;
import com.milestone2.metric.MetricAggregator;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.validation.ValidationExecutor;
import com.milestone2.validation.ValidationExecutorSelector;
import com.milestone2.validation.ValidationStrategy;
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
    private final DatasetValidationService datasetValidationService;
    private final MetricAggregator metricAggregator;
    private final ValidationExecutorSelector validationExecutorSelector;
    private final FoldEvaluationService foldEvaluationService;

    public ModelEvaluator() {
        this(new PositiveClassResolver(),
                new DatasetValidationService(),
                new MetricAggregator(),
                new ValidationExecutorSelector(),
                new FoldEvaluationService());
    }

    ModelEvaluator(PositiveClassResolver positiveClassResolver,
                   DatasetValidationService datasetValidationService,
                   MetricAggregator metricAggregator,
                   ValidationExecutorSelector validationExecutorSelector,
                   FoldEvaluationService foldEvaluationService) {
        this.positiveClassResolver = positiveClassResolver;
        this.datasetValidationService = datasetValidationService;
        this.metricAggregator = metricAggregator;
        this.validationExecutorSelector = validationExecutorSelector;
        this.foldEvaluationService = foldEvaluationService;
    }

    /**
     * Validates the dataset, executes the configured validation strategy and returns the collected fold results.
     *
     * @param definition    classifier definition to evaluate
     * @param data          dataset to evaluate
     * @param config        immutable analysis configuration
     * @param preprocessor  preprocessing pipeline builder
     * @return per-fold evaluation results
     * @throws Exception when validation or fold evaluation fails
     */
    public List<PerFoldResult> evaluateWithFolds(ClassifierDefinition definition,
                                                 Instances data,
                                                 AnalysisConfig config,
                                                 Preprocessor preprocessor) throws Exception {
        AnalysisExecution execution = config.getExecution();
        datasetValidationService.validate(data, config);

        logValidationStart(execution, definition.getDisplayName());
        ValidationExecutor validationExecutor =
                validationExecutorSelector.select(execution.getValidationStrategy());

        List<PerFoldResult> results = validationExecutor.execute(
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
    public Map<MetricDefinition, Double> aggregate(List<PerFoldResult> results) {
        return metricAggregator.aggregate(results);
    }

    /**
     * Resolves the positive class label that should be used for binary evaluation metrics.
     *
     * @param data   dataset being evaluated
     * @param config immutable analysis configuration
     * @return positive class label
     */
    public String resolvePositiveClassValue(Instances data, AnalysisConfig config) {
        return positiveClassResolver.resolvePositiveClassValue(data.classAttribute(), config);
    }

    /**
     * Emits a strategy-specific log message before validation starts.
     *
     * @param execution      execution settings
     * @param classifierName classifier display name
     */
    private void logValidationStart(AnalysisExecution execution, String classifierName) {
        if (execution.getValidationStrategy() == ValidationStrategy.CROSS_VALIDATION) {
            log.info("=== Starting {}x{}-fold cross-validation for {} ===",
                    execution.getRuns(),
                    execution.getFolds(),
                    classifierName);
            return;
        }

        log.info("=== Starting walk-forward validation for {} using temporal attribute '{}' ===",
                classifierName,
                execution.getTemporalAttributeName());
    }
}
