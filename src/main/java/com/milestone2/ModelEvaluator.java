package com.milestone2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.AbstractClassifier;

import java.util.*;
import java.util.concurrent.*;

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
    private static final Logger log = LoggerFactory.getLogger(ModelEvaluator.class);

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

        log.info("=== Starting 10×{}-fold cross-validation ===", folds);

        // 1) Pre-generates all splits 📂
        class Split {
            final int run;
            final int fold;
            final Instances train;
            final Instances test;
            Split(int run, int fold, Instances train, Instances test) {
                this.run = run; this.fold = fold;
                this.train = train; this.test = test;
            }
        }
        List<Split> splits = new ArrayList<>(10 * folds);
        for (int run = 0; run < 10; run++) {
            Instances rand = new Instances(data);
            rand.randomize(new Random(run));
            if (rand.classAttribute().isNominal()) rand.stratify(folds);
            for (int f = 0; f < folds; f++) {
                splits.add(new Split(
                        run, f,
                        rand.trainCV(folds, f, new Random(run)),
                        rand.testCV(folds, f)
                ));
            }
        }
        log.info("Precomputed {} train/test splits", splits.size());

        // 2) Pool sized to cores, not to tasks 🖥️
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(cores);
        CompletionService<PerFoldResult> ecs =
                new ExecutorCompletionService<>(pool);
        log.info("ExecutorService creato con {} thread", cores);

        // 3) Submit tasks using only the necessary split
        for (Split s : splits) {
            ecs.submit(() -> {
                // copy of the classifier
                FilteredClassifier copy =
                        (FilteredClassifier) AbstractClassifier.makeCopy(cls);
                // build and evaluation
                copy.buildClassifier(s.train);
                Evaluation ev = new Evaluation(s.train);
                for (Instance inst : s.test) {
                    ev.evaluateModelOnceAndRecordPrediction(
                            copy.distributionForInstance(inst), inst);
                }
                // metric calculation
                Metrics m = new Metrics(
                        (1 - ev.errorRate()) * 100,
                        ev.precision(1),
                        ev.recall(1),
                        ev.fMeasure(1),
                        ev.kappa(),
                        ev.areaUnderROC(1),
                        computeNPofB20(ev, s.test)
                );
                return new PerFoldResult(
                        cls.getClassifier().getClass().getSimpleName(),
                        s.run, s.fold, m
                );
            });
        }
        log.info("Submitted {} tasks to pool", splits.size());

        // 4) Shutdown and wait (without OOM) 🚦
        pool.shutdown();
        if (!pool.awaitTermination(10, TimeUnit.MINUTES)) {
            log.warn("Timeout in CV, but we do not interrupt threads to avoid OOM");
        } else {
            log.info("All tasks completed");
        }

        // 5) Non-blocking result collection 🎯
        List<PerFoldResult> results = new ArrayList<>(splits.size());
        for (int i = 0; i < splits.size(); i++) {
            Future<PerFoldResult> f = ecs.take();
            results.add(f.get());
        }
        log.info("Collected {} overall results", results.size());

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