package com.milestone2.classifier;

/**
 * Declarative description of a classifier loaded from configuration.
 */
public class ClassifierDefinition {
    private final String id;
    private final String displayName;
    private final String className;
    private final String options;

    public ClassifierDefinition(String id, String displayName, String className, String options) {
        this.id = id;
        this.displayName = displayName;
        this.className = className;
        this.options = options;
    }

    /**
     * Returns the stable identifier used to reference the classifier in configuration and CLI options.
     *
     * @return classifier identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the user-facing classifier name shown in logs and reports.
     *
     * @return classifier display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the fully qualified Weka classifier class name.
     *
     * @return Weka implementation class
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns the raw Weka option string configured for the classifier.
     *
     * @return classifier options string, possibly blank
     */
    public String getOptions() {
        return options;
    }
}

