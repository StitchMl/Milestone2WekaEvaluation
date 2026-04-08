package com.milestone2.feature;

/**
 * Small utility for Pearson correlation on aggregated sums.
 */
public class PearsonCorrelationCalculator {
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


