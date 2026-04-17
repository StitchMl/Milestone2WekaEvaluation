package com.milestone2.classifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Builds classifier definitions from a loaded properties catalog.
 */
public class ClassifierDefinitionResolver {
    /**
     * Resolves all classifier definitions requested for the current run.
     *
     * @param properties     loaded classifier catalog
     * @param idsToLoad      ordered classifier identifiers to resolve
     * @param propertiesPath source catalog path used in error messages
     * @return resolved classifier definitions
     */
    public List<ClassifierDefinition> resolve(Properties properties,
                                              List<String> idsToLoad,
                                              Path propertiesPath) {
        List<ClassifierDefinition> definitions = new ArrayList<>();
        for (String id : idsToLoad) {
            definitions.add(resolveDefinition(properties, id));
        }

        if (definitions.isEmpty()) {
            throw new IllegalArgumentException("No classifier definitions available in " + propertiesPath);
        }
        return definitions;
    }

    /**
     * Resolves one classifier definition from the properties catalog.
     *
     * @param properties loaded classifier catalog
     * @param id         classifier identifier to resolve
     * @return resolved classifier definition
     */
    private ClassifierDefinition resolveDefinition(Properties properties, String id) {
        String prefix = "classifier." + id + ".";
        String className = properties.getProperty(prefix + "class");
        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException("Missing classifier class for id: " + id);
        }

        return new ClassifierDefinition(
                id,
                properties.getProperty(prefix + "displayName", id),
                className,
                properties.getProperty(prefix + "options", "")
        );
    }
}

