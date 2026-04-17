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

    /**
     * Applies one CLI argument that influences data interpretation or classifier selection.
     *
     * @param argument parsed CLI argument
     * @return {@code true} when the argument belongs to this builder, {@code false} otherwise
     */
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

    /**
     * Creates the immutable selection settings gathered from CLI arguments.
     *
     * @return selection settings snapshot
     */
    public AnalysisSelection build() {
        return new AnalysisSelection(
                granularity,
                classAttributeName,
                positiveClassValue,
                sizeAttributeName,
                classifierIds
        );
    }

    /**
     * Converts blank CLI values to {@code null} so downstream components can apply defaults.
     *
     * @param raw raw CLI value
     * @return trimmed semantic value, or {@code null} when blank
     */
    private String emptyToNull(String raw) {
        return raw == null || raw.isBlank() ? null : raw;
    }
}
