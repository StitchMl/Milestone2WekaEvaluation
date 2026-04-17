package com.milestone2.metric;

import com.milestone2.prediction.RankedPrediction;
import com.milestone2.prediction.RankedPredictionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.List;

/**
 * Computes the NPofB20 budget-based metric.
 */
public class NPofB20Calculator {
    private static final Logger log = LoggerFactory.getLogger(NPofB20Calculator.class);

    private final RankedPredictionFactory rankedPredictionFactory;
    private final BudgetedDetectionRateCalculator detectionRateCalculator;

    public NPofB20Calculator() {
        this(new RankedPredictionFactory(), new BudgetedDetectionRateCalculator());
    }

    NPofB20Calculator(RankedPredictionFactory rankedPredictionFactory,
                      BudgetedDetectionRateCalculator detectionRateCalculator) {
        this.rankedPredictionFactory = rankedPredictionFactory;
        this.detectionRateCalculator = detectionRateCalculator;
    }

    /**
     * Computes NPofB20 from recorded predictions and the configured size attribute.
     *
     * @param evaluation          Weka evaluation containing recorded predictions
     * @param test                test dataset aligned with the recorded predictions
     * @param positiveClassIndex  positive class index
     * @param sizeAttributeName   name of the attribute used as inspection cost
     * @return NPofB20 value, or {@link Double#NaN} when the size attribute is missing
     */
    public double compute(Evaluation evaluation,
                          Instances test,
                          int positiveClassIndex,
                          String sizeAttributeName) {
        Attribute sizeAttribute = test.attribute(sizeAttributeName);
        if (sizeAttribute == null) {
            log.warn("Size attribute '{}' not found in '{}'; NPofB20 will be NaN",
                    sizeAttributeName,
                    test.relationName());
            return Double.NaN;
        }

        List<Prediction> predictions = evaluation.predictions();
        List<RankedPrediction> rankedPredictions =
                rankedPredictionFactory.create(test, predictions, positiveClassIndex, sizeAttribute);
        return detectionRateCalculator.compute(rankedPredictions);
    }
}

