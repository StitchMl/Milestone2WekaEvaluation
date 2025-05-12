package com.milestone2;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;

public class ClassifierFactory {
    private ClassifierFactory() {}

    public static Classifier createRandomForest() {
        RandomForest rf = new RandomForest();
        rf.setNumIterations(50);          // only 10 trees to reduce memory
        rf.setMaxDepth(10);                // maximum depth 5 → smaller trees
        rf.setBagSizePercent(50);         // use only 50% of the instances per tree
        return rf;
    }

    public static Classifier createNaiveBayes() {
        return new NaiveBayes();
    }

    public static Classifier createIBk(int k) {
        return new IBk(k);
    }
}