package com.milestone2;

import com.milestone2.classifier.Catalog;
import com.milestone2.classifier.CatalogValidator;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ClassifierCatalogValidatorTest {

 @Test
 void validateInstantiatesCurrentWekaClassifiersFromRepositoryConfig() {
 Path propertiesPath = Paths.get("classifiers.properties");

 assertDoesNotThrow(() -> {
 Catalog catalog = Catalog.load(propertiesPath, List.of());
 new CatalogValidator().validate(catalog, 42L);
 });
 }
}

