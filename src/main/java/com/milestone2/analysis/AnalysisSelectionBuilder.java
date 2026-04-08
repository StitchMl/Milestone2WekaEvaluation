package com.milestone2.analysis;

import com.milestone2.classifier.ClassifierIdParser;

import java.util.List;

/**
 * Collects CLI values that affect data interpretation and classifier selection.
 */
public class AnalysisSelectionBuilder {
    private final ClassifierIdParser classifierIdParser;

    private AnalysisGranularity granularity = Config.DEFAULT_GRANULARITY;
    private String classAttributeName;
    private String positiveClassValue;
    private String sizeAttributeName = Config.DEFAULT_SIZE_ATTRIBUTE;
    private List<String> classifierIds = List.of();

    public AnalysisSelectionBuilder(ClassifierIdParser classifierIdParser) {
        this.classifierIdParser = classifierIdParser;
    }

    public boolean apply(CliArgument argument) {
        switch (argument.getKey()) {
            case "granularity":
                granularity = AnalysisGranularity.from(argument.getValue());
                return true;
            case "class-attribute":
                classAttributeName = emptyToNull(argument.getValue());
                return true;
            case "positive-class":
                positiveClassValue = emptyToNull(argument.getValue());
                return true;
            case "size-attribute":
                sizeAttributeName = argument.getValue().isBlank()
                        ? Config.DEFAULT_SIZE_ATTRIBUTE
                        : argument.getValue();
                return true;
            case "classifiers":
                classifierIds = classifierIdParser.parse(argument.getValue());
                return true;
            default:
                return false;
        }
    }

    public AnalysisSelection build() {
        return new AnalysisSelection(
                granularity,
                classAttributeName,
                positiveClassValue,
                sizeAttributeName,
                classifierIds
        );
    }

    private String emptyToNull(String raw) {
        return raw == null || raw.isBlank() ? null : raw;
    }
}

