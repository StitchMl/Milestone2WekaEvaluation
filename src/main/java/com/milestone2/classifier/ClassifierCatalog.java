package com.milestone2.classifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Loads classifier definitions from a properties file.
 */
public class ClassifierCatalog {
    private final List<ClassifierDefinition> definitions;

    private ClassifierCatalog(List<ClassifierDefinition> definitions) {
        this.definitions = Collections.unmodifiableList(new ArrayList<>(definitions));
    }

    /**
     * Loads the classifier catalog from disk and filters it according to the optional selection received from CLI.
     *
     * @param propertiesPath properties file containing the classifier declarations
     * @param selectedIds    optional classifier identifiers requested by the user
     * @return loaded classifier catalog
     * @throws IOException when the catalog file cannot be read
     */
    public static ClassifierCatalog load(Path propertiesPath, List<String> selectedIds) throws IOException {
        Properties properties = new ClassifierPropertiesLoader().load(propertiesPath);
        List<String> idsToLoad = new ClassifierIdResolver().resolve(properties, selectedIds);
        List<ClassifierDefinition> definitions =
                new ClassifierDefinitionResolver().resolve(properties, idsToLoad, propertiesPath);
        return new ClassifierCatalog(definitions);
    }

    /**
     * Returns the classifier definitions that will be evaluated for the current run.
     *
     * @return immutable classifier definitions list
     */
    public List<ClassifierDefinition> getDefinitions() {
        return definitions;
    }
}

