# Proposta di riorganizzazione — Milestone2WekaEvaluation

Come per MantiMetrics: prima scegli i **nomi delle cartelle** (package), poi i **file** dentro ciascuna.
Per convenzione Java i package hanno iniziale minuscola. Sotto, per ogni package attuale: **ruolo** e uno slot `NOME: ______` da compilare.

Radice attuale: `com.milestone2`

| Cod | Package attuale | Ruolo (di cosa si occupa) | NOME nuovo |
|---|---|---|---|
| — | `com.milestone2` (radice) | `MainApp`: entry point minimale che delega ad `AnalysisApplication`. | `______` |
| A | `analysis` | Bootstrap dell'applicazione: parsing CLI, configurazione immutabile, preparazione runtime (filesystem/logging), risoluzione dei path di input/output, validazioni di avvio. | `startupUtility` |
| B | `classifier` | Catalogo dichiarativo dei classificatori (da `classifiers.properties`), istanziazione con opzioni/seed, selezione del vincitore complessivo (per Kappa poi AUC). | `classifier` |
| C | `crossvalidation` | Esecuzione della cross-validation ripetuta k-fold (10×10) in parallelo, con controllo del parallelismo sui fold. | `crossValidation` |
| D | `dataset` | Scoperta dei dataset (CSV/ARFF) nella cartella di input, caricamento e configurazione dell'attributo classe, validazioni a livello dataset, orchestrazione dell'analisi e pubblicazione dei report. | `dataset` |
| E | `evaluation` | Cuore della valutazione: pipeline di preprocessing (`Preprocessor`: RemoveType→NominalToBinary→ReplaceMissingValues→Standardize→FS→balancing), risoluzione della classe positiva, scelta dell'executor di validazione e aggregazione delle metriche. Contiene anche gli enum `BalancingStrategy`/`FeatureSelectionStrategy`. | `evaluation` |
| F | `feature` | Analisi di correlazione delle feature numeriche col target (Pearson/point-biserial) ed export CSV del ranking. | `featureAnalysis` |
| G | `fold` | Metadati di un singolo split di validazione, addestramento/valutazione del classificatore sul fold, risultati per-fold e loro scrittura su CSV. | `foldMetadata` |
| H | `metric` | Definizione delle metriche, aggregazione tra split, selezione dei vincitori per metrica, indicatori effort-based (`NPofB20Calculator`, `BudgetedDetectionRateCalculator`), dataset per i grafici. | `metric` |
| I | `prediction` | Record di predizione (probabilità/att. reale), predizioni ordinabili per densità (`RankedPrediction`, `RankedPredictionFactory`) usate dalle metriche effort-based, aggregati per scenario. | `prediction` |
| J | `report` | Export CSV delle metriche aggregate per dataset e generazione dei grafici di confronto. | `csvExporter` |
| K | `summary` | Costruzione ed export del riepilogo Milestone 2 (vincitori per metrica + complessivo) su CSV. | `summary` |
| L | `validation` | Astrazione della strategia di validazione (interfaccia `ValidationExecutor`, enum `ValidationStrategy`, selettore dell'executor). | `validationStrategy` |
| M | `validation/timeseries` | Partizionamento temporale del dataset in periodi ordinati e costruzione delle finestre **walk-forward** (training cumulativo, test sulla release successiva). | `validationTimeseries` |
| N | `whatif` | Analisi what-if (Milestone 3): selezione feature azionabile, costruzione dataset A/B/B+/C, predizione di scenario e stima dell'impatto. *(non centrale per M2)* | `whatif` |

## Come procediamo
1. Scrivimi i **nomi delle cartelle** (anche solo `A=..., B=..., ...`).
2. Poi, cartella per cartella, ti elenco i file con il loro ruolo e scegli i nomi.
3. Applico spostamenti/rinomine con fix di `package`/`import`/riferimenti e verifica statica; tu confermi con un build.
