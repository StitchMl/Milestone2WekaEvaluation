package com.milestone2.pipeline;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.MathExpression;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.supervised.instance.Resample;
import weka.core.Instances;

/**
 * Constructs a FilteredClassifier that applies:
 * 1) Log-transform (log10(x+1))
 * 2) Min-Max normalization [0,1].
 * 3) SMOTE for positive class balancing
 * before invoking the chosen classifier.
 */
public class PreprocessingPipeline {
    /**
     * Private constructor to avoid instantiation.
     */
    private PreprocessingPipeline() {
        // It does not have to be instantiated
    }

    /**
     * Creates a FilteredClassifier with the filter pipeline + the baseClassifier.
     *
     * @param baseClassifier the Classifier (e.g., RandomForest, IBk, NaiveBayes)
     * @param data the training dataset (serves to initFormat the filters)
     * @return FilteredClassifier ready for CV or training
     * @throws Exception in case of errors in filter configuration
     */
    public static FilteredClassifier build(Classifier baseClassifier, Instances data) throws Exception {
        // 1) Log-transform (log10(A+1)), ignores the
        MathExpression logFilter = new MathExpression();
        logFilter.setExpression("log(A + 1) / log(10)");
        logFilter.setIgnoreRange("last");
        logFilter.setInputFormat(data);

        // 2) Normalize (Min–Max 0–1)
        Normalize normFilter = new Normalize();
        normFilter.setScale(1.0);
        normFilter.setTranslation(0.0);
        normFilter.setInputFormat(data);

        // 3) Resample as an alternative to SMOTE for balancing
        Resample resampleFilter = new Resample();
        resampleFilter.setBiasToUniformClass(1.0);
        resampleFilter.setSampleSizePercent(100.0);
        resampleFilter.setInputFormat(data);

        // 4) MultiFilter: chain the two filters
        MultiFilter multi = new MultiFilter();
        Filter[] filters = new Filter[] { logFilter, normFilter };
        multi.setFilters(filters);
        multi.setInputFormat(data);
        multi.setInputFormat(data);

        // 5) FilteredClassifier: combines pre-processing and classifier
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(multi);
        fc.setClassifier(baseClassifier);

        return fc;
    }
}