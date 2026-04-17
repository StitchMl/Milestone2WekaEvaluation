package com.milestone2.prediction;

import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Builds probability-ranked predictions enriched with size and positivity information.
 */
public class RankedPredictionFactory {
    /**
     * Builds ranked predictions aligned with the test instances and sorts them by descending probability.
     *
     * @param test                test dataset aligned with the prediction list
     * @param predictions         recorded Weka predictions
     * @param positiveClassIndex  positive class index
     * @param sizeAttribute       attribute used as inspection cost
     * @return ranked predictions sorted by descending probability
     */
    public List<RankedPrediction> create(Instances test,
                                         List<Prediction> predictions,
                                         int positiveClassIndex,
                                         Attribute sizeAttribute) {
        List<RankedPrediction> rankedPredictions = new ArrayList<>(predictions.size());
        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction prediction = (NominalPrediction) predictions.get(i);
            rankedPredictions.add(new RankedPrediction(
                    prediction.distribution()[positiveClassIndex],
                    (int) test.instance(i).value(sizeAttribute),
                    (int) prediction.actual() == positiveClassIndex
            ));
        }
        rankedPredictions.sort(Comparator.comparingDouble(RankedPrediction::getProbability).reversed());
        return rankedPredictions;
    }
}

