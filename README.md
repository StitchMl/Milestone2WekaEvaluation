## Overview

This project performs a **10×10-fold** cross-validation on three classifiers (RandomForest, NaiveBayes, IBk) to compare their performance in terms of Precision, Recall, AUC, Kappa, and NPofB20 using Weka's API. The runner dynamically reads the dataset paths from a configuration file (`datasets.txt`), clones the classifiers at each repetition to limit memory consumption, and collects the average results over the 100 experiments to identify the best classifier for each metric and dataset.

## Table of Contents

* [Features](#features)
* [Prerequisites](#prerequisites)
* [Installation](#installation)
* [Configuration](#configuration)
* [Usage](#usage)
* [Project Structure](#project-structure)

## Features

* Performing **10×10-fold cross-validation** for robust estimates of classification metrics.
* Calculation and aggregation of **Precision, Recall, AUC, Kappa, NPofB20** for each classifier.
* Cloning of classifiers at each repetition to avoid OutOfMemoryError.
* Dynamic reading of datasets from a `datasets.txt` file, facilitating the addition or removal of new projects.
* Automatic reporting of the best classifier for each metric and dataset.

## Prerequisites

* Java JDK 1.8+
* Maven 3.6+
* 64-bit JVM with at least `-Xmx4g` of heap (recommended).
* File `datasets.txt` with the paths to the datasets in ARFF or CSV format.

## Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/tuo-utente/Milestone2WekaEvaluation.git
   cd Milestone2WekaEvaluation
   ```
2. **Build the project with Maven**

   ```bash
   mvn clean package
   ```

   This generates an executable jar in `target/milestone2-1.0-SNAPSHOT.jar`.

## Configuration

Create (or edit) `datasets.txt` in the run directory, entering a path per line. For example:

```
data/projA.arff
data/projB.csv
```

Blank lines and comments (`# ...`) will be ignored.

## Usage

Run the application with:

```bash
java -Xmx4g -jar target/milestone2-1.0-SNAPSHOT.jar
```

The output will show for each dataset and classifier the averages of P/R/AUC/Kappa/NPofB20, followed by a summary of the 'best' classifier for each metric.

## Project Structure

```
├── src/
│   ├── main/java/com/milestone2/
│   │   ├── ClassifierFactory.java     # creates the 3 classifiers
│   │   ├── CrossValidator.java        # handles 10×10-fold CV
│   │   ├── DataManager.java           # ARFF/CSV charge
│   │   ├── MetricsCalculator.java     # calculates Precision/Recall/AUC/Kappa/NPofB20
│   │   └── Milestone2Runner.java      # co-ordinates the execution flow
│   └── resources/
│       └── log4j2.xml                 # log settings
├── datasets.txt                       # list of datasets to be evaluated
├── pom.xml                            # Maven configuration
└── README.md                          # this file
```

This structure follows the standard conventions of Java Maven projects.