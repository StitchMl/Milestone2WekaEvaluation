package com.milestone2;

import com.milestone2.analysis.AnalysisConfig;
import com.milestone2.dataset.DatasetValidationService;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DatasetValidationServiceTest {

    @Test
    void validateRejectsMoreFoldsThanInstances() throws Exception {
        Instances data = new Instances(new StringReader(String.join(System.lineSeparator(),
                "@relation demo",
                "@attribute LOC numeric",
                "@attribute bug {yes,no}",
                "@data",
                "10,no",
                "20,yes",
                "30,no"
        )));
        data.setClassIndex(data.attribute("bug").index());
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--validation=cross-validation",
                "--folds=4",
                "--whatif=false"
        });

        assertThrows(IllegalArgumentException.class, () -> new DatasetValidationService().validate(data, config));
    }

    @Test
    void validateAllowsWalkForwardWhenFoldCountWouldBeTooLarge() throws Exception {
        Instances data = new Instances(new StringReader(String.join(System.lineSeparator(),
                "@relation demo",
                "@attribute ReleaseId {r1,r2,r3}",
                "@attribute LOC numeric",
                "@attribute bug {yes,no}",
                "@data",
                "r1,10,no",
                "r2,20,yes",
                "r3,30,no"
        )));
        data.setClassIndex(data.attribute("bug").index());
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{
                "--validation=walk-forward",
                "--temporal-attribute=ReleaseId",
                "--whatif=false",
                "--folds=10"
        });

        new DatasetValidationService().validate(data, config);
    }
}

