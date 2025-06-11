package com.milestone2;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instances;

import java.util.*;

public class ModelEvaluator {

    /**
     * Evaluates the classifier with 10×10-fold CV and returns
     * the average of the required metrics.
     */
    public Map<String, Double> evaluate(Classifier cls,
                                        Instances data,
                                        int folds) throws Exception {

        final int runs = 10;         // external repetitions
        final int pos  = 1;          // buggy class index

        double acc = 0;
        double totalPrecision = 0;
        double rec = 0;
        double f1 = 0;
        double kap = 0;
        double auc = 0;
        double np20 = 0;

        for (int run = 0; run < runs; run++) {
            Evaluation ev = new Evaluation(data);
            ev.crossValidateModel(cls, data, folds, new Random(run));

            acc  += (1 - ev.errorRate()) * 100;
            totalPrecision += ev.precision(pos);
            rec  += ev.recall(pos);
            f1   += ev.fMeasure(pos);
            kap  += ev.kappa();
            auc  += ev.areaUnderROC(pos);
            np20 += npOfB20(ev, data);
        }

        Map<String, Double> m = new HashMap<>();
        m.put("Accuracy",  acc  / runs);
        m.put("Precision", totalPrecision / runs);
        m.put("Recall",    rec  / runs);
        m.put("F1",        f1   / runs);
        m.put("Kappa",     kap  / runs);
        m.put("AUC",       auc  / runs);
        m.put("NPofB20",   np20 / runs);
        return m;
    }

    /** Effort-aware metric: Normalised PofB@20 (NPofB20). */
    private double npOfB20(Evaluation ev, Instances data) {
        List<Prediction> predictionsList = ev.predictions();
        List<Double> probs = new ArrayList<>(predictionsList.size());
        List<Integer> locs  = new ArrayList<>(predictionsList.size());

        for (int i = 0; i < predictionsList.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictionsList.get(i);
            probs.add(np.distribution()[1]);                          // buggy prob.
            int loc = (int) data.instance(i)
                    .value(data.attribute("LOC").index());
            locs.add(loc);
        }

        int totalLOC = locs.stream().mapToInt(Integer::intValue).sum();
        int totalBug = (int) predictionsList.stream()
                .filter(p -> p.actual() == 1)
                .count();

        // ranking by decreasing probability
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < probs.size(); i++) order.add(i);
        order.sort((a, b) -> Double.compare(probs.get(b), probs.get(a)));

        int budget   = (int) (0.20 * totalLOC);
        int locCum   = 0;
        int bugFound = 0;

        for (int i : order) {
            if (locCum + locs.get(i) > budget) break;
            locCum += locs.get(i);
            if (predictionsList.get(i).actual() == 1) bugFound++;
        }
        return totalBug == 0 ? 0.0 : (double) bugFound / totalBug;
    }
}
