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

    public ClassifierDefinition getClassifierDefinition() {
        return classifierDefinition;
    }

    public double getKappa() {
        return kappa;
    }

    public double getAuc() {
        return auc;
    }

    public String getReason() {
        return reason;
    }
}

