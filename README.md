## Overview

This project evaluates defect prediction classifiers with Weka for the Milestone 2 workflow and also exposes an optional exam-oriented extension for feature what-if analysis:

- Milestone 2 accuracy benchmarking with 10x10-fold cross-validation.
- An optional what-if analysis based on feature correlation and the derived datasets A, B+, B and C.

The application is organized around small orchestration and reporting components instead of a single monolithic runner. Classifier evaluation, fold execution, chart generation, feature correlation, what-if scenario building, and CSV publishing are handled by separate classes.

## Build

```powershell
.\mvnw.cmd clean package
```

The project produces an executable shaded jar in `target/`.

## Run

```powershell
.\run-analysis.cmd
```

Useful CLI options:

- `--data-dir=...` selects the dataset folder. Supported formats are CSV and ARFF.
- `--output-dir=...` chooses the output folder.
- `--class-attribute=bug` sets the target attribute explicitly.
- `--positive-class=yes` sets the positive class explicitly.
- `--classifiers=RF,NB` restricts the classifier catalog.
- `--runs=10 --folds=10 --seed=42` controls the cross-validation execution.
- `--threads=N` caps how many cross-validation folds run concurrently. Default: automatic, up to `min(folds, CPU-1)`.
- `--smote=true|false` enables or disables SMOTE in the preprocessing pipeline. Default: `false` for the Milestone 2 baseline.
- `--whatif=true|false` enables or disables the optional what-if analysis. Default: `false`.
- `--whatif-feature=NSmells` forces the feature used to build B+, B and C.
- `--whatif-classifier=RF` forces the classifier used in the what-if prediction study.

If `--whatif-feature` is not provided, the application prefers `NSmells` when present and zeroable. Otherwise it falls back to the strongest zeroable numeric feature by absolute correlation with the bug label. If `--whatif-classifier` is not provided, the application picks the best cross-validation classifier by Kappa and then AUC.

At startup the application validates the dataset directory, the classifier configuration path, and the current Weka classifier catalog by instantiating every configured classifier from `classifiers.properties`.

During evaluation the application also rejects datasets with fewer instances than the requested folds and warns when the minority class is smaller than the fold count, because that setup makes the cross-validation metrics less reliable.

Project launchers intentionally clear `_JAVA_OPTIONS` before invoking Java, so repository-local build and run commands stay free from the launcher banner injected by the machine environment.

## Outputs

Each run generates:

- `output/results.csv`: aggregate classifier metrics for each dataset.
- `output/fold_metrics.csv`: per-fold metrics for the 10x10 study.
- `output/milestone2_summary.csv`: best classifier per metric plus the overall milestone winner chosen by Kappa and AUC.
- `output/feature_correlations.csv`: ranking of numeric features by correlation with bugginess when the optional what-if analysis is enabled.
- `output/what_if_summary.csv`: scenario summaries for A, B+, B, C plus the paired B+ -> B impact row when the optional what-if analysis is enabled.
- `output/charts/`: bar charts and box plots for the classifier comparison.

## Architecture

The main flow is:

1. `AnalysisApplication` prepares the runtime and opens the output bundle.
2. `AnalysisRunner` discovers datasets and delegates each dataset to `DatasetAnalyzer`.
3. `DatasetAnalyzer` performs:
   - classifier evaluation through `ModelEvaluator`
   - optional what-if analysis through `WhatIfAnalyzer`
4. `DatasetReportPublisher` writes CSV outputs, generates charts, and logs the best metric winners.

The what-if slice is intentionally separated into focused components:

- `FeatureCorrelationAnalyzer`
- `WhatIfFeatureSelector`
- `WhatIfClassifierSelector`
- `WhatIfDatasetBuilder`
- `WhatIfPredictionService`
- `WhatIfScenarioSummarizer`

## Exam Workflow Support

The repository is centered on Milestone 2 and also supports an optional extension for the broader exam narrative.

Accuracy phase:

1. compare the configured classifiers with 10x10-fold cross-validation
2. inspect Precision, Recall, F1, Kappa, AUC, NPofB20 and Accuracy
3. identify the best classifier for the dataset

What-if phase:

1. rank numeric features by correlation with the bug label
2. select an actionable feature, preferably `NSmells`
3. build:
   - `A`: original dataset
   - `B+`: instances where the selected feature is greater than zero
   - `B`: copy of `B+` with the selected feature forced to zero
   - `C`: instances where the selected feature is zero
4. train the selected classifier on `A`
5. compare predictions on `A`, `B+`, `B`, and `C`
6. inspect the paired `B+ -> B` impact row to estimate potentially avoidable buggy methods

## Report Outline

The exam report outline is available in [docs/exam-report-outline.md](docs/exam-report-outline.md). It mirrors the six-section structure requested by the course material and maps each section to the outputs generated by this project.

## Verification

The codebase includes automated checks for:

- classifier catalog loading and Weka instantiation
- Milestone 2 smoke execution with a real ARFF dataset
- configuration parsing and summary selection rules
