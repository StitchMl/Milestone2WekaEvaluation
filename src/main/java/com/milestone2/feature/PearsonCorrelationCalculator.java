package com.milestone2.feature;

/**
 * Small utility for Pearson correlation on aggregated sums.
 */
public class PearsonCorrelationCalculator {
    /**
     * Computes Pearson correlation from pre-aggregated sums without materializing the original vectors.
     *
     * @param count  number of paired observations
     * @param sumX   sum of X values
     * @param sumY   sum of Y values
     * @param sumXX  sum of squared X values
     * @param sumYY  sum of squared Y values
     * @param sumXY  sum of X*Y products
     * @return Pearson correlation, or {@code 0.0} when it cannot be computed reliably
     */
    public double calculate(int count,
                            double sumX,
                            double sumY,
                            double sumXX,
                            double sumYY,
                            double sumXY) {
        if (count < 2) {
            return 0.0;
        }

        double numerator = (count * sumXY) - (sumX * sumY);
        double denominator = Math.sqrt(
                ((count * sumXX) - (sumX * sumX))
                        * ((count * sumYY) - (sumY * sumY))
        );
        if (Double.compare(denominator, 0.0) == 0) {
            return 0.0;
        }
        return numerator / denominator;
    }
}

