# Milestone 2 — Classificazione e Bug Prediction su Apache AVRO

**Corso:** ISW2 — **Progetto:** difettosità a livello di classe (target `Buggy`) sul dataset AVRO prodotto in Milestone 1
**Strumento:** Weka Java API 3.8.6 (progetto `Milestone2WekaEvaluation`) — **Seed:** 42 — **Data esecuzione:** 30/07/2026

---

## 1. Introduzione

L'obiettivo della Milestone 2 è addestrare e confrontare modelli di *bug prediction* a livello di classe sul dataset AVRO costruito in Milestone 1, valutando l'effetto di tre fattori metodologici (classificatore, feature selection, bilanciamento delle classi) sotto due tecniche di validazione. La milestone richiede in particolare il confronto di tre classificatori — Random Forest, Naïve Bayes e IBk (k-NN) — e la misura di metriche classiche ed *effort-aware*, con l'accento sul controllo intellettuale di ogni scelta metodologica.

Il dataset è organizzato per release ordinate temporalmente (`ReleaseId`), con la classe `Buggy` fortemente minoritaria (~14%). La variante principale analizzata è `pct34_total_gh0_churn0`; le restanti 15 varianti prodotte in M1 (combinazioni di percentuale di release usata come training, tipo di conteggio, presenza di feature GitHub e di churn) sono usate come analisi di sensibilità.

## 2. Metodologia

### 2.1 Classificatori

Il confronto riguarda **Random Forest** (default Weka, 100 alberi, profondità illimitata), **Naïve Bayes** (default Weka, senza kernel estimator) e **IBk** con **k = 1** (default Weka, valore tipico del corso). La scelta dei tre algoritmi è imposta dalla specifica; si sono mantenuti i parametri di default, senza tuning degli iperparametri, per confrontare i classificatori in condizioni standard e riproducibili.

### 2.2 Pipeline di pre-processing (leakage-safe)

Tutti i filtri sono incapsulati in un `FilteredClassifier`, quindi **fittati esclusivamente sul training fold** e applicati al test senza mai osservarlo. L'ordine della catena è: rimozione degli attributi identificativi `string` (`Project, Path, Class, ReleaseId`) via `RemoveType`, poi `NominalToBinary`, `ReplaceMissingValues`, `Standardize`, quindi feature selection ed eventuale bilanciamento. Questo isolamento è la garanzia principale contro il data leakage nel pre-processing.

### 2.3 Feature selection (fattore Sì/No)

Fattore attivabile/disattivabile: **CFS** (`CfsSubsetEval` + ricerca greedy `BestFirst` con backtracking), che seleziona un sottoinsieme di feature molto correlate col target ma poco correlate tra loro. È disponibile anche `InfoGain + Ranker` come alternativa filter. Essendo dentro il `FilteredClassifier`, la selezione avviene per-fold.

### 2.4 Bilanciamento delle classi

Confronto di quattro livelli: **nessuno**, **SMOTE**, **undersampling** (`SpreadSubsample -M 1.0`), **oversampling** (`Resample -B 1.0` con `-Z` calcolato per pareggiare le classi). L'obiettivo è un bilanciamento perfetto **1:1** tra `Buggy` yes/no; il balancing è l'ultimo anello della catena e agisce **solo sul training set**, lasciando al test la distribuzione reale.

### 2.5 Tecniche di validazione

Due tecniche, con ruoli diversi:

- **10×10-fold cross-validation** (10 ripetizioni di 10-fold), richiesta dalla specifica come confronto equo tra modelli.
- **Walk-forward** (across-release, ordinata per `ReleaseId`): training **cumulativo** (all'iterazione *n* si addestra sulle release 1…*n*−1), test sulla **singola release successiva** *n*, prima predizione sulla release 2 (`min-train-periods = 1`).

La walk-forward preserva l'ordine temporale ed evita di usare dati "futuri" per predire il "passato"; è la validazione **realistica** per la bug prediction. La cross-validation è mantenuta come **termine di paragone** per quantificare il bias temporale, non come performance attesa in produzione.

### 2.6 Metriche

Si riportano **Precision, Recall, F1, AUC, Cohen's Kappa** e **NPofB20**. Kappa misura quanto il modello supera un classificatore banale ed è la metrica di riferimento del corso. NPofB20 è la metrica *effort-aware*: si ordinano le classi per **densità di bug decrescente = P(bug) / LOC** e si misura la frazione di bug individuati ispezionando il **20% delle LOC totali** (normalizzazione di Falessi; effort = LOC). È la metrica prioritaria per la milestone.

### 2.7 Disegno sperimentale

Matrice **completa di 16 configurazioni** sulla variante principale: validazione {CV 10×10, walk-forward} × feature selection {nessuna, CFS} × balancing {nessuno, SMOTE, undersampling, oversampling}. Ogni run confronta i tre classificatori insieme (48 righe classificatore×config). In aggiunta, una **analisi di sensibilità** con la configurazione baseline (CV, no-FS, no-balancing) su tutti i 16 ARFF di M1.

## 3. Risultati e Research Questions

### RQ1 — Qual è il classificatore migliore?

**Random Forest**, in modo netto e consistente sotto entrambe le validazioni e su tutti i 16 dataset. In cross-validation raggiunge Kappa 0.87–0.88 e AUC 0.98; in walk-forward resta il migliore per Kappa (0.673). IBk è stabile secondo; Naïve Bayes è sistematicamente ultimo (Kappa 0.36–0.51).

**Leaderboard — cross-validation 10×10 (ottimistica)**

| Config | Classificatore | Kappa | AUC | NPofB20 |
|---|---|---|---|---|
| none + SMOTE | Random Forest | **0.880** | 0.982 | 0.893 |
| none + none | Random Forest | 0.871 | 0.982 | 0.895 |
| none + oversampling | Random Forest | 0.870 | 0.982 | 0.888 |
| none + none | IBk | 0.798 | 0.901 | 0.828 |
| CFS + SMOTE | Naïve Bayes | 0.514 | 0.834 | 0.752 |

**Leaderboard — walk-forward (realistica)**

| Config | Classificatore | Kappa | AUC | NPofB20 |
|---|---|---|---|---|
| none + oversampling | Random Forest | **0.673** | 0.856 | 0.574 |
| none + oversampling | IBk | 0.667 | 0.772 | 0.526 |
| none + none | IBk | 0.664 | 0.770 | 0.536 |
| none + SMOTE | Random Forest | 0.656 | 0.856 | 0.582 |
| none + none | Random Forest | 0.578 | 0.866 | **0.594** |

### RQ2 — Il bilanciamento aiuta?

Poco, e in modo dipendente dalla validazione. In cross-validation SMOTE dà a Random Forest un miglioramento marginale di Kappa (0.871 → 0.880); l'oversampling è equivalente al baseline. In walk-forward l'oversampling è invece decisivo per il Kappa di RF (0.578 → 0.673, +0.095). L'**undersampling è la tecnica più dannosa** in entrambe le validazioni (RF CV 0.729; degrada precision perché scarta troppa informazione della classe maggioritaria). Sul piano *effort-aware*, il balancing incide poco su NPofB20 (per RF senza FS resta nell'intervallo 0.574–0.594 in walk-forward).

### RQ3 — La feature selection (CFS) migliora?

**No.** CFS peggiora Random Forest e IBk sotto entrambe le validazioni (es. RF CV: Kappa 0.871 → 0.795; AUC 0.982 → 0.939). L'unico beneficiario è Naïve Bayes, il cui Kappa in CV sale da 0.36 a 0.51 grazie alla rimozione di feature correlate che ne violano l'assunzione di indipendenza. Il risultato è coerente con la natura euristica della feature selection: su un dataset già pulito e ben costruito, ridurre le feature abbassa l'accuratezza in cambio di minore complessità; per RF e IBk il **set completo è più informativo**.

### RQ4 — Qual è la configurazione finale raccomandata?

Con NPofB20 (walk-forward) come metrica prioritaria e Kappa (walk-forward) come criterio di compromesso, la scelta è **Random Forest, nessuna feature selection, oversampling**: Kappa 0.673 (massimo in walk-forward) con NPofB20 0.574. Tra le configurazioni RF senza FS l'NPofB20 varia poco (0.574–0.594) mentre il Kappa varia molto: l'oversampling massimizza il Kappa a fronte di un NPofB20 quasi invariato. La variante **senza balancing** (NPofB20 0.594, Kappa 0.578) è l'alternativa "minimale" citabile.

### Interpretazione dell'NPofB20

Un NPofB20 ≈ 0.57 in walk-forward significa che, ispezionando solo il **20% del codice (in LOC)**, il modello individua il **57% dei bug totali**. Un ordinamento casuale ne troverebbe circa il 20%: il modello offre quindi un guadagno quasi **triplo** rispetto al caso, un risultato pratico positivo per un revisore che deve allocare l'effort di ispezione.

## 4. Analisi di sensibilità

Sulla configurazione baseline (CV, no-FS, no-balancing) applicata a tutti i 16 dataset di M1, la gerarchia tra classificatori è invariata e Random Forest domina ovunque.

| Classificatore | Kappa medio | min–max | AUC medio | NPofB20 medio | pct20 | pct34 |
|---|---|---|---|---|---|---|
| Random Forest | 0.892 | 0.871–0.930 | 0.982 | 0.875 | 0.914 | 0.871 |
| IBk (k=1) | 0.814 | 0.752–0.868 | 0.910 | 0.822 | 0.852 | 0.775 |
| Naïve Bayes | 0.550 | 0.362–0.749 | 0.886 | 0.675 | 0.677 | 0.424 |

Le varianti `pct20` (20% delle release come training minimo) risultano leggermente più "facili" delle `pct34` (Kappa RF 0.914 vs 0.871): un training set più ampio include release più eterogenee e periodi più rumorosi, che alzano la difficoltà. L'effetto delle feature GitHub (`gh`) e del churn (`churn`) sui valori aggregati è di secondo ordine rispetto alla percentuale di release. Questi numeri, ottenuti in cross-validation, condividono l'ottimismo discusso alla sezione seguente.

## 5. Discussione

Il risultato più rilevante è il **divario tra cross-validation e walk-forward**: per Random Forest il Kappa scende da 0.88 a 0.67 e, soprattutto, l'NPofB20 crolla da ~0.89 a ~0.57. Questo comportamento è pienamente coerente con la letteratura di bug prediction: la cross-validation è ottimistica e irrealistica su dataset ordinati temporalmente, perché il rimescolamento dei fold consente al modello di usare informazioni del "futuro" per predire il "passato". L'effetto è amplificato da feature con forte legame temporale come `prevBuggy` e `prevNSmells`.

Ne deriva una lettura precisa del Kappa in cross-validation: un valore di **0.88 non va interpretato come "ottimo modello" ma come *red flag* di data leakage temporale**. In contesti reali di ingegneria del software i valori di Kappa attesi si collocano tipicamente tra 0.1 e 0.4; il nostro 0.88 è la prova sperimentale della necessità di preservare l'ordine dei dati per non sovrastimare le capacità del modello. La walk-forward, con Kappa ~0.67, resta comunque un risultato solido e onesto.

Una discrepanza interessante tra metriche riguarda l'oversampling in walk-forward: massimizza il Kappa ma non l'NPofB20, che è invece leggermente più alto nella configurazione senza balancing. Ciò conferma che le due metriche misurano cose diverse — accordo di classificazione binaria (Kappa) contro capacità di *ranking* per densità di bug (NPofB20) — e che la scelta della configurazione va motivata rispetto all'obiettivo prioritario. Infine, il fatto che la feature selection peggiori le performance è un esito sperimentale valido: indica che il dataset di M1 è già informativo e che per RF/IBk non vale la pena ridurne la dimensionalità.

## 6. Threats to Validity

**Specifici di M2.** *Leakage temporale residuo:* mitigato dall'isolamento per-fold della pipeline (`FilteredClassifier`) e dalla walk-forward cumulativa con test sempre su release futura; resta il rischio teorico legato a feature con memoria temporale. *Scelta del seed:* con seed unico (42) l'impatto della casualità su Random Forest e SMOTE non è quantificato (nessuna analisi di varianza su più seed). *Assenza di tuning:* si sono usati i parametri di default di Weka anziché ottimizzare gli iperparametri, il che può sottostimare il potenziale dei modelli. *Effort-aware limitato:* l'analisi si ferma alla soglia del 20% (NPofB20) invece di considerare l'intera curva PofB.

**Ereditati da M1.** Snoring residuo nell'etichettatura, uso della tecnica Proportion per stimare l'Injected Version, linkage rate ticket↔commit non unitario e copertura NSmells su 13 release su 14.

## 7. Conclusioni

Random Forest è il classificatore più efficace e stabile sul dataset AVRO. La configurazione raccomandata è Random Forest senza feature selection con oversampling, valutata in walk-forward (Kappa 0.673, NPofB20 0.574): ispezionando il 20% del codice si individua circa il 57% dei bug. La feature selection CFS non è utile per RF/IBk e il bilanciamento porta benefici modesti. Il contributo metodologico principale è la dimostrazione, sui dati, dell'ottimismo della cross-validation rispetto alla validazione walk-forward, che resta l'unica misura realistica per la bug prediction.

---

*Dati di supporto: `output/report/m2_matrix.csv` (48 righe, 16 configurazioni × 3 classificatori) e `output/report/m2_sensitivity.csv` (16 dataset). Registro delle decisioni: `docs/Decisioni_Milestone2.md`.*
