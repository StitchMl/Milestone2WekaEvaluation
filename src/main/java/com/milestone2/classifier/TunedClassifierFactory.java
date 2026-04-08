package com.milestone2.classifier;

import com.milestone2.analysis.NetlibRuntimeConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Randomizable;
import weka.core.Utils;

/**
 * Builds configured classifiers from declarative definitions.
 */
public class TunedClassifierFactory {
    private static final Logger log = LoggerFactory.getLogger(TunedClassifierFactory.class);

    private TunedClassifierFactory() {
    }

    public static Classifier createClassifier(ClassifierDefinition definition, long seed) throws Exception {
        NetlibRuntimeConfigurer.configurePureJava();

        String[] options = definition.getOptions() == null || definition.getOptions().isBlank()
                ? new String[0]
                : Utils.splitOptions(definition.getOptions());

        Classifier classifier = AbstractClassifier.forName(definition.getClassName(), options);
        if (classifier instanceof Randomizable) {
            ((Randomizable) classifier).setSeed((int) (seed & Integer.MAX_VALUE));
        }

        log.debug("Classifier '{}' instantiated as {} with options '{}'",
                definition.getDisplayName(),
                definition.getClassName(),
                definition.getOptions());
        return classifier;
    }
}

