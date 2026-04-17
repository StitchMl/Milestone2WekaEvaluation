package com.milestone2.crossvalidation;

import com.milestone2.fold.FoldResultProducer;
import com.milestone2.fold.FoldContext;
import com.milestone2.fold.PerFoldResult;
import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.validation.ValidationExecutor;
import com.milestone2.validation.ValidationStrategy;
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
public class CrossValidationExecutor implements ValidationExecutor {
    private static final Logger log = LoggerFactory.getLogger(CrossValidationExecutor.class);

    private final CrossValidationParallelismResolver parallelismResolver;

    public CrossValidationExecutor() {
        this(new CrossValidationParallelismResolver());
    }

    CrossValidationExecutor(CrossValidationParallelismResolver parallelismResolver) {
        this.parallelismResolver = parallelismResolver;
    }

    /**
     * Returns the validation strategy handled by this executor.
     *
     * @return {@link ValidationStrategy#CROSS_VALIDATION}
     */
    @Override
    public ValidationStrategy supportedStrategy() {
        return ValidationStrategy.CROSS_VALIDATION;
    }

    /**
     * Executes repeated stratified cross-validation and evaluates each fold in parallel.
     *
     * @param data     dataset to evaluate
     * @param config   immutable analysis configuration
     * @param producer fold evaluator callback
     * @return collected per-fold results
     * @throws Exception when fold submission, evaluation or collection fails
     */
    @Override
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

    /**
     * Creates and submits every fold evaluation task for one repeated cross-validation run.
     *
     * @param data              source dataset
     * @param config            immutable analysis configuration
     * @param producer          fold evaluator callback
     * @param completionService completion queue used to collect results
     * @param run               repeated-run index
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
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
            final FoldContext context =
                    FoldContext.crossValidation(runIndex, foldIndex, train.numInstances(), test.numInstances());

            completionService.submit(() -> producer.produce(train, test, context));
        }
    }

    /**
     * Waits for all folds of one run to complete and appends their results.
     *
     * @param folds             number of folds to collect
     * @param completionService completion queue used to retrieve results
     * @param results           destination list for collected fold results
     * @throws Exception when any fold evaluation fails
     */
    private void collectRunResults(int folds,
                                   CompletionService<PerFoldResult> completionService,
                                   List<PerFoldResult> results) throws Exception {
        for (int i = 0; i < folds; i++) {
            results.add(completionService.take().get());
        }
    }

    /**
     * Gracefully shuts down the fold worker pool and forces termination after a timeout.
     *
     * @param executorService executor to shut down
     * @throws InterruptedException when awaiting termination is interrupted
     */
    private void shutdownExecutor(ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
            log.warn("Forcing cross-validation worker shutdown after timeout");
            executorService.shutdownNow();
        }
    }
}
