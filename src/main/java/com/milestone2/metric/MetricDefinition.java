package com.milestone2.metric;

/**
 * Central definition of supported evaluation metrics and their presentation rules.
 */
public enum MetricDefinition {
    ACCURACY("Accuracy", true),
    PRECISION("Precision", false),
    RECALL("Recall", false),
    F1("F1", false),
    KAPPA("Kappa", false),
    AUC("AUC", false),
    NPOFB20("NPofB20", false);

    private final String displayName;
    private final boolean percentageBased;

    MetricDefinition(String displayName, boolean percentageBased) {
        this.displayName = displayName;
        this.percentageBased = percentageBased;
    }

    /**
     * Returns the human-readable metric label used in reports and charts.
     *
     * @return metric display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Extracts the metric value from the metric bundle.
     *
     * @param metrics metric bundle
     * @return extracted metric value
     */
    public double extract(Metrics metrics) {
        return metrics.get(this);
    }

    /**
     * Normalizes the metric for chart rendering, converting percentage-based values into the [0,1] range.
     *
     * @param value raw metric value
     * @return normalized chart value
     */
    public double normalizeForChart(double value) {
        return percentageBased ? value / 100.0 : value;
    }
}

