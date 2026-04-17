package com.milestone2.summary;

import com.milestone2.classifier.OverallClassifierWinner;
import com.milestone2.metric.MetricWinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dataset-level milestone summary used for CSV export and logging.
 */
public class Milestone2Summary {
    private final List<MetricWinner> metricWinners;
    private final OverallClassifierWinner overallWinner;

    public Milestone2Summary(List<MetricWinner> metricWinners,
                             OverallClassifierWinner overallWinner) {
        this.metricWinners = Collections.unmodifiableList(new ArrayList<>(metricWinners));
        this.overallWinner = overallWinner;
    }

    /**
     * Returns the winners selected independently for each metric.
     *
     * @return immutable metric winners list
     */
    public List<MetricWinner> getMetricWinners() {
        return metricWinners;
    }

    /**
     * Returns the overall milestone winner selected with the Kappa/AUC rule.
     *
     * @return overall winner, or {@code null} when unavailable
     */
    public OverallClassifierWinner getOverallWinner() {
        return overallWinner;
    }
}

