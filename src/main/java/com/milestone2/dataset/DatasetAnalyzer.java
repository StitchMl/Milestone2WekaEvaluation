package com.milestone2.dataset;

import com.milestone2.evaluation.ModelEvaluator;
import com.milestone2.evaluation.Preprocessor;
import com.milestone2.fold.PerFoldResult;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.classifier.ClassifierCatalog;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.classifier.ClassifierEvaluationReport;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.whatif.WhatIfAnalysisReport;
import com.milestone2.whatif.WhatIfAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Analyzes one dataset end-to-end and returns a structured report.
 */
public class DatasetAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(DatasetAnalyzer.class);

    private final GenericDataLoader dataLoader;
    private final Preprocessor preprocessor;
    private final ModelEvaluator modelEvaluator;
    private final WhatIfAnalyzer whatIfAnalyzer;

    public DatasetAnalyzer() {
        this(new GenericDataLoader(),
                new Preprocessor(),
                new ModelEvaluator(),
                new WhatIfAnalyzer());
    }

    DatasetAnalyzer(GenericDataLoader dataLoader,
                    Preprocessor preprocessor,
                    ModelEvaluator modelEvaluator,
                    WhatIfAnalyzer whatIfAnalyzer) {
        this.dataLoader = dataLoader;
        this.preprocessor = preprocessor;
        this.modelEvaluator = modelEvaluator;
        this.whatIfAnalyzer = whatIfAnalyzer;
    }

    public DatasetAnalysisReport analyze(Path datasetFile,
                                         AnalysisConfig config,
                                         ClassifierCatalog classifierCatalog) throws Exception {
        String datasetName = datasetFile.getFileName().toString();
        Instances dataset = dataLoader.load(datasetFile, config);
        String positiveClass = modelEvaluator.resolvePositiveClassValue(dataset, config);

        log.info("Processing dataset '{}' with class='{}' positive='{}'",
                datasetName,
                dataset.classAttribute().name(),
                positiveClass);

        List<ClassifierEvaluationReport> classifierReports = new ArrayList<>();
        for (ClassifierDefinition definition : classifierCatalog.getDefinitions()) {
            classifierReports.add(evaluateClassifier(definition, dataset, config));
        }
        WhatIfAnalysisReport whatIfReport =
                whatIfAnalyzer.analyze(dataset, config, classifierReports, preprocessor);

        return new DatasetAnalysisReport(
                datasetName,
                dataset.classAttribute().name(),
                positiveClass,
                classifierReports,
                whatIfReport
        );
    }

    private ClassifierEvaluationReport evaluateClassifier(ClassifierDefinition definition,
                                                          Instances dataset,
                                                          AnalysisConfig config) throws Exception {
        List<PerFoldResult> foldResults =
                modelEvaluator.evaluateWithFolds(definition, dataset, config, preprocessor);
        Map<MetricDefinition, Double> aggregated = modelEvaluator.aggregate(foldResults);
        return new ClassifierEvaluationReport(definition, aggregated, foldResults);
    }
}

