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

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getClassName() {
        return className;
    }

    public String getOptions() {
        return options;
    }
}

