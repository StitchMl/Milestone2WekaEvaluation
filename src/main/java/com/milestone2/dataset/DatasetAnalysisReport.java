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

    public String getDatasetName() {
        return datasetName;
    }

    public String getClassAttributeName() {
        return classAttributeName;
    }

    public String getPositiveClassValue() {
        return positiveClassValue;
    }

    public List<ClassifierEvaluationReport> getClassifierReports() {
        return classifierReports;
    }

    public WhatIfAnalysisReport getWhatIfReport() {
        return whatIfReport;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasWhatIfReport() {
        return whatIfReport != null;
    }
}

