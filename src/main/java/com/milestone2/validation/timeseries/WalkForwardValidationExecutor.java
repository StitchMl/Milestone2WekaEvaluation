package com.milestone2.validation.timeseries;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.AnalysisExecution;
import com.milestone2.fold.FoldContext;
import com.milestone2.fold.FoldResultProducer;
import com.milestone2.fold.PerFoldResult;
import com.milestone2.validation.ValidationExecutor;
import com.milestone2.validation.ValidationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes deterministic walk-forward validation over ordered temporal periods.
 */
public class WalkForwardValidationExecutor implements ValidationExecutor {
    private static final Logger log = LoggerFactory.getLogger(WalkForwardValidationExecutor.class);

    private final TemporalDatasetPartitioner temporalDatasetPartitioner;

    public WalkForwardValidationExecutor() {
        this(new TemporalDatasetPartitioner());
    }

    WalkForwardValidationExecutor(TemporalDatasetPartitioner temporalDatasetPartitioner) {
        this.temporalDatasetPartitioner = temporalDatasetPartitioner;
    }

    /**
     * Returns the validation strategy handled by this executor.
     *
     * @return {@link ValidationStrategy#WALK_FORWARD}
     */
    @Override
    public ValidationStrategy supportedStrategy() {
        return ValidationStrategy.WALK_FORWARD;
    }

    /**
     * Executes walk-forward validation over temporal buckets and evaluates every generated window in order.
     *
     * @param data     dataset to evaluate
     * @param config   immutable analysis configuration
     * @param producer fold evaluator callback
     * @return collected walk-forward results
     * @throws Exception when temporal partitioning or split evaluation fails
     */
    @Override
    public List<PerFoldResult> execute(Instances data,
                                       AnalysisConfig config,
                                       FoldResultProducer producer) throws Exception {
        AnalysisExecution execution = config.getExecution();
        List<TemporalBucket> buckets =
                temporalDatasetPartitioner.partition(data, execution.getTemporalAttributeName());
        int minimumTrainingPeriods = execution.getMinimumTrainingPeriods();
        validateMinimumTrainingPeriods(data, minimumTrainingPeriods, buckets.size());

        List<WalkForwardWindow> windows = buildWindows(buckets, minimumTrainingPeriods);
        List<PerFoldResult> results = new ArrayList<>(windows.size());

        log.info("Running walk-forward validation with {} temporal periods, {} windows and attribute '{}'",
                buckets.size(),
                windows.size(),
                execution.getTemporalAttributeName());

        for (WalkForwardWindow window : windows) {
            FoldContext context = FoldContext.walkForward(
                    window.getFoldIndex(),
                    window.getTrainingWindowLabel(),
                    window.getTestWindowLabel(),
                    window.getTrainingData().numInstances(),
                    window.getTestData().numInstances()
            );
            results.add(producer.produce(window.getTrainingData(), window.getTestData(), context));
        }

        return results;
    }

    /**
     * Verifies that the dataset exposes enough temporal periods for the requested minimum training window.
     *
     * @param data                   dataset being evaluated
     * @param minimumTrainingPeriods configured minimum training periods
     * @param availablePeriods       number of temporal buckets found in the dataset
     */
    private void validateMinimumTrainingPeriods(Instances data,
                                                int minimumTrainingPeriods,
                                                int availablePeriods) {
        if (minimumTrainingPeriods < 1) {
            throw new IllegalArgumentException("Walk-forward validation requires at least one training period");
        }
        if (availablePeriods <= minimumTrainingPeriods) {
            throw new IllegalArgumentException(
                    "Dataset '" + data.relationName() + "' has only " + availablePeriods
                            + " temporal periods but walk-forward validation requires more than "
                            + minimumTrainingPeriods
            );
        }
    }

    /**
     * Builds every walk-forward window by progressively extending the training history.
     *
     * @param buckets                 ordered temporal buckets
     * @param minimumTrainingPeriods  minimum number of periods required before testing
     * @return ordered walk-forward windows
     */
    private List<WalkForwardWindow> buildWindows(List<TemporalBucket> buckets, int minimumTrainingPeriods) {
        List<WalkForwardWindow> windows = new ArrayList<>(buckets.size() - minimumTrainingPeriods);
        for (int testBucketIndex = minimumTrainingPeriods; testBucketIndex < buckets.size(); testBucketIndex++) {
            windows.add(buildWindow(buckets, minimumTrainingPeriods, testBucketIndex));
        }
        return windows;
    }

    /**
     * Builds one walk-forward window made of all buckets up to the test period and the next future bucket.
     *
     * @param buckets                ordered temporal buckets
     * @param minimumTrainingPeriods configured minimum training periods
     * @param testBucketIndex        index of the bucket used for testing
     * @return walk-forward window
     */
    private WalkForwardWindow buildWindow(List<TemporalBucket> buckets,
                                          int minimumTrainingPeriods,
                                          int testBucketIndex) {
        Instances trainingData = new Instances(buckets.get(0).getInstances(), 0);
        for (int bucketIndex = 0; bucketIndex < testBucketIndex; bucketIndex++) {
            appendBucket(trainingData, buckets.get(bucketIndex).getInstances());
        }

        TemporalBucket testBucket = buckets.get(testBucketIndex);
        Instances testData = new Instances(testBucket.getInstances());
        return new WalkForwardWindow(
                testBucketIndex - minimumTrainingPeriods,
                trainingWindowLabel(buckets, testBucketIndex - 1),
                testBucket.getLabel(),
                trainingData,
                testData
        );
    }

    /**
     * Appends a deep copy of all instances from one bucket into the target dataset.
     *
     * @param target destination dataset
     * @param source source bucket instances
     */
    private void appendBucket(Instances target, Instances source) {
        for (Instance instance : source) {
            target.add((Instance) instance.copy());
        }
    }

    /**
     * Builds the textual label that summarizes the first and last period included in the training window.
     *
     * @param buckets                ordered temporal buckets
     * @param lastTrainingBucketIndex last bucket index included in training
     * @return training window label
     */
    private String trainingWindowLabel(List<TemporalBucket> buckets, int lastTrainingBucketIndex) {
        String first = buckets.get(0).getLabel();
        String last = buckets.get(lastTrainingBucketIndex).getLabel();
        if (first.equals(last)) {
            return first;
        }
        return first + ".." + last;
    }
}
