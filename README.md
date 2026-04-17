## Overview

This project evaluates defect prediction classifiers with Weka for the Milestone 2 workflow and ships a maintainability-oriented what-if study by default:

- Walk-forward validation that preserves release order through the `ReleaseId` temporal attribute.
- Comparison of the three milestone classifiers: Random Forest, Naive Bayes and IBk.
- A what-if analysis based on feature correlation and the derived datasets A, B+, B and C.

The application is intentionally organized around small orchestration, validation, reporting and what-if components instead of a monolithic runner. The largest production files stay small and responsibilities are isolated by package.

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
- `--validation=walk-forward|cross-validation` selects the validation strategy. Default: `walk-forward`.
- `--temporal-attribute=ReleaseId` selects the attribute used to preserve temporal order. Default: `ReleaseId`.
- `--min-train-periods=N` sets how many historical periods must be accumulated before the first walk-forward prediction. Default: `1`.
- `--runs=N --folds=N` controls the legacy randomized cross-validation flow when `--validation=cross-validation`.
- `--threads=N` caps how many cross-validation folds run concurrently. Default: automatic, up to `min(folds, CPU-1)`.
- `--smote=true|false` enables or disables SMOTE in the preprocessing pipeline. Default: `false` for the Milestone 2 baseline.
- `--whatif=true|false` enables or disables the what-if analysis. Default: `true`.
- `--whatif-feature=NSmells` forces the feature used to build B+, B and C.
- `--whatif-classifier=RF` forces the classifier used in the what-if prediction study.

If `--whatif-feature` is not provided, the application prefers `NSmells` when present and zeroable. Otherwise, it falls back to the strongest zeroable numeric feature by an absolute correlation with the bug label. If `--whatif-classifier` is not provided, the application picks the best validated classifier by Kappa and then AUC.

At a startup the application validates the dataset directory, the classifier configuration path, and the current Weka classifier catalog by instantiating every configured classifier from `classifiers.properties`.

During walk-forward validation the application requires a temporal attribute with contiguous ordered periods. During randomized cross-validation it rejects datasets with fewer instances than the requested folds and warns when the minority class is smaller than the fold count, because that setup makes the metrics less reliable.

Project launchers intentionally clear `_JAVA_OPTIONS` before invoking Java, so repository-local build and run commands stay free from the launcher banner injected by the machine environment.

## Outputs

Each run generates:

- `output/results.csv`: aggregate classifier metrics for each dataset, including validation strategy and temporal attribute.
- `output/fold_metrics.csv`: per-split metrics plus explicit training and test windows for the validation strategy in use.
- `output/milestone2_summary.csv`: best classifier per metric plus the overall milestone winner chosen by Kappa and AUC.
- `output/feature_correlations.csv`: ranking of numeric features by a correlation with bugginess, plus the feature actually selected for the what-if study.
- `output/what_if_summary.csv`: scenario summaries for A, B+, B, C plus the paired B+ → B impact row used to estimate potentially avoidable buggy entities.
- `output/charts/`: bar charts and box plots for the classifier comparison.

## Architecture

The main flow is:

1. `AnalysisApplication` prepares the runtime and opens the output bundle.
2. `AnalysisRunner` discovers datasets and delegates each dataset to `DatasetAnalyzer`.
3. `DatasetAnalyzer` performs:
   - classifier evaluation through `ModelEvaluator`
   - what-if analysis through `WhatIfAnalyzer`
4. `DatasetReportPublisher` writes CSV outputs, generates charts, and logs the best metric winners.

Validation is now split into dedicated slices:

- `crossvalidation/`: legacy randomized repeated k-fold execution.
- `validation/`: strategy selection and validation abstractions.
- `validation/timeseries/`: walk-forward period extraction and temporal window generation.

The what-if slice is intentionally separated into focused components:

- `FeatureCorrelationAnalyzer`
- `WhatIfFeatureSelector`
- `WhatIfClassifierSelector`
- `WhatIfDatasetBuilder`
- `WhatIfPredictionService`
- `WhatIfScenarioSummarizer`

A full class-by-class architecture inventory is available in [docs/architecture-map.md](docs/architecture-map.md).

## Exam Workflow Support

The repository is centered on Milestone 2 and keeps the default workflow aligned with the deliverable.

Accuracy phase:

1. compare the configured classifiers with walk-forward validation over ordered releases
2. inspect Precision, Recall, F1, Kappa, AUC, NPofB20 and Accuracy
3. identify the best classifier for the dataset

What-if phase:

1. rank numeric features by a correlation with the bug label
2. identify the strongest overall correlation, and the actionable what-if feature, preferably `NSmells`
3. build:
   - `A`: original dataset
   - `B+`: instances where the selected feature is greater than zero
   - `B`: copy of `B+` with the selected feature forced to zero
   - `C`: instances where the selected feature is zero
4. train the selected classifier on `A`
5. compare predictions on `A`, `B+`, `B`, and `C`
6. inspect the paired `B+ -> B` impact row to estimate potentially avoidable buggy methods.

## Report Outline

The exam report outline is available in [docs/exam-report-outline.md](docs/exam-report-outline.md). It mirrors the six-section structure requested by the course material and maps each section to the outputs generated by this project.

## Verification

The codebase includes automated checks for:

- classifier catalog loading and Weka instantiation
- walk-forward temporal window construction and ordering guarantees
- Milestone 2 smoke execution with real ARFF and CSV datasets
- configuration parsing and summary selection rules
