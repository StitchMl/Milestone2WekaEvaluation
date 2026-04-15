package com.milestone2.validation.timeseries;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.fold.PerFoldResult;
import com.milestone2.metric.Metrics;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalkForwardValidationExecutorTest {

    @Test
    void executeBuildsGrowingTrainingWindows() throws Exception {
        Instances data = new Instances(new StringReader(String.join(System.lineSeparator(),
                "@relation temporalDemo",
                "@attribute ReleaseId {r1,r2,r3}",
                "@attribute LOC numeric",
                "@attribute bug {yes,no}",
                "@data",
                "r1,10,no",
                "r1,11,no",
                "r1,12,yes",
                "r1,13,yes",
                "r1,14,yes",
                "r2,15,no",
                "r2,16,no",
                "r2,17,yes",
                "r2,18,yes",
                "r2,19,yes",
                "r3,20,no",
                "r3,21,no",
                "r3,22,yes",
                "r3,23,yes",
                "r3,24,yes"
        )));
        data.setClassIndex(data.attribute("bug").index());
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--validation=walk-forward",
                "--temporal-attribute=ReleaseId",
                "--whatif=false"
        });

        List<PerFoldResult> results = new WalkForwardValidationExecutor().execute(
                data,
                config,
                (train, test, context) -> new PerFoldResult(
                        context.getRunIndex(),
                        context.getFoldIndex(),
                        context.getTrainingWindowLabel(),
                        context.getTestWindowLabel(),
                        train.numInstances(),
                        test.numInstances(),
                        new Metrics(0, 0, 0, 0, 0, 0, 0)
                )
        );

        assertEquals(2, results.size());
        assertEquals("r1", results.get(0).getTrainingWindowLabel());
        assertEquals("r2", results.get(0).getTestWindowLabel());
        assertEquals(5, results.get(0).getTrainingInstances());
        assertEquals(5, results.get(0).getTestInstances());
        assertEquals("r1..r2", results.get(1).getTrainingWindowLabel());
        assertEquals("r3", results.get(1).getTestWindowLabel());
        assertEquals(10, results.get(1).getTrainingInstances());
        assertEquals(5, results.get(1).getTestInstances());
    }

    @Test
    void executeRejectsDatasetsWherePeriodsReappearOutOfOrder() throws Exception {
        Instances data = new Instances(new StringReader(String.join(System.lineSeparator(),
                "@relation temporalDemo",
                "@attribute ReleaseId {r1,r2}",
                "@attribute LOC numeric",
                "@attribute bug {yes,no}",
                "@data",
                "r1,10,no",
                "r2,20,yes",
                "r1,30,no"
        )));
        data.setClassIndex(data.attribute("bug").index());
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--validation=walk-forward",
                "--temporal-attribute=ReleaseId",
                "--whatif=false"
        });

        assertThrows(IllegalArgumentException.class, () -> new WalkForwardValidationExecutor().execute(
                data,
                config,
                (train, test, context) -> new PerFoldResult(0, 0, new Metrics(0, 0, 0, 0, 0, 0, 0))
        ));
    }
}
