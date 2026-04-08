package com.milestone2.evaluation;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.crossvalidation.CrossValidationExecutor;
import com.milestone2.dataset.DatasetValidationService;
import com.milestone2.fold.FoldEvaluationService;
import com.milestone2.fold.PerFoldResult;
import com.milestone2.metric.MetricAggregator;
import com.milestone2.metric.MetricDefinition;
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
    private final CrossValidationExecutor crossValidationExecutor;
    private final FoldEvaluationService foldEvaluationService;

    public ModelEvaluator() {
        this(new PositiveClassResolver(),
                new DatasetValidationService(),
                new MetricAggregator(),
                new CrossValidationExecutor(),
                new FoldEvaluationService());
    }

    ModelEvaluator(PositiveClassResolver positiveClassResolver,
                   DatasetValidationService datasetValidationService,
                   MetricAggregator metricAggregator,
                   CrossValidationExecutor crossValidationExecutor,
                   FoldEvaluationService foldEvaluationService) {
        this.positiveClassResolver = positiveClassResolver;
        this.datasetValidationService = datasetValidationService;
        this.metricAggregator = metricAggregator;
        this.crossValidationExecutor = crossValidationExecutor;
        this.foldEvaluationService = foldEvaluationService;
    }

    public List<PerFoldResult> evaluateWithFolds(ClassifierDefinition definition,
                                                 Instances data,
                                                 AnalysisConfig config,
                                                 Preprocessor preprocessor) throws Exception {
        AnalysisExecution execution = config.getExecution();
        datasetValidationService.validate(data, config);

        log.info("=== Starting {}x{}-fold cross-validation for {} ===",
                execution.getRuns(),
                execution.getFolds(),
                definition.getDisplayName());

        List<PerFoldResult> results = crossValidationExecutor.execute(
                data,
                config,
                (train, test, runIndex, foldIndex) -> foldEvaluationService.evaluate(
                        definition,
                        config,
                        preprocessor,
                        train,
                        test,
                        runIndex,
                        foldIndex
                )
        );

        log.info("Collected {} fold results for {}", results.size(), definition.getDisplayName());
        return results;
    }

    public Map<MetricDefinition, Double> aggregate(List<PerFoldResult> results) {
        return metricAggregator.aggregate(results);
    }

    public String resolvePositiveClassValue(Instances data, AnalysisConfig config) {
        return positiveClassResolver.resolvePositiveClassValue(data.classAttribute(), config);
    }
}


