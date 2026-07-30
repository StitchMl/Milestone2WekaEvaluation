package com.milestone2.startupUtility;

import com.milestone2.classifier.Catalog;
import com.milestone2.classifier.CatalogValidator;

/**
 * Performs startup validation for paths and classifier catalog wiring.
 */
public class StartupValidator {
 private final PathValidator analysisPathValidator;
 private final CatalogValidator classifierCatalogValidator;

 public StartupValidator() {
 this(new PathValidator(), new CatalogValidator());
 }

 StartupValidator(PathValidator analysisPathValidator,
 CatalogValidator classifierCatalogValidator) {
 this.analysisPathValidator = analysisPathValidator;
 this.classifierCatalogValidator = classifierCatalogValidator;
 }

 /**
 * Validates filesystem prerequisites and confirms that every configured classifier can be instantiated.
 *
 * @param config immutable analysis configuration
 * @param classifierCatalog loaded classifier catalog
 * @throws Exception when startup validation fails
 */
 public void validate(RunConfig config, Catalog classifierCatalog) throws Exception {
 analysisPathValidator.validate(config.getPaths());
 classifierCatalogValidator.validate(classifierCatalog, config.getExecution().getSeed());
 }
}

