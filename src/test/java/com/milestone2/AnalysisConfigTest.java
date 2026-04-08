package com.milestone2;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.analysis.Config;
import com.milestone2.analysis.AnalysisGranularity;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalysisConfigTest {

    @Test
    void fromArgsParsesGranularityAndClassifierSelection() {
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--data-dir=data/custom",
                "--output-dir=build/out",
                "--granularity=method",
                "--class-attribute=bug",
                "--positive-class=yes",
                "--size-attribute=LOC_METHOD",
                "--classifiers=RF,NB",
                "--runs=3",
                "--folds=4",
                "--seed=123",
                "--threads=2",
                "--smote=false",
                "--whatif=false",
                "--whatif-feature=NSmells",
                "--whatif-classifier=RF"
        });

        assertEquals(Paths.get("data/custom"), config.getPaths().getDataDir());
        assertEquals(Paths.get("build/out"), config.getPaths().getOutputDir());
        assertEquals(Paths.get("build/out/results.csv"), config.getPaths().getResultsCsv());
        assertEquals(Paths.get("build/out/fold_metrics.csv"), config.getPaths().getFoldCsv());
        assertEquals(Paths.get("build/out/milestone2_summary.csv"), config.getPaths().getMilestone2SummaryCsv());
        assertEquals(Paths.get("build/out/feature_correlations.csv"), config.getPaths().getFeatureCorrelationsCsv());
        assertEquals(Paths.get("build/out/what_if_summary.csv"), config.getPaths().getWhatIfSummaryCsv());
        assertEquals(Paths.get("build/out/charts"), config.getPaths().getChartsDir());
        assertEquals(AnalysisGranularity.METHOD, config.getSelection().getGranularity());
        assertEquals("bug", config.getSelection().getClassAttributeName());
        assertEquals("yes", config.getSelection().getPositiveClassValue());
        assertEquals("LOC_METHOD", config.getSelection().getSizeAttributeName());
        assertEquals(List.of("RF", "NB"), config.getSelection().getClassifierIds());
        assertEquals(3, config.getExecution().getRuns());
        assertEquals(4, config.getExecution().getFolds());
        assertEquals(123L, config.getExecution().getSeed());
        assertEquals(2, config.getExecution().getMaxParallelism());
        assertFalse(config.getExecution().isApplySmote());
        assertFalse(config.getWhatIfOptions().isEnabled());
        assertEquals("NSmells", config.getWhatIfOptions().getFeatureName());
        assertEquals("RF", config.getWhatIfOptions().getClassifierId());
    }

    @Test
    void fromArgsDisablesWhatIfByDefaultForMilestone2Flow() {
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[0]);

        assertEquals(Config.DEFAULT_MAX_PARALLELISM, config.getExecution().getMaxParallelism());
        assertFalse(config.getExecution().isApplySmote());
        assertFalse(config.getWhatIfOptions().isEnabled());
    }

    @Test
    void fromArgsCanEnableWhatIfExplicitly() {
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{"--whatif=true"});

        assertTrue(config.getWhatIfOptions().isEnabled());
    }
}

