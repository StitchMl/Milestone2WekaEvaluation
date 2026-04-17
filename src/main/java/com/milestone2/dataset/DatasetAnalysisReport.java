package com.milestone2.dataset;

import com.milestone2.whatif.WhatIfAnalysisReport;
import com.milestone2.classifier.ClassifierEvaluationReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the full outcome of analyzing a single dataset.
 */
public class DatasetAnalysisReport {
    private final String datasetName;
    private final String classAttributeName;
    private final String positiveClassValue;
    private final List<ClassifierEvaluationReport> classifierReports;
    private final WhatIfAnalysisReport whatIfReport;

    public DatasetAnalysisReport(String datasetName,
                                 String classAttributeName,
                                 String positiveClassValue,
                                 List<ClassifierEvaluationReport> classifierReports) {
        this(datasetName, classAttributeName, positiveClassValue, classifierReports, null);
    }

    public DatasetAnalysisReport(String datasetName,
                                 String classAttributeName,
                                 String positiveClassValue,
                                 List<ClassifierEvaluationReport> classifierReports,
                                 WhatIfAnalysisReport whatIfReport) {
        this.datasetName = datasetName;
        this.classAttributeName = classAttributeName;
        this.positiveClassValue = positiveClassValue;
        this.classifierReports = Collections.unmodifiableList(new ArrayList<>(classifierReports));
        this.whatIfReport = whatIfReport;
    }

    /**
     * Returns the analyzed dataset name as reported by the loader.
     *
     * @return dataset name
     */
    public String getDatasetName() {
        return datasetName;
    }

    /**
     * Returns the class attribute used during evaluation.
     *
     * @return class attribute name
     */
    public String getClassAttributeName() {
        return classAttributeName;
    }

    /**
     * Returns the positive class label used to compute binary metrics.
     *
     * @return positive class label
     */
    public String getPositiveClassValue() {
        return positiveClassValue;
    }

    /**
     * Returns the classifier reports generated for this dataset.
     *
     * @return immutable classifier reports list
     */
    public List<ClassifierEvaluationReport> getClassifierReports() {
        return classifierReports;
    }

    /**
     * Returns the optional what-if analysis report associated with the dataset.
     *
     * @return what-if report, or {@code null} when the workflow is disabled or skipped
     */
    public WhatIfAnalysisReport getWhatIfReport() {
        return whatIfReport;
    }

    /**
     * Indicates whether a what-if report is attached to this dataset analysis.
     *
     * @return {@code true} when a what-if report is available
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasWhatIfReport() {
        return whatIfReport != null;
    }
}
