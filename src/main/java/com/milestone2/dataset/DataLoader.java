package com.milestone2.dataset;

import com.milestone2.startupUtility.RunConfig;
import com.milestone2.startupUtility.Defaults;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads datasets from CSV or ARFF files and validates the target attribute.
 */
public class DataLoader {
 /**
 * Loads a dataset from disk and resolves its class attribute according to the analysis configuration.
 *
 * @param datasetPath dataset file path
 * @param config immutable analysis configuration
 * @return loaded dataset with class index configured
 * @throws IOException when the file format is unsupported or cannot be read
 */
 public Instances load(Path datasetPath, RunConfig config) throws IOException {
 String filename = datasetPath.getFileName().toString().toLowerCase();
 Instances data;

 if (filename.endsWith(".csv")) {
 CSVLoader loader = new CSVLoader();
 loader.setSource(datasetPath.toFile());
 data = loader.getDataSet();
 } else if (filename.endsWith(".arff")) {
 ArffLoader loader = new ArffLoader();
 loader.setSource(datasetPath.toFile());
 data = loader.getDataSet();
 } else {
 throw new IOException("Unsupported file format: " + datasetPath.getFileName());
 }

 data.setClassIndex(resolveClassIndex(data, config.getSelection().getClassAttributeName()));
 return data;
 }

 /**
 * Loads a dataset located under the configured data directory.
 *
 * @param filename dataset filename relative to the configured data directory
 * @param config immutable analysis configuration
 * @return loaded dataset with class index configured
 * @throws IOException when the file format is unsupported or cannot be read
 */
 public Instances load(String filename, RunConfig config) throws IOException {
 return load(config.getPaths().getDataDir().resolve(filename), config);
 }

 /**
 * Loads a dataset from the default data directory using the default analysis configuration.
 *
 * @param filename dataset filename relative to the default data directory
 * @return loaded dataset with class index configured
 * @throws IOException when the file format is unsupported or cannot be read
 */
 public Instances load(String filename) throws IOException {
 return load(Paths.get(Defaults.DATA_DIR).resolve(filename), RunConfig.fromArgs(new String[0]));
 }

 /**
 * Resolves the index of the class attribute, defaulting to the last attribute when none is configured.
 *
 * @param data loaded dataset
 * @param classAttributeName configured class attribute name
 * @return class attribute index
 */
 private int resolveClassIndex(Instances data, String classAttributeName) {
 if (classAttributeName == null || classAttributeName.isBlank()) {
 return data.numAttributes() - 1;
 }

 Attribute classAttribute = data.attribute(classAttributeName);
 if (classAttribute == null) {
 throw new IllegalArgumentException(
 "Class attribute '" + classAttributeName + "' not found in dataset " + data.relationName()
 );
 }
 return classAttribute.index();
 }
}

