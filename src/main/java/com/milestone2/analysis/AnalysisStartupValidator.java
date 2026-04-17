package com.milestone2.analysis;

import com.milestone2.classifier.ClassifierCatalog;
import com.milestone2.classifier.ClassifierCatalogValidator;

/**
 * Performs startup validation for paths and classifier catalog wiring.
 */
public class AnalysisStartupValidator {
    private final AnalysisPathValidator analysisPathValidator;
    private final ClassifierCatalogValidator classifierCatalogValidator;

    public AnalysisStartupValidator() {
        this(new AnalysisPathValidator(), new ClassifierCatalogValidator());
    }

    AnalysisStartupValidator(AnalysisPathValidator analysisPathValidator,
                             ClassifierCatalogValidator classifierCatalogValidator) {
        this.analysisPathValidator = analysisPathValidator;
        this.classifierCatalogValidator = classifierCatalogValidator;
    }

    /**
     * Validates filesystem prerequisites and confirms that every configured classifier can be instantiated.
     *
     * @param config             immutable analysis configuration
     * @param classifierCatalog  loaded classifier catalog
     * @throws Exception when startup validation fails
     */
    public void validate(AnalysisConfig config, ClassifierCatalog classifierCatalog) throws Exception {
        analysisPathValidator.validate(config.getPaths());
        classifierCatalogValidator.validate(classifierCatalog, config.getExecution().getSeed());
    }
}

