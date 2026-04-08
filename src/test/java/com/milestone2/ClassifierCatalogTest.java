package com.milestone2;

import com.milestone2.classifier.ClassifierCatalog;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassifierCatalogTest {

    @Test
    void loadReturnsConfiguredDefinitionsInDeclaredOrder() throws Exception {
        Path properties = Files.createTempFile("classifiers", ".properties");
        Files.writeString(properties, String.format(
                "classifiers=RF,NB%n" +
                        "classifier.RF.displayName=Random Forest%n" +
                        "classifier.RF.class=weka.classifiers.trees.RandomForest%n" +
                        "classifier.RF.options=-I 25%n" +
                        "classifier.NB.displayName=Naive Bayes%n" +
                        "classifier.NB.class=weka.classifiers.bayes.NaiveBayes%n"
        ), StandardCharsets.UTF_8);

        try {
            ClassifierCatalog catalog = ClassifierCatalog.load(properties, List.of());

            assertEquals(2, catalog.getDefinitions().size());
            assertEquals("RF", catalog.getDefinitions().get(0).getId());
            assertEquals("Random Forest", catalog.getDefinitions().get(0).getDisplayName());
            assertEquals("weka.classifiers.trees.RandomForest", catalog.getDefinitions().get(0).getClassName());
            assertEquals("-I 25", catalog.getDefinitions().get(0).getOptions());
            assertEquals("NB", catalog.getDefinitions().get(1).getId());
        } finally {
            Files.deleteIfExists(properties);
        }
    }
}

