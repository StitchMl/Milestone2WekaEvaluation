package com.milestone2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.supervised.instance.SMOTE;

/**
 * The Preprocessor class applies a series of preprocessing steps to a Weka Instances dataset.
 * Steps include:
 *   - Removing string attributes (e.g., IDs or text data).
 *   - Converting nominal attributes to binary (one-hot encoding).
 *   - Replacing or imputing missing values.
 *   - Standardizing numeric attributes.
 *   - Applying SMOTE to balance class distribution.
 * Each step logs its operation for traceability.
 */
public class Preprocessor {
    private static final Logger log = LoggerFactory.getLogger(Preprocessor.class);

    /**
     * Applies preprocessing filters to the input dataset.
     *
     * @param data the original Instances dataset to preprocess (must have a class index set before calling).
     * @return a new Instances object with preprocessing applied.
     * @throws Exception if any filter fails during processing.
     */
    public Instances applyFilters(Instances data) throws Exception {
        log.info("Starting preprocessing: {} instances, {} attributes",
                data.numInstances(), data.numAttributes());

        Instances processedData = data;

        // 1) Remove string attributes (e.g., ID, file paths) if present
        log.info("Removing string attributes if any");
        RemoveType removeStringFilter = new RemoveType();
        removeStringFilter.setOptions(new String[]{"-T", "string"});
        removeStringFilter.setInputFormat(processedData);
        processedData = Filter.useFilter(processedData, removeStringFilter);
        log.info("After RemoveType: {} instances, {} attributes",
                processedData.numInstances(), processedData.numAttributes());

        // 2) Convert multivalued nominal attributes into binary (one-hot encoding)
        log.info("Converting nominal attributes to binary");
        NominalToBinary nominalToBinaryFilter = new NominalToBinary();
        nominalToBinaryFilter.setInputFormat(processedData);
        processedData = Filter.useFilter(processedData, nominalToBinaryFilter);
        log.info("After NominalToBinary: {} attributes", processedData.numAttributes());

        // 3) Replace missing values (imputation)
        log.info("Replacing missing values");
        ReplaceMissingValues missingValuesFilter = new ReplaceMissingValues();
        missingValuesFilter.setInputFormat(processedData);
        processedData = Filter.useFilter(processedData, missingValuesFilter);
        log.info("After ReplaceMissingValues: missing values filled (if any)");

        // 4) Standardize numeric attributes (mean=0, std=1)
        log.info("Standardizing numeric attributes");
        Standardize standardizeFilter = new Standardize();
        standardizeFilter.setInputFormat(processedData);
        processedData = Filter.useFilter(processedData, standardizeFilter);
        log.info("After Standardize: data has zero mean and unit variance (numeric attributes)");

        // 5) Apply SMOTE to balance the class distribution (if applicable)
        log.info("Applying SMOTE to balance class distribution");
        // Note: SMOTE requires the class attribute to be set and nominal.
        SMOTE smoteFilter = new SMOTE();
        smoteFilter.setInputFormat(processedData);
        processedData = Filter.useFilter(processedData, smoteFilter);
        log.info("After SMOTE: {} instances", processedData.numInstances());

        // 6) Ensure the class attribute is set to the last attribute
        if (processedData.classIndex() == -1) {
            processedData.setClassIndex(processedData.numAttributes() - 1);
            log.info("Class attribute not set. Setting class index to last attribute: index {}",
                    processedData.classIndex());
        } else {
            log.info("Class attribute already set to index {}", processedData.classIndex());
        }

        log.info("Preprocessing completed: final dataset has {} instances, {} attributes",
                processedData.numInstances(), processedData.numAttributes());
        return processedData;
    }
}