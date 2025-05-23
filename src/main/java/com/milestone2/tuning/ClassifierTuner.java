package com.milestone2.tuning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;

/**
 * Utility to optimise IBk and RandomForest:
 * - IBk: find k ∈ {1,3,5,7,9} with CVParameterSelection
 * - RF: finds 3 parameters with GridSearch
 */
public class ClassifierTuner {
    private static final Logger logger = LoggerFactory.getLogger(ClassifierTuner.class);

    /**
     * Private constructor to avoid instantiation.
     */
    private ClassifierTuner() {
        // It does not have to be instantiated
    }

    /**
     * Optimizes the parameter k of IBk using CVParameterSelection.
     *
     * @param data dataset to optimize on (training only)
     * @return optimised IBk
     */
    public static Classifier tuneIBk(Instances data) throws Exception {
        CVParameterSelection cvs = new CVParameterSelection();
        // Defines the -K parameter with values 1..9 in 5 steps (1,3,5,7,9)
        cvs.setCVParameters(new String[]{ "K 1 9 5" });
        cvs.setClassifier(new IBk());
        cvs.buildClassifier(data);

        // Extracts the best value of k
        String[] opts = cvs.getBestClassifierOptions();
        // Apply the optimal parameter to a new IBk
        IBk bestIbk = new IBk();
        bestIbk.setOptions(opts);
        return bestIbk;
    }

    /**
     * Optimises RandomForest on numTrees and maxDepth via GridSearch.
     *
     * @param data dataset to optimize on (training only)
     * @return RandomForest optimised
     */
    public static Classifier tuneRandomForest(Instances data) throws Exception {
        logger.info("=== RandomForest tuning start ===");

        // 1) Removing unary attributes
        logger.debug("Removing unary attributes from the dataset ({} instances, {} attributes)",
                data.numInstances(), data.numAttributes());
        Instances filteredData = removeUnaryAttributes(data);
        logger.debug("After removeUnary: {} attributes", filteredData.numAttributes());

        // 2) I convert nominal to numeric
        logger.debug("Conversion of nominal to numeric attributes");
        Instances numericData = convertNominalToNumeric(filteredData);
        logger.debug("After convertNominal: {} attributes ({} instances)",
                numericData.numAttributes(), numericData.numInstances());

        // 3) I configure CVParameterSelection
        logger.debug("I configure CVParameterSelection for numTrees and maxDepth");
        CVParameterSelection cvs = new CVParameterSelection();
        cvs.setCVParameters(new String[]{
                "I 10 100 3",       // numTrees: 10, 55, 100
                "depth 5 20 4"      // maxDepth: 5,10,15,20
        });
        RandomForest baseRf = new RandomForest();
        cvs.setClassifier(baseRf);

        // 4) I perform the internal search
        logger.info("Starting CVParameterSelection (10 times internal CV)...");
        cvs.buildClassifier(numericData);
        logger.info("CVParameterSelection completed");

        // 5) I extract the best options
        String[] bestOpts = cvs.getBestClassifierOptions();
        String best = String.join(" ", bestOpts);
        logger.info("Best options found: {}", best);

        // 6) Applico i parametri al nuovo RandomForest
        RandomForest bestRf = new RandomForest();
        bestRf.setOptions(bestOpts);
        logger.info("Optimised RandomForest created with options: {}", best);
        logger.info("=== Fine tuning RandomForest ===");

        return bestRf;
    }

    /**
     * Removes unary attributes from the dataset.
     * An attribute is unary if it has only one unique value across all instances.
     *
     * @param data the dataset to process
     * @return a new dataset without unary attributes
     */
    private static Instances removeUnaryAttributes(Instances data) {
        Instances result = new Instances(data);
        for (int i = result.numAttributes() - 1; i >= 0; i--) {
            if (i != result.classIndex()) {  // Non rimuovere l'attributo classe
                boolean isUnary = true;
                double firstVal = result.firstInstance().value(i);

                for (int j = 1; j < result.numInstances() && isUnary; j++) {
                    if (result.instance(j).value(i) != firstVal) {
                        isUnary = false;
                    }
                }

                if (isUnary) {
                    result.deleteAttributeAt(i);
                }
            }
        }
        return result;
    }

    /**
     * Converte gli attributi nominali in numerici usando NominalToBinary.
     *
     * @param data il dataset da convertire
     * @return dataset con attributi nominali convertiti in numerici
     */
    private static Instances convertNominalToNumeric(Instances data) throws Exception {
        weka.filters.unsupervised.attribute.NominalToBinary nomToBin =
                new weka.filters.unsupervised.attribute.NominalToBinary();
        nomToBin.setInputFormat(data);
        return weka.filters.Filter.useFilter(data, nomToBin);
    }
}
