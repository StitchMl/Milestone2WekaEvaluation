# Mappa di rinomina file — Milestone2WekaEvaluation

Nomi **semplificati** proposti da me (più corti e parlanti, senza prefissi ridondanti col package).
Rivedi la colonna **Nuovo nome** e cambia ciò che non ti convince; poi dico "applico".
I `package-info.java` non sono elencati (restano; il `package` si aggiorna col rename cartella).
Cartelle: `analysis→startupUtility, classifier→classifier, crossvalidation→crossValidation, dataset→dataset, evaluation→evaluation, feature→featureAnalysis, fold→foldMetadata, metric→metric, prediction→prediction, report→csvExporter, summary→summary, validation→validationStrategy, validation/timeseries→validationTimeseries, whatif→whatif`.

> **whatif (Milestone 3):** lasciato invariato per ora (i nomi `Analyzer`/`AnalysisReport` collidono con `dataset`); lo semplifichiamo in M3.

## radice `com.milestone2`
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| MainApp | Entry point minimale, delega all'applicazione. | `MainApp` |

## startupUtility (ex `analysis`)
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| AnalysisApplication | Entry point di una singola esecuzione. | `Application` |
| AnalysisArgumentsParser | Argomenti CLI grezzi → configurazione. | `CliArgumentsParser` |
| AnalysisConfig | Configurazione immutabile radice. | `RunConfig` |
| AnalysisConfigBuilder | Assembla RunConfig. | `RunConfigBuilder` |
| AnalysisExecution | Strategia/seed/validazione a runtime. | `ExecutionSettings` |
| AnalysisExecutionBuilder | Parsing switch CLI runtime. | `ExecutionSettingsBuilder` |
| AnalysisGranularity | Granularità di analisi. | `Granularity` |
| AnalysisOutputs | Apre/chiude i writer CSV del run. | `OutputWriters` |
| AnalysisPathValidator | Valida i path input/output. | `PathValidator` |
| AnalysisPaths | Bundle immutabile dei path risolti. | `ResolvedPaths` |
| AnalysisPathsBuilder | Risolve i path default/utente. | `ResolvedPathsBuilder` |
| AnalysisRunner | Itera i dataset e li delega all'analyzer. | `Runner` |
| AnalysisRuntime | Prepara filesystem e logging. | `RuntimePreparer` |
| AnalysisSelection | Class/positive/size attr + selezione classificatori. | `SelectionSettings` |
| AnalysisSelectionBuilder | Parsing switch CLI di selezione. | `SelectionSettingsBuilder` |
| AnalysisStartupValidator | Valida path e catalogo all'avvio. | `StartupValidator` |
| CliArgument | Argomento `--key=value` immutabile. | `CliArgument` |
| Config | Default di progetto e nomi file costanti. | `Defaults` |
| NetlibRuntimeConfigurer | Forza il backend netlib pure-Java. | `NetlibConfigurer` |
| OutputCloseSupport | Chiusura sicura dei writer. | `WriterCloseSupport` |
| PreprocessingConfig | Config balancing + feature selection. | `PreprocessingConfig` |
| ValidationConfig | Config strategia/parametri di validazione. | `ValidationConfig` |

## classifier
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| ClassifierCatalog | Lista immutabile dei classificatori. | `Catalog` |
| ClassifierCatalogValidator | Istanzia ogni classificatore (fail-fast). | `CatalogValidator` |
| ClassifierDefinition | Metadati immutabili (da properties). | `Definition` |
| ClassifierDefinitionResolver | Properties → definizioni concrete. | `DefinitionResolver` |
| ClassifierEvaluationReport | Metriche aggregate e per-split. | `EvaluationReport` |
| ClassifierIdParser | Parsing ID classificatore da CLI. | `IdParser` |
| ClassifierIdResolver | Risolve il sottoinsieme di classificatori. | `IdResolver` |
| ClassifierPropertiesLoader | Carica il file properties. | `PropertiesLoader` |
| OverallClassifierWinner | Vincitore complessivo (immutabile). | `OverallWinner` |
| OverallClassifierWinnerSelector | Sceglie il vincitore (Kappa poi AUC). | `OverallWinnerSelector` |
| TunedClassifierFactory | Istanzia i classificatori Weka con opzioni/seed. | `ClassifierFactory` |

## crossValidation (ex `crossvalidation`)
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| CrossValidationExecutor | k-fold ripetuta stratificata in parallelo. | `KFoldExecutor` |
| CrossValidationParallelismResolver | Limita il parallelismo dei fold. | `ParallelismResolver` |

## dataset
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| DatasetAnalysisReport | Aggregato immutabile del risultato. | `AnalysisReport` |
| DatasetAnalyzer | Coordina valutazione + what-if per dataset. | `Analyzer` |
| DatasetDiscovery | Scopre i dataset CSV/ARFF. | `Discovery` |
| DatasetReportPublisher | Scrive output e attiva grafici/log. | `ReportPublisher` |
| DatasetValidationService | Valida le precondizioni del dataset. | `ValidationService` |
| GenericDataLoader | Carica CSV/ARFF, configura la classe. | `DataLoader` |

## evaluation
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| BalancingStrategy | Enum tecniche di bilanciamento. | `BalancingStrategy` |
| FeatureSelectionStrategy | Enum strategie di FS. | `FeatureSelectionStrategy` |
| ModelEvaluator | Sceglie executor, valuta split, aggrega. | `ModelEvaluator` |
| PositiveClassResolver | Trova label/indice classe positiva. | `PositiveClassResolver` |
| Preprocessor | Pipeline di filtri per-fold (no-leakage). | `Preprocessor` |

## featureAnalysis (ex `feature`)
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| FeatureCorrelation | Riga di correlazione immutabile. | `Correlation` |
| FeatureCorrelationAnalyzer | Calcola le correlazioni col label. | `CorrelationAnalyzer` |
| FeatureCorrelationWriter | Esporta il ranking feature. | `CorrelationWriter` |
| PearsonCorrelationCalculator | Calcola il coefficiente di correlazione. | `PearsonCalculator` |

## foldMetadata (ex `fold`)
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| FoldContext | Metadati di uno split. | `FoldContext` |
| FoldDistributionDatasetFactory | Dataset-grafico della distribuzione classi. | `FoldDistributionChart` |
| FoldEvaluationService | Addestra/valuta il classificatore sullo split. | `FoldEvaluator` |
| FoldResultProducer | Interfaccia funzionale per valutare uno split. | `FoldResultProducer` |
| FoldResultsWriter | Esporta metriche per-split + finestra. | `FoldResultsWriter` |
| PerFoldResult | Risultato metriche immutabile di uno split. | `FoldResult` |

## metric
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| BestMetricLogger | Logga il miglior classificatore per metrica. | `BestMetricLogger` |
| BudgetedDetectionRateCalculator | Helper per metriche effort-aware. | `BudgetedDetectionRate` |
| MetricAggregator | Media le metriche tra split. | `MetricAggregator` |
| MetricCategoryDatasetFactory | Dataset-grafico per confronto metriche. | `MetricChartData` |
| MetricDefinition | Elenco canonico metriche + estrazione. | `MetricDefinition` |
| MetricWinner | Miglior classificatore per una metrica. | `MetricWinner` |
| MetricWinnerSelector | Seleziona il vincitore per ogni metrica. | `MetricWinnerSelector` |
| Metrics | Contenitore metriche di uno split. | `Metrics` |
| NPofB20Calculator | Calcola NPofB20 (ranking per densità). | `NPofB20Calculator` |

## prediction
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| PredictionRecord | Predizione con label reale e probabilità. | `PredictionRecord` |
| RankedPrediction | Predizione ordinabile (effort-based). | `RankedPrediction` |
| RankedPredictionFactory | Predizioni Weka → ordinate per densità. | `RankedPredictionFactory` |
| ScenarioPredictionSummary | Vista aggregata predizioni per scenario. | `ScenarioSummary` |

## csvExporter (ex `report`)
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| ChartGenerator | Crea i grafici di confronto. | `ChartGenerator` |
| CsvResultsSupport | Helper condiviso per i CSV dei risultati. | `CsvSupport` |
| ResultsWriter | Esporta le metriche aggregate per dataset. | `ResultsWriter` |

## summary
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| Milestone2Summary | Riepilogo immutabile (vincitori). | `Summary` |
| Milestone2SummaryBuilder | Assembla il riepilogo. | `SummaryBuilder` |
| Milestone2SummaryRecordFactory | Crea i record CSV del riepilogo. | `SummaryRecordFactory` |
| Milestone2SummaryWriter | Esporta il CSV del riepilogo. | `SummaryWriter` |

## validationStrategy (ex `validation`)
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| OrderedHoldoutValidationExecutor | Holdout ordinato 80/20 temporale. | `HoldoutExecutor` |
| ValidationExecutor | Interfaccia strategia di validazione. | `ValidationExecutor` |
| ValidationExecutorSelector | Risolve l'executor per la strategia. | `ExecutorSelector` |
| ValidationStrategy | Enum delle strategie di validazione. | `ValidationStrategy` |

## validationTimeseries (ex `validation/timeseries`)
| File attuale | Ruolo | Nuovo nome |
|---|---|---|
| TemporalBucket | Slice contiguo di un periodo. | `TemporalBucket` |
| TemporalDatasetPartitioner | Raggruppa in periodi ordinati contigui. | `TemporalPartitioner` |
| WalkForwardValidationExecutor | Finestre walk-forward deterministiche. | `WalkForwardExecutor` |
| WalkForwardWindow | Finestra train/test walk-forward. | `WalkForwardWindow` |

## whatif (Milestone 3 — invariato per ora)
| File attuale | Nuovo nome |
|---|---|
| WhatIfAnalysisReport … WhatIfSummaryWriter (18 file) | *(invariati; rinomina rimandata a M3)* |
