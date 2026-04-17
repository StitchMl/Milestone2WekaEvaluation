package com.milestone2.classifier;

/**
 * Best overall classifier for the dataset, using the milestone ranking rule.
 */
public class OverallClassifierWinner {
    private final ClassifierDefinition classifierDefinition;
    private final double kappa;
    private final double auc;
    private final String reason;

    public OverallClassifierWinner(ClassifierDefinition classifierDefinition,
                                   double kappa,
                                   double auc,
                                   String reason) {
        this.classifierDefinition = classifierDefinition;
        this.kappa = kappa;
        this.auc = auc;
        this.reason = reason;
    }

    /**
     * Returns the classifier selected as overall milestone winner.
     *
     * @return winning classifier definition
     */
    public ClassifierDefinition getClassifierDefinition() {
        return classifierDefinition;
    }

    /**
     * Returns the aggregate Kappa achieved by the winning classifier.
     *
     * @return winning Kappa value
     */
    public double getKappa() {
        return kappa;
    }

    /**
     * Returns the aggregate AUC used as tie-breaker for the winning classifier.
     *
     * @return winning AUC value
     */
    public double getAuc() {
        return auc;
    }

    /**
     * Returns the textual explanation of the winner selection rule.
     *
     * @return winner selection reason
     */
    public String getReason() {
        return reason;
    }
}

