package com.milestone2.fold;

import com.milestone2.evaluation.PositiveClassResolver;
import com.milestone2.evaluation.Preprocessor;
import com.milestone2.metric.NPofB20Calculator;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.classifier.TunedClassifierFactory;
import com.milestone2.metric.Metrics;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Evaluates one train/test fold and computes all metrics for it.
 */
public class FoldEvaluationService {
    private final PositiveClassResolver positiveClassResolver;
    private final NPofB20Calculator npOfB20Calculator;

    public FoldEvaluationService() {
        this(new PositiveClassResolver(), new NPofB20Calculator());
    }

    FoldEvaluationService(PositiveClassResolver positiveClassResolver,
                          NPofB20Calculator npOfB20Calculator) {
        this.positiveClassResolver = positiveClassResolver;
        this.npOfB20Calculator = npOfB20Calculator;
    }

    public PerFoldResult evaluate(ClassifierDefinition definition,
                                  AnalysisConfig config,
                                  Preprocessor preprocessor,
                                  Instances train,
                                  Instances test,
                                  FoldContext context) throws Exception {
        AnalysisExecution execution = config.getExecution();
        Classifier baseClassifier = TunedClassifierFactory.createClassifier(
                definition,
                execution.getSeed() + (context.getRunIndex() * 1_000L) + context.getFoldIndex()
        );
        FilteredClassifier pipeline = preprocessor.buildPipeline(baseClassifier, config);
        pipeline.buildClassifier(train);

        Evaluation evaluation = new Evaluation(train);
        for (Instance instance : test) {
            evaluation.evaluateModelOnceAndRecordPrediction(
                    pipeline.distributionForInstance(instance),
                    instance
            );
        }

        int positiveClassIndex = positiveClassResolver.resolvePositiveClassIndex(train.classAttribute(), config);
        Metrics metrics = new Metrics(
                (1 - evaluation.errorRate()) * 100.0,
                evaluation.precision(positiveClassIndex),
                evaluation.recall(positiveClassIndex),
                evaluation.fMeasure(positiveClassIndex),
                evaluation.kappa(),
                evaluation.areaUnderROC(positiveClassIndex),
                npOfB20Calculator.compute(
                        evaluation,
                        test,
                        positiveClassIndex,
                        config.getSelection().getSizeAttributeName()
                )
        );

        return new PerFoldResult(
                context.getRunIndex(),
                context.getFoldIndex(),
                context.getTrainingWindowLabel(),
                context.getTestWindowLabel(),
                context.getTrainingInstances(),
                context.getTestInstances(),
                metrics
        );
    }
}

