package com.milestone2.whatif;

import com.milestone2.evaluation.Preprocessor;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.classifier.ClassifierEvaluationReport;
import com.milestone2.feature.FeatureCorrelation;
import com.milestone2.feature.FeatureCorrelationAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.List;

/**
 * Coordinates the exam-oriented correlation study and what-if prediction flow.
 */
public class WhatIfAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(WhatIfAnalyzer.class);

    private final FeatureCorrelationAnalyzer featureCorrelationAnalyzer;
    private final WhatIfFeatureSelector featureSelector;
    private final WhatIfClassifierSelector classifierSelector;
    private final WhatIfDatasetBuilder datasetBuilder;
    private final WhatIfPredictionService predictionService;

    public WhatIfAnalyzer() {
        this(new FeatureCorrelationAnalyzer(),
                new WhatIfFeatureSelector(),
                new WhatIfClassifierSelector(),
                new WhatIfDatasetBuilder(),
                new WhatIfPredictionService());
    }

    WhatIfAnalyzer(FeatureCorrelationAnalyzer featureCorrelationAnalyzer,
                   WhatIfFeatureSelector featureSelector,
                   WhatIfClassifierSelector classifierSelector,
                   WhatIfDatasetBuilder datasetBuilder,
                   WhatIfPredictionService predictionService) {
        this.featureCorrelationAnalyzer = featureCorrelationAnalyzer;
        this.featureSelector = featureSelector;
        this.classifierSelector = classifierSelector;
        this.datasetBuilder = datasetBuilder;
        this.predictionService = predictionService;
    }

    /**
     * Runs the complete what-if workflow for one dataset, from correlation study to scenario prediction summaries.
     *
     * @param data               dataset to analyze
     * @param config             immutable analysis configuration
     * @param classifierReports  evaluated classifier reports
     * @param preprocessor       preprocessing pipeline builder
     * @return what-if analysis report, or {@code null} when the workflow is disabled
     * @throws Exception when feature selection or scenario evaluation fails
     */
    public WhatIfAnalysisReport analyze(Instances data,
                                        AnalysisConfig config,
                                        List<ClassifierEvaluationReport> classifierReports,
                                        Preprocessor preprocessor) throws Exception {
        if (!config.getWhatIfOptions().isEnabled()) {
            return null;
        }

        List<FeatureCorrelation> correlations = featureCorrelationAnalyzer.analyze(data, config);
        WhatIfFeatureSelection featureSelection =
                featureSelector.select(data, config.getWhatIfOptions(), correlations);
        if (featureSelection == null) {
            log.warn("Skipping what-if scenario for dataset '{}' because no zeroable numeric feature was found",
                    data.relationName());
            return new WhatIfAnalysisReport(correlations, null);
        }

        WhatIfClassifierSelection classifierSelection =
                classifierSelector.select(config.getWhatIfOptions(), classifierReports);
        WhatIfDatasetSet datasetSet = datasetBuilder.build(data, featureSelection);
        WhatIfScenarioReport scenarioReport = predictionService.evaluate(
                data,
                datasetSet,
                featureSelection,
                classifierSelection,
                config,
                preprocessor
        );

        log.info("What-if analysis for '{}' will manipulate feature '{}' using classifier '{}'",
                data.relationName(),
                featureSelection.getFeatureName(),
                classifierSelection.getDefinition().getDisplayName());
        return new WhatIfAnalysisReport(correlations, scenarioReport);
    }
}

