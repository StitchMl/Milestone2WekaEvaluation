package com.milestone2.classifier;

import com.milestone2.config.ConfigLoader;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;

public class RandomForestProvider implements ClassifierProvider {
    private final int numTrees;
    private final int maxDepth;
    private final int bagPercent;

    public RandomForestProvider(ConfigLoader config) {
        this.numTrees     = config.getIntProperty("rf.numTrees", 50);
        this.maxDepth     = config.getIntProperty("rf.maxDepth", 10);
        this.bagPercent   = config.getIntProperty("rf.bagSizePercent", 50);
    }

    @Override
    public String name() {
        return "RandomForest";
    }

    @Override
    public Classifier create() {
        RandomForest rf = new RandomForest();
        rf.setNumIterations(numTrees);
        rf.setMaxDepth(maxDepth);
        rf.setBagSizePercent(bagPercent);
        return rf;
    }
}