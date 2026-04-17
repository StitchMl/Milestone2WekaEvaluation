package com.milestone2.metric;

import com.milestone2.prediction.RankedPrediction;

import java.util.List;

/**
 * Computes the share of positives found within a 20% inspection budget.
 */
public class BudgetedDetectionRateCalculator {
    private static final double INSPECTION_BUDGET_RATIO = 0.20;

    /**
     * Computes how many positive entities are found before exhausting the inspection budget.
     *
     * @param rankedPredictions predictions sorted by descending inspection priority
     * @return fraction of positives found within the budget
     */
    public double compute(List<RankedPrediction> rankedPredictions) {
        int totalSize = rankedPredictions.stream().mapToInt(RankedPrediction::getSize).sum();
        long totalPositives = rankedPredictions.stream().filter(RankedPrediction::isPositive).count();

        if (totalPositives == 0L || totalSize == 0) {
            return 0.0;
        }

        int budget = (int) (totalSize * INSPECTION_BUDGET_RATIO);
        int consumedSize = 0;
        int foundPositives = 0;

        for (RankedPrediction prediction : rankedPredictions) {
            if (consumedSize + prediction.getSize() > budget) {
                break;
            }
            consumedSize += prediction.getSize();
            if (prediction.isPositive()) {
                foundPositives++;
            }
        }

        return (double) foundPositives / totalPositives;
    }
}

