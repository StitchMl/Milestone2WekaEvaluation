package com.milestone2;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;

/**
 * Enum representing the different types of classifiers available in the project.
 * Each constant corresponds to a Weka classifier that can be instantiated.
 */
public enum ClassifierType {
    /** Classificatore Random Forest (bosco di alberi casuali) */
    RANDOM_FOREST,
    /** Classificatore Naive Bayes (Bayes ingenuo) */
    NAIVE_BAYES,
    /** Classificatore IBk (K-Nearest Neighbors basato su istanze) */
    K_NEAREST_NEIGHBORS;

    /**
     * Creates an instance of the Weka classifier corresponding to the selected type.
     *
     * @return a Classifier object for the specified type
     */
    public Classifier createClassifier() {
        switch (this) {
            case RANDOM_FOREST:
                return new RandomForest();
            case NAIVE_BAYES:
                return new NaiveBayes();
            case K_NEAREST_NEIGHBORS:
                return new IBk();
            default:
                // It should never happen but managed for completeness
                throw new IllegalArgumentException("Type of classifier not recognised: " + this);
        }
    }
}