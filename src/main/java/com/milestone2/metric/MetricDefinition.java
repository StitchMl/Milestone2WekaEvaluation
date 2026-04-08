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

    public String getDisplayName() {
        return displayName;
    }

    public double extract(Metrics metrics) {
        return metrics.get(this);
    }

    public double normalizeForChart(double value) {
        return percentageBased ? value / 100.0 : value;
    }
}

