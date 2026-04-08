package com.milestone2.classifier;

/**
 * Validates that the configured Weka classifiers can be instantiated.
 */
public class ClassifierCatalogValidator {
    public void validate(ClassifierCatalog classifierCatalog, long seed) throws Exception {
        for (ClassifierDefinition definition : classifierCatalog.getDefinitions()) {
            TunedClassifierFactory.createClassifier(definition, seed);
        }
    }
}

