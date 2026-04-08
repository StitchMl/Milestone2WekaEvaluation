package com.milestone2.classifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Resolves which classifier identifiers must be loaded from the properties catalog.
 */
public class ClassifierIdResolver {
    private static final String CLASSIFIERS_KEY = "classifiers";

    private final ClassifierIdParser classifierIdParser;

    public ClassifierIdResolver() {
        this(new ClassifierIdParser());
    }

    ClassifierIdResolver(ClassifierIdParser classifierIdParser) {
        this.classifierIdParser = classifierIdParser;
    }

    public List<String> resolve(Properties properties, List<String> selectedIds) {
        return selectedIds.isEmpty() ? configuredIds(properties) : selectedIds;
    }

    private List<String> configuredIds(Properties properties) {
        String configured = properties.getProperty(CLASSIFIERS_KEY, "");
        if (!configured.isBlank()) {
            return classifierIdParser.parse(configured);
        }

        Set<String> ids = new TreeSet<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("classifier.") && key.endsWith(".class")) {
                String id = key.substring("classifier.".length(), key.length() - ".class".length());
                ids.add(id);
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }
}

