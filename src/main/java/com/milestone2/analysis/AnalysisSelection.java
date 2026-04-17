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

    /**
     * Returns the repository granularity represented by the input dataset.
     *
     * @return analysis granularity
     */
    public AnalysisGranularity getGranularity() {
        return granularity;
    }

    /**
     * Returns the configured class attribute name, or {@code null} when the last attribute should be used.
     *
     * @return class attribute name
     */
    public String getClassAttributeName() {
        return classAttributeName;
    }

    /**
     * Returns the positive class label requested by the user, when explicitly provided.
     *
     * @return configured positive class label, or {@code null}
     */
    public String getPositiveClassValue() {
        return positiveClassValue;
    }

    /**
     * Returns the attribute used as inspection cost or entity size in ranking metrics.
     *
     * @return size attribute name
     */
    public String getSizeAttributeName() {
        return sizeAttributeName;
    }

    /**
     * Returns the classifier identifiers explicitly requested from the catalog.
     *
     * @return ordered classifier identifiers, possibly empty
     */
    public List<String> getClassifierIds() {
        return classifierIds;
    }
}

