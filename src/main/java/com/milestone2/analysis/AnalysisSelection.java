package com.milestone2.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User-facing selection knobs that shape how the analysis interprets data.
 */
public class AnalysisSelection {
    private final AnalysisGranularity granularity;
    private final String classAttributeName;
    private final String positiveClassValue;
    private final String sizeAttributeName;
    private final List<String> classifierIds;

    public AnalysisSelection(AnalysisGranularity granularity,
                             String classAttributeName,
                             String positiveClassValue,
                             String sizeAttributeName,
                             List<String> classifierIds) {
        this.granularity = granularity;
        this.classAttributeName = classAttributeName;
        this.positiveClassValue = positiveClassValue;
        this.sizeAttributeName = sizeAttributeName;
        this.classifierIds = Collections.unmodifiableList(new ArrayList<>(classifierIds));
    }

    public AnalysisGranularity getGranularity() {
        return granularity;
    }

    public String getClassAttributeName() {
        return classAttributeName;
    }

    public String getPositiveClassValue() {
        return positiveClassValue;
    }

    public String getSizeAttributeName() {
        return sizeAttributeName;
    }

    public List<String> getClassifierIds() {
        return classifierIds;
    }
}

