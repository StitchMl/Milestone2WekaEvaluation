package com.milestone2;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.classifier.ClassifierDefinition;
import com.milestone2.classifier.ClassifierEvaluationReport;
import com.milestone2.metric.MetricDefinition;
import com.milestone2.whatif.WhatIfClassifierSelection;
import com.milestone2.whatif.WhatIfClassifierSelector;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhatIfClassifierSelectorTest {

    @Test
    void selectPrefersHigherKappaBeforeAuc() {
        WhatIfClassifierSelection selection = new WhatIfClassifierSelector().select(
                AnalysisConfig.fromArgs(new String[0]).getWhatIfOptions(),
                List.of(
                        report("RF", "Random Forest", 0.30, 0.90),
                        report("NB", "Naive Bayes", 0.40, 0.60)
                )
        );

        assertEquals("NB", selection.getDefinition().getId());
    }

    @Test
    void selectUsesExplicitClassifierOverride() {
        WhatIfClassifierSelection selection = new WhatIfClassifierSelector().select(
                AnalysisConfig.fromArgs(new String[]{"--whatif-classifier=RF"}).getWhatIfOptions(),
                List.of(
                        report("RF", "Random Forest", 0.30, 0.90),
                        report("NB", "Naive Bayes", 0.40, 0.60)
                )
        );

        assertEquals("RF", selection.getDefinition().getId());
    }

    private ClassifierEvaluationReport report(String id, String displayName, double kappa, double auc) {
        Map<MetricDefinition, Double> metrics = new EnumMap<>(MetricDefinition.class);
        for (MetricDefinition metric : MetricDefinition.values()) {
            metrics.put(metric, 0.0);
        }
        metrics.put(MetricDefinition.KAPPA, kappa);
        metrics.put(MetricDefinition.AUC, auc);
        return new ClassifierEvaluationReport(
                new ClassifierDefinition(id, displayName, "weka.classifiers.Dummy", ""),
                metrics,
                List.of()
        );
    }
}

