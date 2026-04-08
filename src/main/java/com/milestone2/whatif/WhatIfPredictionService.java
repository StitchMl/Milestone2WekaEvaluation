package com.milestone2.whatif;

import com.milestone2.evaluation.PositiveClassResolver;
import com.milestone2.prediction.PredictionRecord;
import com.milestone2.evaluation.Preprocessor;
import com.milestone2.classifier.TunedClassifierFactory;
import com.milestone2.analysis.AnalysisConfig;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Trains the selected classifier on A and evaluates the what-if scenarios.
 */
public class WhatIfPredictionService {
    private final PositiveClassResolver positiveClassResolver;
    private final WhatIfScenarioSummarizer scenarioSummarizer;

    public WhatIfPredictionService() {
        this(new PositiveClassResolver(), new WhatIfScenarioSummarizer());
    }

    WhatIfPredictionService(PositiveClassResolver positiveClassResolver,
                            WhatIfScenarioSummarizer scenarioSummarizer) {
        this.positiveClassResolver = positiveClassResolver;
        this.scenarioSummarizer = scenarioSummarizer;
    }

    public WhatIfScenarioReport evaluate(Instances originalDataset,
                                         WhatIfDatasetSet datasetSet,
                                         WhatIfFeatureSelection featureSelection,
                                         WhatIfClassifierSelection classifierSelection,
                                         AnalysisConfig config,
                                         Preprocessor preprocessor) throws Exception {
        Classifier classifier = TunedClassifierFactory.createClassifier(
                classifierSelection.getDefinition(),
                config.getExecution().getSeed()
        );
        FilteredClassifier pipeline = preprocessor.buildPipeline(classifier, config);
        pipeline.buildClassifier(originalDataset);

        int positiveClassIndex = positiveClassResolver.resolvePositiveClassIndex(originalDataset.classAttribute(), config);
        List<PredictionRecord> originalPredictions = predict(datasetSet.getOriginalDataset(), pipeline, positiveClassIndex);
        List<PredictionRecord> bPlusPredictions = predict(datasetSet.getBPlusDataset(), pipeline, positiveClassIndex);
        List<PredictionRecord> bPredictions = predict(datasetSet.getBDataset(), pipeline, positiveClassIndex);
        List<PredictionRecord> cPredictions = predict(datasetSet.getCDataset(), pipeline, positiveClassIndex);

        return new WhatIfScenarioReport(
                featureSelection,
                classifierSelection,
                List.of(
                        scenarioSummarizer.summarize(WhatIfScenario.A, originalPredictions),
                        scenarioSummarizer.summarize(WhatIfScenario.B_PLUS, bPlusPredictions),
                        scenarioSummarizer.summarize(WhatIfScenario.B, bPredictions),
                        scenarioSummarizer.summarize(WhatIfScenario.C, cPredictions)
                ),
                scenarioSummarizer.summarizeImpact(bPlusPredictions, bPredictions)
        );
    }

    private List<PredictionRecord> predict(Instances dataset,
                                           FilteredClassifier pipeline,
                                           int positiveClassIndex) throws Exception {
        List<PredictionRecord> predictions = new ArrayList<>();
        for (Instance instance : dataset) {
            double[] distribution = pipeline.distributionForInstance(instance);
            predictions.add(new PredictionRecord(
                    (int) instance.classValue() == positiveClassIndex,
                    Utils.maxIndex(distribution) == positiveClassIndex,
                    distribution[positiveClassIndex]
            ));
        }
        return predictions;
    }
}

