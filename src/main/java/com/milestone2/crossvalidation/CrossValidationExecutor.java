package com.milestone2.crossvalidation;

import com.milestone2.fold.FoldResultProducer;
import com.milestone2.fold.PerFoldResult;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Executes deterministic cross-validation folds in parallel.
 */
public class CrossValidationExecutor {
    private static final Logger log = LoggerFactory.getLogger(CrossValidationExecutor.class);

    private final CrossValidationParallelismResolver parallelismResolver;

    public CrossValidationExecutor() {
        this(new CrossValidationParallelismResolver());
    }

    CrossValidationExecutor(CrossValidationParallelismResolver parallelismResolver) {
        this.parallelismResolver = parallelismResolver;
    }

    public List<PerFoldResult> execute(Instances data,
                                       AnalysisConfig config,
                                       FoldResultProducer producer) throws Exception {
        AnalysisExecution execution = config.getExecution();
        int workerCount = parallelismResolver.resolve(execution);
        ExecutorService executorService = Executors.newFixedThreadPool(workerCount);
        CompletionService<PerFoldResult> completionService = new ExecutorCompletionService<>(executorService);
        List<PerFoldResult> results = new ArrayList<>(execution.getRuns() * execution.getFolds());
        log.info("Running {}x{}-fold cross-validation with {} fold workers",
                execution.getRuns(),
                execution.getFolds(),
                workerCount);

        try {
            for (int run = 0; run < execution.getRuns(); run++) {
                submitRunTasks(data, config, producer, completionService, run);
                collectRunResults(execution.getFolds(), completionService, results);
            }
        } finally {
            shutdownExecutor(executorService);
        }

        return results;
    }

    private void submitRunTasks(Instances data,
                                AnalysisConfig config,
                                FoldResultProducer producer,
                                CompletionService<PerFoldResult> completionService,
                                int run) {
        AnalysisExecution execution = config.getExecution();
        Instances randomized = new Instances(data);
        randomized.randomize(new Random(execution.getSeed() + run));
        if (randomized.classAttribute().isNominal()) {
            randomized.stratify(execution.getFolds());
        }

        for (int fold = 0; fold < execution.getFolds(); fold++) {
            final int runIndex = run;
            final int foldIndex = fold;
            final Instances train = new Instances(randomized.trainCV(execution.getFolds(), fold));
            final Instances test = new Instances(randomized.testCV(execution.getFolds(), fold));

            completionService.submit(() -> producer.produce(train, test, runIndex, foldIndex));
        }
    }

    private void collectRunResults(int folds,
                                   CompletionService<PerFoldResult> completionService,
                                   List<PerFoldResult> results) throws Exception {
        for (int i = 0; i < folds; i++) {
            results.add(completionService.take().get());
        }
    }

    private void shutdownExecutor(ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
            log.warn("Forcing cross-validation worker shutdown after timeout");
            executorService.shutdownNow();
        }
    }
}

