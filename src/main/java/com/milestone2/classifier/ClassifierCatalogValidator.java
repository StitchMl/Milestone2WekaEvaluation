package com.milestone2.classifier;

/**
 * Validates that the configured Weka classifiers can be instantiated.
 */
public class ClassifierCatalogValidator {
    /**
     * Instantiates each configured classifier once to fail fast on invalid classes or options.
     *
     * @param classifierCatalog classifier catalog to validate
     * @param seed              seed propagated to randomizable classifiers
     * @throws Exception when any classifier cannot be created
     */
    public void validate(ClassifierCatalog classifierCatalog, long seed) throws Exception {
        for (ClassifierDefinition definition : classifierCatalog.getDefinitions()) {
            TunedClassifierFactory.createClassifier(definition, seed);
        }
    }
}

