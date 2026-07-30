package com.milestone2.classifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Loads classifier definitions from a properties file.
 */
public class Catalog {
 private final List<Definition> definitions;

 private Catalog(List<Definition> definitions) {
 this.definitions = List.copyOf(definitions);
 }

 /**
 * Loads the classifier catalog from disk and filters it according to the optional selection received from CLI.
 *
 * @param propertiesPath properties file containing the classifier declarations
 * @param selectedIds optional classifier identifiers requested by the user
 * @return loaded classifier catalog
 * @throws IOException when the catalog file cannot be read
 */
 public static Catalog load(Path propertiesPath, List<String> selectedIds) throws IOException {
 Properties properties = new PropertiesLoader().load(propertiesPath);
 List<String> idsToLoad = new IdResolver().resolve(properties, selectedIds);
 List<Definition> definitions =
 new DefinitionResolver().resolve(properties, idsToLoad, propertiesPath);
 return new Catalog(definitions);
 }

 /**
 * Returns the classifier definitions that will be evaluated for the current run.
 *
 * @return immutable classifier definitions list
 */
 public List<Definition> getDefinitions() {
 return definitions;
 }
}

