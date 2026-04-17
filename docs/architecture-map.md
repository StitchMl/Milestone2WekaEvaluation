# Architecture Map

This document assigns a concrete responsibility to each production file, so the project stays maintainable, navigable and free from God classes.

## Package Roles

- `analysis`: application bootstrap, CLI parsing, immutable configuration, runtime preparation and output path wiring.
- `classifier`: declarative classifier catalog, classifier instantiation and milestone winner selection.
- `crossvalidation`: legacy repeated k-fold execution used only when explicitly requested.
- `dataset`: dataset discovery, loading, validation, orchestration and report publication.
- `evaluation`: preprocessing, positive-class resolution and model evaluation orchestration.
- `feature`: numeric feature correlation analysis and CSV export.
- `fold`: fold metadata, per-split metrics and CSV writing.
- `metric`: metric definitions, aggregation, milestone winners and effort-based indicators.
- `prediction`: prediction records and scenario aggregation DTOs.
- `report`: CSV summary export and chart generation.
- `summary`: milestone-specific winner summary generation.
- `validation`: validation-strategy abstraction and executor selection.
- `validation/timeseries`: temporal partitioning and walk-forward window construction.
- `whatif`: what-if feature selection, dataset mutation, scenario prediction and impact reporting.

## File Responsibilities

### root `com.milestone2`

- `MainApp`: minimal executable entry point that delegates to `AnalysisApplication`.

### `analysis`

- `AnalysisApplication`: top-level entry point for one analysis run.
- `AnalysisArgumentsParser`: turns raw CLI arguments into structured configuration.
- `AnalysisConfig`: immutable configuration root shared across the application.
- `AnalysisConfigBuilder`: coordinates the builders that assemble `AnalysisConfig`.
- `AnalysisExecution`: stores execution-time strategy, seed and validation settings.
- `AnalysisExecutionBuilder`: parses runtime-oriented CLI switches.
- `AnalysisGranularity`: represents the analysis granularity requested by the user.
- `AnalysisOutputs`: opens and closes all CSV writers for one run.
- `AnalysisPathValidator`: validates input and output paths before execution.
- `AnalysisPaths`: immutable bundle of resolved filesystem paths.
- `AnalysisPathsBuilder`: resolves default and user-provided paths.
- `AnalysisRunner`: iterates datasets and delegates each one to the dataset analyzer.
- `AnalysisRuntime`: prepares filesystem and logging before execution.
- `AnalysisSelection`: stores class attribute, positive class, size attribute and classifier selection.
- `AnalysisSelectionBuilder`: parses selection-oriented CLI switches.
- `AnalysisStartupValidator`: validates paths and classifier catalog wiring at a startup.
- `CliArgument`: immutable representation of one `--key=value` argument.
- `Config`: central place for project defaults and constant filenames.
- `NetlibRuntimeConfigurer`: forces the pure-Java netlib backend for portability.

### `classifier`

- `ClassifierCatalog`: immutable list of classifier definitions chosen for the run.
- `ClassifierCatalogValidator`: instantiates every configured classifier to fail fast on catalog errors.
- `ClassifierDefinition`: immutable classifier metadata loaded from properties.
- `ClassifierDefinitionResolver`: converts properties into concrete classifier definitions.
- `ClassifierEvaluationReport`: stores aggregate and per-split metrics for one classifier.
- `ClassifierIdParser`: parses classifier IDs from CLI text.
- `ClassifierIdResolver`: resolves the effective classifier subset from properties and CLI selection.
- `ClassifierPropertiesLoader`: loads the classifier properties file from disk.
- `OverallClassifierWinner`: immutable description of the overall milestone winner.
- `OverallClassifierWinnerSelector`: chooses the overall milestone winner by Kappa then AUC.
- `TunedClassifierFactory`: instantiates Weka classifiers with configured options and deterministic seeds.

### `crossvalidation`

- `CrossValidationExecutor`: executes repeated stratified k-fold validation in parallel.
- `CrossValidationParallelismResolver`: caps fold parallelism using fold count and CPU availability.

### `dataset`

- `DatasetAnalysisReport`: immutable aggregate of the full analysis result for one dataset.
- `DatasetAnalyzer`: coordinates classifier evaluation and what-if analysis for a dataset.
- `DatasetDiscovery`: discovers CSV and ARFF datasets from the configured directory.
- `DatasetReportPublisher`: writes dataset outputs and triggers chart/log publication.
- `DatasetValidationService`: validates dataset-level preconditions before model evaluation.
- `GenericDataLoader`: loads CSV or ARFF datasets and configures the class attribute.

### `evaluation`

- `ModelEvaluator`: chooses the validation executor, evaluates splits and aggregates metrics.
- `PositiveClassResolver`: finds the positive class label/index consistently across datasets.
- `Preprocessor`: builds the Weka filtering pipeline used before classifier training.

### `feature`

- `FeatureCorrelation`: immutable correlation row for one numeric feature.
- `FeatureCorrelationAnalyzer`: computes point-biserial style correlations against the bug label.
- `FeatureCorrelationWriter`: exports the feature ranking and selected what-if feature.
- `PearsonCorrelationCalculator`: computes the correlation coefficient from running sums.

### `fold`

- `FoldContext`: metadata for one evaluated validation split.
- `FoldEvaluationService`: trains one classifier on one split and computes metrics.
- `FoldResultProducer`: functional interface used by validation executors to evaluate one split.
- `FoldResultsWriter`: exports per-split metrics with validation-window metadata.
- `PerFoldResult`: immutable metrics result for one validation split.

### `metric`

- `BestMetricLogger`: logs the best classifier for each metric and the overall winner.
- `BudgetedDetectionRateCalculator`: shared helper for effort-aware metrics.
- `MetricAggregator`: averages metric values across validation splits.
- `MetricCategoryDatasetFactory`: builds chart datasets for metric comparison charts.
- `MetricDefinition`: canonical list of supported metrics and extraction rules.
- `MetricWinner`: immutable best-classifier result for one metric.
- `MetricWinnerSelector`: selects the winner for every supported metric.
- `Metrics`: immutable container for the metrics measured on one split.
- `NPofB20Calculator`: computes the NPofB20 indicator from ranked predictions.

### `prediction`

- `PredictionRecord`: stores one scenario prediction with an actual label and positive probability.
- `RankedPrediction`: sortable prediction used by effort-based metrics.
- `RankedPredictionFactory`: converts Weka predictions into ranked prediction objects.
- `ScenarioPredictionSummary`: aggregated view of predictions for one what-if scenario.

### `report`

- `ChartGenerator`: creates the milestone comparison charts.
- `ResultsWriter`: exports aggregate classifier metrics for each dataset.

### `summary`

- `Milestone2Summary`: immutable milestone summary containing metric winners and overall winner.
- `Milestone2SummaryBuilder`: assembles the milestone summary for one dataset.
- `Milestone2SummaryRecordFactory`: creates CSV records for summary export.
- `Milestone2SummaryWriter`: exports the milestone summary CSV.

### `validation`

- `ValidationExecutor`: strategy interface implemented by each validation flow.
- `ValidationExecutorSelector`: resolves the executor for the configured strategy.
- `ValidationStrategy`: enum describing the supported validation strategies.

### `validation/timeseries`

- `TemporalBucket`: contiguous slice of data belonging to one temporal period.
- `TemporalDatasetPartitioner`: groups the dataset into contiguous ordered temporal periods.
- `WalkForwardValidationExecutor`: executes deterministic walk-forward validation windows.
- `WalkForwardWindow`: immutable description of one walk-forward training/test split.

### `whatif`

- `WhatIfAnalysisReport`: immutable root report for correlation ranking plus scenario analysis.
- `WhatIfAnalyzer`: coordinates the full what-if flow for one dataset.
- `WhatIfClassifierSelection`: immutable description of the classifier chosen for what-if.
- `WhatIfClassifierSelector`: selects the classifier used for what-if prediction.
- `WhatIfDatasetBuilder`: builds A, B+, B and C datasets from the selected feature.
- `WhatIfDatasetSet`: immutable bundle of the A, B+, B and C datasets.
- `WhatIfFeatureSelection`: immutable description of the feature chosen for what-if.
- `WhatIfFeatureSelector`: selects the actionable feature to manipulate.
- `WhatIfImpactSummary`: immutable estimate of relieved and avoidable buggy entities.
- `WhatIfOptions`: immutable what-if configuration parsed from CLI.
- `WhatIfOptionsBuilder`: parses what-if-related CLI switches.
- `WhatIfOutputs`: opens and closes the what-if CSV writers.
- `WhatIfPredictionService`: trains the chosen classifier and predicts all what-if scenarios.
- `WhatIfScenario`: enum listing the named what-if datasets.
- `WhatIfScenarioReport`: immutable report that joins feature choice, classifier choice and scenario results.
- `WhatIfScenarioSummarizer`: aggregates raw scenario predictions into business-level summaries.
- `WhatIfSummaryRecordFactory`: creates CSV rows for a scenario and impact output.
- `WhatIfSummaryWriter`: exports the what-if summary CSV.
