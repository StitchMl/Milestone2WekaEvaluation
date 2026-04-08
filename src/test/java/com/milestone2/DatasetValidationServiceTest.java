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
        AnalysisConfig config = AnalysisConfig.fromArgs(new String[]{"--folds=4"});

        assertThrows(IllegalArgumentException.class, () -> new DatasetValidationService().validate(data, config));
    }
}

