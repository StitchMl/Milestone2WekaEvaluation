package com.milestone2;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest;

public enum ClassifierType {
    RANDOM_FOREST, NAIVE_BAYES, IBK;

    public Classifier build() {
        switch (this) {
            case RANDOM_FOREST: return new RandomForest();
            case NAIVE_BAYES:   return new NaiveBayes();
            case IBK:           return new IBk();
            default: throw new IllegalArgumentException();
        }
    }
}