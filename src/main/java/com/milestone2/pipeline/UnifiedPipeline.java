package com.milestone2.pipeline;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.MathExpression;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.supervised.instance.SMOTE;
import weka.core.Instances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It constructs a classifier which it applies in sequence:
 * 1) Log-transform (weka.filters.unsupervised.attribute.MathExpression)
 * 2) Min–Max normalization (weka.filters.unsupervised.attribute.Normalize)
 * 3) SMOTE oversampling (weka.filters.supervised.instance.SMOTE)
 * 4) Feature selection (weka.classifiers.meta.AttributeSelectedClassifier)
 * 5) CalibratedClassifier (weka.classifiers.meta.CalibratedClassifier)
 */
public class UnifiedPipeline {
    private static final Logger logger = LoggerFactory.getLogger(UnifiedPipeline.class);

    /**
     * Private constructor to avoid instantiation.
     * This class is a utility and should not be instantiated.
     */
    private UnifiedPipeline() { /* not instantiated */ }

    /**
     * Returns a Classifier complete with pre-processing,
     * feature-selection and calibration.
     *
     * @param base  Basic Classifier (RandomForest, IBk, ...)
     * @param data  Training Dataset (serves for initFormat)
     * @return      Classifier ready for train/CV
     */
    public static Classifier build(Classifier base, Instances data) throws Exception {
        logger.info("Start of unified pipeline construction");

        // --- 1) Log-transform (log10(x+1)) ---
        logger.debug("I apply logarithmic transformation");
        MathExpression log = new MathExpression();
        log.setExpression("log(A + 1) / log(10)");
        log.setIgnoreRange("last");
        log.setInputFormat(data);

        // --- 2) Normalize Min–Max [0,1] ---
        logger.debug("I apply Min-Max normalisation");
        Normalize norm = new Normalize();
        norm.setInputFormat(data);

        // --- 3) SMOTE oversampling (100%) ---
        logger.debug("I apply SMOTE oversampling");
        SMOTE smote = new SMOTE();
        smote.setPercentage(100.0);
        smote.setInputFormat(data);

        // --- 4) MultiFilter for chaining filters ---
        logger.debug("Create MultiFilter for chaining filters");
        MultiFilter preFilter = new MultiFilter();
        preFilter.setFilters(new Filter[]{ log, norm, smote });
        preFilter.setInputFormat(data);

        // --- 5) Feature selection wrapper ---
        logger.debug("I configure FilteredClassifier and AttributeSelectedClassifier");
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(preFilter);

        AttributeSelectedClassifier asc = new AttributeSelectedClassifier();
        asc.setClassifier(base);
        asc.setEvaluator(new InfoGainAttributeEval());
        asc.setSearch(new Ranker());

        fc.setClassifier(asc);

        logger.info("Successfully constructed unified pipeline");
        return fc;
    }
}