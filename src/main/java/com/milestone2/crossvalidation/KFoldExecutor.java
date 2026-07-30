package com.milestone2.crossValidation;

import com.milestone2.foldMetadata.FoldResultProducer;
import com.milestone2.foldMetadata.FoldContext;
import com.milestone2.foldMetadata.FoldResult;
import com.milestone2.startupUtility.RunConfig;
import com.milestone2.startupUtility.ExecutionSettings;
import com.milestone2.validationStrategy.ValidationExecutor;
import com.milestone2.validationStrategy.ValidationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Executes deterministic cross-validation folds in parallel.
 */
public class KFoldExecutor implements ValidationExecutor {
 private static final Logger log = LoggerFactory.getLogger(KFoldExecutor.class);

 private final ParallelismResolver parallelismResolver;

 public KFoldExecutor() {
 this(new ParallelismResolver());
 }

 KFoldExecutor(ParallelismResolver parallelismResolver) {
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
 * @param data dataset to evaluate
 * @param config immutable analysis configuration
 * @param producer fold evaluator callback
 * @return collected per-fold results
 * @throws Exception when fold submission, evaluation or collection fails
 */
 @Override
 public List<FoldResult> execute(Instances data,
 RunConfig config,
 FoldResultProducer producer) throws Exception {
 ExecutionSettings execution = config.getExecution();
 int workerCount = parallelismResolver.resolve(execution);
 ExecutorService executorService = Executors.newFixedThreadPool(workerCount);
 CompletionService<FoldResult> completionService = new ExecutorCompletionService<>(executorService);
 List<FoldResult> results = new ArrayList<>(execution.getRuns() * execution.getFolds());
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
 * @param data source dataset
 * @param config immutable analysis configuration
 * @param producer fold evaluator callback
 * @param completionService completion queue used to collect results
 * @param run repeated-run index
 */
 // S2245: Random is used only to shuffle the dataset for reproducible cross-validation splits,
 // not for any security- or cryptography-sensitive purpose. SecureRandom would add overhead
 // with no benefit in this ML context.
 @SuppressWarnings({"UnnecessaryLocalVariable", "java:S2245"})
 private void submitRunTasks(Instances data,
 RunConfig config,
 FoldResultProducer producer,
 CompletionService<FoldResult> completionService,
 int run) {
 ExecutionSettings execution = config.getExecution();
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
 * @param folds number of folds to collect
 * @param completionService completion queue used to retrieve results
 * @param results destination list for collected fold results
 * @throws Exception when any fold evaluation fails
 */
 private void collectRunResults(int folds,
 CompletionService<FoldResult> completionService,
 List<FoldResult> results) throws InterruptedException, ExecutionException {
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
