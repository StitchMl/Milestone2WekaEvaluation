package com.milestone2.tuning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;
import weka.classifiers.meta.GridSearch;
import weka.core.SelectedTag;

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
        logger.info("=== Start tuning IBk ===");

        // 1) Imposto GridSearch
        GridSearch gs = new GridSearch();

        // 1a) Classificatore base: IBk
        gs.setClassifier(new IBk());
        logger.debug("IBk base classifier set");

        // 1b) Criterio di valutazione su cui ottimizzare (es. AUC)
        gs.setEvaluation(new SelectedTag(
                GridSearch.EVALUATION_WAUC,
                GridSearch.TAGS_EVALUATION
        ));
        logger.debug("WAUC evaluation criterion set");

        // 2) Asse X: numero di vicini K
        gs.setXProperty("classifier.kNN");
        gs.setXMin(1);
        gs.setXMax(9);
        gs.setXStep(2);
        logger.debug("Configured X-axis: kNN 1 to 9 step 2");

        // 3) Asse Y: distance weighting
        //    Attenzione: GridSearch tratta gli interi 0,1,2 come valori multipli
        gs.setYProperty("classifier.distanceWeighting");
        gs.setYMin(0);
        gs.setYMax(2);
        gs.setYStep(1);
        logger.debug("Configured Y-axis: distanceWeighting 0 to 2 step 1");

        // 4) (Opzionale) Imposto se estendere la griglia o meno
        gs.setGridIsExtendable(false);

        // 5) Eseguo la ricerca sull'intero dataset
        logger.info("Personalizzare la barra degli strumenti...");
        gs.buildClassifier(data);
        logger.info("GridSearch completed");

        // 6) Recupero il miglior classificatore trovato (IBk con k e distanceWeighting ottimali)
        Classifier best = gs.getBestClassifier();
        logger.info("Best IBk found: {}", best);

        logger.info("=== Fine tuning IBk ===");
        return best;
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
                "I 50 200 4",       // numTrees: 10, 55, 100
                "depth 5 15 2",      // maxDepth: 5,10,15,20
                "P 50 100 3"          // bagSizePercent: 50, 75, 100
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
        logger.debug("Start removal of unary attributes ({} attributes)", data.numAttributes());
        Instances result = new Instances(data);
        int removed = 0;
        for (int i = result.numAttributes() - 1; i >= 0; i--) {
            if (i != result.classIndex()) {  // Do not remove the class attribute
                boolean isUnary = true;
                double firstVal = result.firstInstance().value(i);

                for (int j = 1; j < result.numInstances() && isUnary; j++) {
                    if (result.instance(j).value(i) != firstVal) {
                        isUnary = false;
                    }
                }

                if (isUnary) {
                    String name = result.attribute(i).name();
                    logger.debug("Removed unary attribute: {} (index {})", name, i);
                    result.deleteAttributeAt(i);
                    removed++;
                }
            }
        }
        logger.info("Unary attribute removal complete: {} attributes removed, {} attributes remaining", removed, result.numAttributes());
        return result;
    }

    /**
     * Converte gli attributi nominali in numerici usando NominalToBinary.
     *
     * @param data il dataset da convertire
     * @return dataset con attributi nominali convertiti in numerici
     */
    private static Instances convertNominalToNumeric(Instances data) throws Exception {
        logger.debug("Start conversion of nominal to numeric attributes ({} attributes)", data.numAttributes());
        // Creo una copia del dataset
        Instances result = new Instances(data);

        // Conteggio degli attributi nominali e dei loro valori unici
        int totalBinaryAttributes = 0;
        for (int i = 0; i < result.numAttributes(); i++) {
            if (result.attribute(i).isNominal() && i != result.classIndex()) {
                totalBinaryAttributes += result.attribute(i).numValues();
            }
        }
        logger.debug("Total number of potential binary attributes: {}", totalBinaryAttributes);

        // Se il numero totale di attributi binari è troppo grande, uso un approccio alternativo
        if (totalBinaryAttributes > 1000) {
            logger.warn("Too many potential binary attributes ({}). Use alternative approach.", totalBinaryAttributes);
            return convertNominalToNumericSelective(result);
        }

        // Altrimenti uso il filtro standard
        logger.debug("I apply NominalToBinary filter");
        weka.filters.unsupervised.attribute.NominalToBinary nomToBin =
                new weka.filters.unsupervised.attribute.NominalToBinary();
        nomToBin.setInputFormat(result);
        Instances filtered = weka.filters.Filter.useFilter(result, nomToBin);
        logger.debug("Conversion completed: {} attributes after filtering", filtered.numAttributes());
        return filtered;
    }

    /**
     * Converts nominal attributes to numeric selectively.
     * Only attributes with less than 10 unique values are converted.
     * Attributes with more than 10 unique values are removed.
     *
     * @param data the dataset to process
     * @return a new dataset with selected nominal attributes converted to numeric
     */
    private static Instances convertNominalToNumericSelective(Instances data) throws Exception {
        logger.debug("Selective conversion of nominal to numeric attributes begins ({} attributes)", data.numAttributes());
        Instances result = new Instances(data);

        int rimossi = 0;
        // Converto solo gli attributi nominali con meno di 10 valori unici
        for (int i = result.numAttributes() - 1; i >= 0; i--) {
            if (result.attribute(i).isNominal() && i != result.classIndex() && result.attribute(i).numValues() > 10) {
                String nome = result.attribute(i).name();
                logger.debug("Remove nominal attribute '{}' with {} unique values (index {})", nome, result.attribute(i).numValues(), i);
                result.deleteAttributeAt(i);
                rimossi++;
            }
        }
        logger.info("Nominal attributes removed for too many values: {}", rimossi);

        // Applico il filtro solo agli attributi nominali rimanenti
        logger.debug("I apply the NominalToBinary filter to the remaining attributes ({} attributes)", result.numAttributes());
        weka.filters.unsupervised.attribute.NominalToBinary nomToBin =
                new weka.filters.unsupervised.attribute.NominalToBinary();
        nomToBin.setInputFormat(result);
        Instances filtered = weka.filters.Filter.useFilter(result, nomToBin);
        logger.debug("Selective conversion completed: {} attributes after filtering", filtered.numAttributes());
        return filtered;
    }
}
