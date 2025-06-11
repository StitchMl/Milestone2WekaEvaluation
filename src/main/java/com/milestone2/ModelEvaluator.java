package com.milestone2;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.AbstractClassifier;

import java.util.*;

/**
 * ModelEvaluator extended to support:
 * - 10×10-fold manual CV via trainCV/testCV
 * - collection of metrics for run/fold
 * - aggregation (averaging) of metrics
 */
public class ModelEvaluator {

    private static final String ACCURACY = "Accuracy";
    private static final String PRECISION = "Precision";
    private static final String RECALL = "Recall";
    private static final String F1 = "F1";
    private static final String KAPPA = "Kappa";
    private static final String AUC = "AUC";
    private static final String NPOFB20 = "NPofB20";

    /**
     * Utility class, no instantiation.
     */
    private ModelEvaluator() {
        // Utility class, no instantiation
    }

    /**
     * Performs 10 manual 10-fold CV runs:
     * - randomises and stratifies (if nominal) the dataset
     * - for each fold uses trainCV/testCV to get train/test set
     * - for each fold train a classifier clone and evaluate with
     * evaluateModelOnceAndRecordPrediction
     * - calculates NPofB20 on the test set
     */
    public static List<PerFoldResult> evaluateWithFolds(
            FilteredClassifier cls,
            Instances data,
            int folds) throws Exception {

        List<PerFoldResult> results = new ArrayList<>();
        // 10 external runs
        for (int run = 0; run < 10; run++) {
            // copy and randomize
            Instances randData = new Instances(data);
            Random rnd = new Random(run);
            randData.randomize(rnd);
            if (randData.classAttribute().isNominal()) {
                randData.stratify(folds);
            }

            // 10 internal folds
            for (int f = 0; f < folds; f++) {
                // train/test split
                Instances train = randData.trainCV(folds, f, rnd);
                Instances test  = randData.testCV(folds, f);

                // deep copy of the classifier for each fold
                FilteredClassifier copy =
                        (FilteredClassifier) AbstractClassifier.makeCopy(cls);
                // build on the train set
                copy.buildClassifier(train);

                // Evaluation per fold
                Evaluation ev = new Evaluation(train);
                // records predictions to calculate AUC and NPofB20
                for (int i = 0; i < test.numInstances(); i++) {
                    Instance inst = test.instance(i);
                    double[] dist = copy.distributionForInstance(inst);
                    ev.evaluateModelOnceAndRecordPrediction(dist, inst);
                }

                // standard metrics
                double accuracy  = (1 - ev.errorRate()) * 100;
                double precision = ev.precision(1);
                double recall    = ev.recall(1);
                double f1        = ev.fMeasure(1);
                double kappa     = ev.kappa();
                double auc       = ev.areaUnderROC(1);
                // NPofB20 defined as a proportion of bugs found out of 20% LOC
                double np20      = computeNPofB20(ev, test);

                Metrics m = new Metrics(
                        accuracy,
                        precision,
                        recall,
                        f1,
                        kappa,
                        auc,
                        np20
                );

                results.add(new PerFoldResult(
                        cls.getClassifier().getClass().getSimpleName(),
                        run,
                        f,
                        m
                ));
            }
        }
        return results;
    }

    /**
     * Average (aggregate) metrics over all PerFoldResults.
     */
    public static Map<String, Double> aggregate(List<PerFoldResult> list) {
        Map<String, Double> sum = new HashMap<>();
        // initialise
        Arrays.asList(ACCURACY,PRECISION,RECALL,F1,KAPPA,AUC,NPOFB20)
                .forEach(m -> sum.put(m, 0.0));

        for (PerFoldResult r : list) {
            Metrics m = r.metrics;
            sum.put(ACCURACY,  sum.get(ACCURACY)  + m.accuracy);
            sum.put(PRECISION, sum.get(PRECISION) + m.precision);
            sum.put(RECALL,    sum.get(RECALL)    + m.recall);
            sum.put(F1,        sum.get(F1)        + m.f1);
            sum.put(KAPPA,     sum.get(KAPPA)     + m.kappa);
            sum.put(AUC,       sum.get(AUC)       + m.auc);
            sum.put(NPOFB20,   sum.get(NPOFB20)   + m.npOfb20);
        }

        // division by number of results
        int n = list.size();
        Map<String, Double> avg = new HashMap<>();
        sum.forEach((k, v) -> avg.put(k, v / n));
        return avg;
    }

    /**
     * Calculates NPofB20 on a single Evaluation and its test set.
     */
    private static double computeNPofB20(Evaluation ev, Instances test) {
        List<Prediction> predictionsList = ev.predictions();
        List<Double> probs = new ArrayList<>(predictionsList.size());
        List<Integer> locs  = new ArrayList<>(predictionsList.size());

        // extract buggy and LOC probabilities from each test instance
        for (int i = 0; i < predictionsList.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictionsList.get(i);
            probs.add(np.distribution()[1]);
            int loc = (int) test.instance(i)
                    .value(test.attribute("LOC").index());
            locs.add(loc);
        }

        int totalLOC = locs.stream().mapToInt(Integer::intValue).sum();
        long totalBug = predictionsList.stream()
                .filter(p -> p.actual() == 1)
                .count();

        // ranking by decreasing probability
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < probs.size(); i++) order.add(i);
        order.sort((a, b) -> Double.compare(probs.get(b), probs.get(a)));

        int budget = (int) (0.20 * totalLOC);
        int locCum = 0;
        int bugFound = 0;
        for (int i : order) {
            if (locCum + locs.get(i) > budget) break;
            locCum += locs.get(i);
            if (predictionsList.get(i).actual() == 1) bugFound++;
        }
        return totalBug == 0 ? 0.0 : (double) bugFound / totalBug;
    }
}