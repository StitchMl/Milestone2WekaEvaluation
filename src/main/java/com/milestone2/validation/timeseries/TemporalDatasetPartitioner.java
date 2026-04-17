package com.milestone2.validation.timeseries;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Groups a dataset into contiguous temporal buckets while preserving original order.
 */
public class TemporalDatasetPartitioner {

    /**
     * Splits the dataset into contiguous periods based on the configured temporal attribute.
     *
     * @param data                  dataset to partition
     * @param temporalAttributeName temporal attribute used to derive periods
     * @return ordered temporal buckets
     */
    public List<TemporalBucket> partition(Instances data, String temporalAttributeName) {
        Attribute temporalAttribute = resolveTemporalAttribute(data, temporalAttributeName);
        List<TemporalBucket> buckets = new ArrayList<>();
        Set<String> closedLabels = new HashSet<>();

        String currentLabel = null;
        Instances currentBucket = null;
        for (Instance instance : data) {
            if (instance.isMissing(temporalAttribute)) {
                throw new IllegalArgumentException(
                        "Dataset '" + data.relationName() + "' has missing temporal values in '"
                                + temporalAttributeName + "'"
                );
            }

            String periodLabel = toLabel(instance, temporalAttribute);
            if (!periodLabel.equals(currentLabel)) {
                if (closedLabels.contains(periodLabel)) {
                    throw new IllegalArgumentException(
                            "Dataset '" + data.relationName() + "' is not ordered by '"
                                    + temporalAttributeName + "': period '" + periodLabel
                                    + "' appears again after a later period"
                    );
                }
                if (currentLabel != null) {
                    closedLabels.add(currentLabel);
                }

                currentLabel = periodLabel;
                currentBucket = new Instances(data, 0);
                buckets.add(new TemporalBucket(periodLabel, currentBucket));
            }

            currentBucket.add((Instance) instance.copy());
        }

        if (buckets.size() < 2) {
            throw new IllegalArgumentException(
                    "Dataset '" + data.relationName() + "' must contain at least two temporal periods in '"
                            + temporalAttributeName + "' for walk-forward validation"
            );
        }
        return buckets;
    }

    /**
     * Resolves the configured temporal attribute from the dataset and validates its presence.
     *
     * @param data                  dataset to partition
     * @param temporalAttributeName temporal attribute name from configuration
     * @return resolved temporal attribute
     */
    private Attribute resolveTemporalAttribute(Instances data, String temporalAttributeName) {
        if (temporalAttributeName == null || temporalAttributeName.isBlank()) {
            throw new IllegalArgumentException("Walk-forward validation requires a temporal attribute name");
        }

        Attribute attribute = data.attribute(temporalAttributeName);
        if (attribute == null) {
            throw new IllegalArgumentException(
                    "Temporal attribute '" + temporalAttributeName + "' not found in dataset "
                            + data.relationName()
            );
        }
        return attribute;
    }

    /**
     * Converts one instance value of the temporal attribute into the stable bucket label used by the partitioner.
     *
     * @param instance          instance being processed
     * @param temporalAttribute temporal attribute
     * @return bucket label for the instance
     */
    private String toLabel(Instance instance, Attribute temporalAttribute) {
        if (temporalAttribute.isDate()) {
            return temporalAttribute.formatDate(instance.value(temporalAttribute));
        }
        if (temporalAttribute.isNumeric()) {
            double numericValue = instance.value(temporalAttribute);
            long asLong = (long) numericValue;
            if (Double.compare(numericValue, asLong) == 0) {
                return Long.toString(asLong);
            }
            return String.format(Locale.US, "%.6f", numericValue);
        }
        return instance.stringValue(temporalAttribute);
    }
}
