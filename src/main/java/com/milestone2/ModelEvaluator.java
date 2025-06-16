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

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

/**
 * Performs cross-validation (N_RUNS x N_FOLDS) and collects metrics.
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
     * Cannot be instantiated.
     */
    private ModelEvaluator() {
        // Utility class
    }

    /**
     * Runs Config.N_RUNS times a manual Config.N_FOLDS-fold CV:
     * - randomises and stratifies the dataset
     * - generates split train/test for each fold
     * - calculates metrics for each pair (run, fold)
     */
    public static List<PerFoldResult> evaluateWithFolds(
            FilteredClassifier cls,
            Instances data,
            int folds) throws Exception {

        log.info("=== Starting {}×{}-fold cross-validation ===", Config.N_RUNS, folds);

        // 1) Pre-generation of all train/test splits
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
        List<Split> splits = new ArrayList<>(Config.N_RUNS * folds);
        for (int run = 0; run < Config.N_RUNS; run++) {
            Instances rand = new Instances(data);
            rand.randomize(new SecureRandom());
            if (rand.classAttribute().isNominal()) rand.stratify(folds);
            for (int f = 0; f < folds; f++) {
                splits.add(new Split(
                        run, f,
                        rand.trainCV(folds, f, new SecureRandom()),
                        rand.testCV(folds, f)
                ));
            }
        }
        log.info("Precomputed {} train/test splits", splits.size());

        // 2) Creation of a thread pool
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(cores);
        CompletionService<PerFoldResult> ecs =
                new ExecutorCompletionService<>(pool);
        log.info("ExecutorService created with {} thread", cores);

        // 3) Submission of training/eval tasks
        for (Split s : splits) {
            ecs.submit(() -> {
                // copy of the classifier
                FilteredClassifier copy =
                        (FilteredClassifier) AbstractClassifier.makeCopy(cls);
                // trains and performs predictions
                copy.buildClassifier(s.train);
                Evaluation ev = new Evaluation(s.train);
                for (Instance inst : s.test) {
                    ev.evaluateModelOnceAndRecordPrediction(
                            copy.distributionForInstance(inst), inst);
                }
                // calculation of metrics
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

        // 4) Waiting for completion without forced interruption
        pool.shutdown();
        if (!pool.awaitTermination(10, TimeUnit.MINUTES)) {
            log.warn("Timeout in CV, we do not interrupt threads to avoid OOM");
        } else {
            log.info("All tasks completed");
        }

        // 5) Collecting results
        List<PerFoldResult> results = new ArrayList<>(splits.size());
        for (int i = 0; i < splits.size(); i++) {
            Future<PerFoldResult> f = ecs.take();
            results.add(f.get());
        }
        log.info("Collected {} overall results", results.size());

        return results;
    }

    /**
     * Aggregates average metrics over all repetitions (run/fold).
     */
    public static Map<String, Double> aggregate(List<PerFoldResult> list) {
        Map<String, Double> sum = new HashMap<>();
        // initialise
        Arrays.asList(ACCURACY, PRECISION, RECALL, F1, KAPPA, AUC, NPOFB20)
                .forEach(m -> sum.put(m, 0.0));

        for (PerFoldResult r : list) {
            Metrics m = r.getMetrics();
            sum.put(ACCURACY,  sum.get(ACCURACY)  + m.getAccuracy());
            sum.put(PRECISION, sum.get(PRECISION) + m.getPrecision());
            sum.put(RECALL,    sum.get(RECALL)    + m.getRecall());
            sum.put(F1,        sum.get(F1)        + m.getF1());
            sum.put(KAPPA,     sum.get(KAPPA)     + m.getKappa());
            sum.put(AUC,       sum.get(AUC)       + m.getAUC());
            sum.put(NPOFB20,   sum.get(NPOFB20)   + m.getNpOfb20());
        }

        // calculate the average
        int n = list.size();
        Map<String, Double> avg = new HashMap<>();
        sum.forEach((k, v) -> avg.put(k, v / n));
        return avg;
    }

    /**
     * Calculates NPofB20 given an Evaluation and its test set.
     */
    private static double computeNPofB20(Evaluation ev, Instances test) {
        List<Prediction> predictionsList = ev.predictions();
        List<Double> probs = new ArrayList<>(predictionsList.size());
        List<Integer> locs  = new ArrayList<>(predictionsList.size());

        // Extracts probability and LOC from each test instance
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

        // Sort by decreasing probability
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