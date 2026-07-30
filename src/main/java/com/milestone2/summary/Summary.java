package com.milestone2.summary;

import com.milestone2.classifier.OverallWinner;
import com.milestone2.metric.MetricWinner;

import java.util.List;

/**
 * Dataset-level milestone summary used for CSV export and logging.
 */
public class Summary {
 private final List<MetricWinner> metricWinners;
 private final OverallWinner overallWinner;

 public Summary(List<MetricWinner> metricWinners,
 OverallWinner overallWinner) {
 this.metricWinners = List.copyOf(metricWinners);
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
 public OverallWinner getOverallWinner() {
 return overallWinner;
 }
}

