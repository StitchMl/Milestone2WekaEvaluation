package com.milestone2.validationStrategy;

import com.milestone2.startupUtility.RunConfig;
import com.milestone2.foldMetadata.FoldContext;
import com.milestone2.foldMetadata.FoldResultProducer;
import com.milestone2.foldMetadata.FoldResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.Collections;
import java.util.List;

/**
 * Ordered 80/20 holdout: the first 80% of rows (as they appear in the dataset,
 * ordered by the temporal attribute) form the training set; the remaining 20%
 * form the test set. No shuffling is applied.
 */
public class HoldoutExecutor implements ValidationExecutor {
 private static final Logger log = LoggerFactory.getLogger(HoldoutExecutor.class);
 private static final double TRAIN_RATIO = 0.8;

 @Override
 public ValidationStrategy supportedStrategy() {
 return ValidationStrategy.ORDERED_HOLDOUT;
 }

 @Override
 public List<FoldResult> execute(Instances data,
 RunConfig config,
 FoldResultProducer producer) throws Exception {
 int total = data.numInstances();
 int trainSize = (int) Math.round(total * TRAIN_RATIO);
 int testSize = total - trainSize;

 if (trainSize == 0 || testSize == 0) {
 throw new IllegalArgumentException(
 "Dataset too small for ordered holdout: " + total + " instances");
 }

 Instances train = new Instances(data, 0, trainSize);
 Instances test = new Instances(data, trainSize, testSize);

 log.info("Running ordered 80/20 holdout: {} train, {} test instances", trainSize, testSize);

 FoldContext context = FoldContext.orderedHoldout(trainSize, testSize);
 FoldResult result = producer.produce(train, test, context);
 return Collections.singletonList(result);
 }
}
