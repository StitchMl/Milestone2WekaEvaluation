# Decisioni — Milestone 2 (Classificazione con Weka)

> Registro passo-passo di **ogni** decisione, comprese quelle **obbligate** dalla specifica.
> Legenda stato: 🔒 obbligata (specifica/slide) · ✅ scelta (mia, con supporto NotebookLM) · 💡 consigliata (buona prassi) · ❓ da confermare.
> Progetto: `Milestone2WekaEvaluation` (Weka Java API 3.8.6). Dataset di input: i 16 ARFF prodotti in M1; variante principale `pct34_total_gh0_churn0`.

## Metodo di lavoro
- Le scelte già prese si applicano passo-passo; nei dubbi si chiede a me o a NotebookLM.
- Si documenta ogni decisione (anche obbligata) con: **decisione**, **stato**, **motivazione**, **come è implementata** nel progetto.

---

## 1. Decisioni metodologiche

### 1.1 Classificatori — 🔒
**Decisione:** confronto di **Random Forest**, **Naïve Bayes**, **IBk (k-NN)**.
**Motivazione:** i tre classificatori sono esplicitamente richiesti dalla slide degli obiettivi M2 (RF robusto/accurato, NB probabilistico con indipendenza delle feature, IBk non parametrico basato sulla vicinanza).
**Implementazione:** `classifiers.properties`. **Iperparametri fissati (NotebookLM → default Weka):**
- **IBk: `k=1`** (`-K 1`) — valore tipico del corso e default Weka; non è richiesto confrontare più k (il focus è tra classificatori diversi). *(era `-K 5` → da cambiare)*
- **Random Forest: default Weka**, in particolare **100 alberi** (`-I 100`); rimuovere il `-depth 15` (profondità illimitata di default). *(era `-I 50 -depth 15`)*
- **Naïve Bayes: default Weka**, nessuna modifica ai kernel estimator → **rimuovere `-K`**. *(era `-K`)*

Nessun classificatore aggiuntivo (Logistic/J48 scartati per attenersi alla specifica).

### 1.2 Feature selection — 💡 (fattore Sì/No)
**Decisione:** **CFS** (`CfsSubsetEval` + ricerca greedy `BestFirst` con backtracking) come fattore attivabile/disattivabile.
**Motivazione:** cerca un sottoinsieme di feature molto correlate col target ma poco correlate tra loro; raccomandata dai materiali del corso.
**Implementazione (no-leakage):** in `evaluation/Preprocessor` la FS è un filtro `weka.filters.supervised.attribute.AttributeSelection` inserito **dentro** un `FilteredClassifier` (equivalente all'`AttributeSelectedClassifier`): viene quindi **fittata solo sul training fold**, mai sul test → nessun data leakage. Livello `WRAPPER`=CFS+BestFirst; è disponibile anche `FILTER`=InfoGain+Ranker come alternativa, e `NONE`.
**Selezione:** CLI `--feature-selection=none|filter|wrapper`.

### 1.3 Bilanciamento delle classi — ✅ (confronto di tutte le tecniche)
**Decisione:** confrontare **quattro livelli**: `NONE`, **SMOTE**, **undersampling** (`SpreadSubsample`), **oversampling** (`Resample`).
**Motivazione:** con ~14% di classi `Buggy` il bilanciamento è necessario per non far ignorare la classe minoritaria; il confronto delle tre tecniche dà il massimo controllo intellettuale (scelta esplicita rispetto alle opzioni NotebookLM).
**Implementazione (no-leakage):** il filtro di balancing è l'ultimo anello del `FilteredClassifier` in `Preprocessor`, quindi agisce **solo sul training set**; il test resta con la distribuzione reale.
**Obiettivo fissato (NotebookLM): bilanciamento perfetto 1:1** tra `Buggy` yes/no nel training set.
- **Undersampling:** `SpreadSubsample -M 1.0` (rapporto 1:1) — già corretto.
- **Oversampling:** `Resample -B 1.0` per distribuzione uniforme; la percentuale `-Z` per pareggiare esattamente le classi è `Z = 100 · (maggioritaria − minoritaria) / minoritaria`, quindi **dipende dal dataset** (per la principale ≈ 507%). Da rendere calcolata per-dataset anziché fissa `-Z 200`.
- **SMOTE:** applicato fino a parità tra le classi (percentuale tale da raggiungere il 1:1).
**Selezione:** CLI `--balancing=none|smote|undersampling|oversampling`.

### 1.4 Tecnica di validazione — 🔒 + 💡
**Decisione:** **10×10-fold cross-validation** (10 ripetizioni di 10-fold) come tecnica di confronto principale, **più** una validazione **walk-forward** (across-release, ordinata per `ReleaseId`) come verifica finale realistica.
**Motivazione:** la 10×10 CV è esplicitamente richiesta dalle slide M2 per confrontare i modelli in modo equo; la walk-forward è preferibile nella bug prediction perché preserva l'ordine temporale ed evita di usare il futuro per predire il passato (nessun leakage temporale).
**Parametri walk-forward fissati (NotebookLM):** training **cumulativo** (all'iterazione n si addestra su tutte le release da 1 a n−1), test sulla **singola release successiva** n; prima predizione sulla **release 2** → `--min-train-periods=1`.
**Implementazione:** `crossvalidation/CrossValidationExecutor` (k-fold ripetuta, parallela) selezionata con `--validation=cross-validation --runs=10 --folds=10`; `validation/timeseries/WalkForwardValidationExecutor` con `--validation=walk-forward --temporal-attribute=ReleaseId --min-train-periods=1`. *(Da verificare in M2-3 che la finestra sia cumulativa e non scorrevole.)*

### 1.5 Metriche di valutazione — ✅ (set completo)
**Decisione:** riportare **Precision, Recall, F1, AUC, Cohen's Kappa, NPofB20**.
**Motivazione:** Precision/Recall/F1 per l'efficacia sulla classe positiva; AUC perché indipendente dalla soglia; **Kappa** è la metrica fondamentale del corso (quanto il modello supera un classificatore banale); **NPofB20** è la metrica effort-based specifica del corso (probabilità normalizzata di trovare bug ispezionando il 20% del codice più rischioso).
**NPofB20 — criterio di ranking fissato (Falessi/NotebookLM):** ispezione del **20% delle LOC totali** del dataset; il ranking **NON** è per sola probabilità ma per **densità di bug decrescente = P(bug) / LOC** (maggior ritorno di bug per riga letta). Effort/costo = **LOC** della classe.
**Implementazione:** `metric/NPofB20Calculator` + `prediction/RankedPredictionFactory`, costo/effort via `--size-attribute=LOC` (NaN se manca). AUC e Kappa dall'`Evaluation` di Weka.
**⚠️ Correzione necessaria (M2-4):** `RankedPredictionFactory` ordina attualmente per **sola probabilità decrescente** (`Comparator.comparingDouble(getProbability).reversed()`) → questo calcola la **PofB20**, non la NPofB20. Va cambiato in ordinamento per **densità `probability / max(LOC,1)`** decrescente.

### 1.6 Matrice degli esperimenti — 🔒/💡
**Decisione:** esperimento **completo** sulla variante principale `pct34_total_gh0_churn0`, incrociando **classificatore (3) × feature selection (Sì/No) × balancing (4 livelli)**; le altre 15 varianti M1 usate come **analisi di sensibilità** nel report.
**Motivazione:** la specifica chiede una tabella comparativa dei fattori sulla variante principale; le altre servono a discutere come cambiano le performance al variare dei parametri di M1.
**Implementazione:** un run del jar confronta già i 3 classificatori insieme; serve un **driver** che iteri FS(2) × balancing(4) = 8 run (→ 24 righe classificatore×config) e aggreghi la tabella. *(gap, vedi §2)*.

### 1.7 Strumento — 💡
**Decisione:** **Weka Java API** (progetto `Milestone2WekaEvaluation`), non Weka Explorer manuale.
**Motivazione:** riproducibilità della pipeline e calcolo di metriche avanzate (NPofB20, walk-forward); il controllo intellettuale richiesto si esprime meglio con l'automazione. Explorer resta utile solo per analisi preliminari.

### 1.8 Parametri di dominio del dataset — 🔒
**Decisione:** attributo target `Buggy`, classe positiva `yes`, attributo temporale `ReleaseId`, attributo di size/effort `LOC`; gli identificativi (`Project, Path, Class, ReleaseId`) sono `string` e vengono rimossi nel pipeline (`RemoveType -T string`) prima dell'addestramento.
**Motivazione:** coerenza con lo schema ARFF di M1; gli ID non hanno valore predittivo. `ReleaseId` è però necessario **prima** del pipeline per il partizionamento temporale walk-forward.
**Implementazione:** CLI `--class-attribute=Buggy --positive-class=yes --temporal-attribute=ReleaseId --size-attribute=LOC`.

---

## 2. Gap analysis — stato del progetto esistente vs metodologia

**Già presente e conforme (nessun intervento):**
- Pipeline **leakage-safe** (`FilteredClassifier` fittato per-fold) con ordine: RemoveType(string) → NominalToBinary → ReplaceMissingValues → Standardize → FS → balancing.
- FS: CFS+BestFirst (`WRAPPER`) e InfoGain (`FILTER`); Balancing: NONE/SMOTE/undersampling/oversampling — tutti selezionabili da CLI.
- Validazione: 10×10 CV parallela **e** walk-forward temporale (`ReleaseId`).
- Metriche: Precision/Recall/F1/AUC/Kappa + **NPofB20** (effort = size-attribute).
- Selezione vincitori per metrica e complessivo (Kappa poi AUC), export CSV, grafici.

**Gap da colmare:**
- **G1 — Wiring dataset:** far girare la pipeline sui 16 ARFF di M1 (`--data-dir` → cartella ARFF), principale `pct34_total_gh0_churn0`, con `--size-attribute=LOC`. Attualmente ha girato su dataset segnaposto (`A.arff`…).
- **G2 — Driver matrice fattori:** manca l'orchestrazione che spazzi FS(2)×balancing(4) e aggreghi la tabella comparativa unica (oggi ogni combinazione è un'invocazione separata del jar). Da aggiungere uno script/driver.
- **G3 — Doppia validazione:** eseguire e salvare **entrambe** 10×10 CV e walk-forward per il confronto e la verifica finale.
- **G4 — Verifica output:** confermare che AUC/Kappa/NPofB20 siano popolati nelle righe per-classificatore sui nostri dati (i "METRIC_WINNER" mostravano Kappa/AUC vuoti solo perché relativi a metriche diverse).
- **G5 — Config classificatori (valori fissati):** `classifiers.properties` → IBk `-K 1`, RF `-I 100` (togliere `-depth 15`), NB senza `-K`. Balancing 1:1 con `Resample -Z` calcolato per-dataset.
- **G6 — NPofB20 (correzione ranking):** cambiare `RankedPredictionFactory` da ordinamento per probabilità a ordinamento per **densità `P/LOC`** decrescente (vedi §1.5).
- **G7 — Sensibilità:** dopo la principale, eseguire (almeno in forma ridotta) le altre 15 varianti per la sezione di sensibilità del report.
- **G8 — What-if (M3):** i dataset A/B/B+/C in `whatif/` appartengono alla Milestone 3; si mantengono ma non sono centrali per M2.

---

## 3. Piano operativo (task)
- **M2-0** Review/gap-analysis del progetto *(questo documento)*. ✔️
- **M2-1** Collegare gli ARFF di AVRO (principale + 15) e i parametri di dominio. ✔️
- **M2-2** Confermare/ordinare i fattori FS e balancing (no-leakage) e i parametri. ✔️
- **M2-3** Validazione 10×10 CV + walk-forward. ✔️
- **M2-4** Metriche complete + NPofB20; driver della matrice e tabella comparativa; sensibilità. ✔️
- **M2-5** Valutazione risultati (§6) e stesura report M2 *(in corso)*.

**Stato gap:** G1–G6 chiusi (config classificatori applicata, NPofB20 corretta per densità, wiring sui 16 ARFF, doppia validazione). G7 (sensibilità 16 varianti) eseguita in CV. G8 (what-if M3) rinviato.

## 4. Punti aperti — RISOLTI (NotebookLM)
1. **IBk k = 1** (default Weka, tipico del corso; nessun confronto di k richiesto).
2. **Balancing 1:1**: `SpreadSubsample -M 1.0`; `Resample -B 1.0` con `-Z = 100·(maj−min)/min` calcolato per-dataset; SMOTE fino alla parità.
3. **Walk-forward**: `--min-train-periods=1`, training **cumulativo** (1..n−1), test sulla release n, prima predizione sulla release 2.
4. **RF/NB**: default Weka (RF 100 alberi; NB senza kernel).
5. **NPofB20**: effort = LOC, soglia 20% delle LOC totali, ranking per **densità P(bug)/LOC** decrescente (normalizzazione di Falessi).

---

## 5. Esecuzione della matrice e risultati

**Eseguito il 2026-07-30.** Matrice completa di **16 configurazioni** sulla variante principale `pct34_total_gh0_churn0` = validazione{CV 10×10, walk-forward} × FS{nessuna, CFS wrapper} × balancing{nessuno, SMOTE, undersampling, oversampling}; più **sensibilità** con la config baseline (CV, no-FS, no-balancing) su tutti i 16 ARFF. Seed unico = 42. Output completo in `output/report/m2_matrix.csv` e `output/report/m2_sensitivity.csv`.

### 5.1 Leaderboard per Kappa — cross-validation 10×10 (ottimistica)
| Config | Classificatore | Kappa | AUC | NPofB20 |
|---|---|---|---|---|
| none + SMOTE | Random Forest | **0.880** | 0.982 | 0.893 |
| none + none | Random Forest | 0.871 | 0.982 | 0.895 |
| none + oversampling | Random Forest | 0.870 | 0.982 | 0.888 |
| none + none | IBk | 0.798 | 0.901 | 0.828 |
| CFS + SMOTE | Naïve Bayes | 0.514 | 0.834 | 0.752 |

### 5.2 Leaderboard per Kappa — walk-forward (realistica)
| Config | Classificatore | Kappa | AUC | NPofB20 |
|---|---|---|---|---|
| none + oversampling | Random Forest | **0.673** | 0.856 | 0.574 |
| none + oversampling | IBk | 0.667 | 0.772 | 0.526 |
| none + none | IBk | 0.664 | 0.770 | 0.536 |
| none + SMOTE | Random Forest | 0.656 | 0.856 | 0.582 |
| none + none | Random Forest | 0.578 | 0.866 | **0.594** |

### 5.3 Sensibilità (16 ARFF, baseline CV) — Kappa medio
| Classificatore | Kappa medio | min–max | AUC medio | NPofB20 medio | pct20 | pct34 |
|---|---|---|---|---|---|---|
| Random Forest | 0.892 | 0.871–0.930 | 0.982 | 0.875 | 0.914 | 0.871 |
| IBk (k=1) | 0.814 | 0.752–0.868 | 0.910 | 0.822 | 0.852 | 0.775 |
| Naïve Bayes | 0.550 | 0.362–0.749 | 0.886 | 0.675 | 0.677 | 0.424 |

### 5.4 Configurazione finale selezionata — ✅
**Decisione:** **Random Forest, nessuna feature selection, oversampling, valutata in walk-forward** (Kappa 0.673, NPofB20 0.574).
**Motivazione (NotebookLM):** la metrica prioritaria è **NPofB20** (effort-aware) valutata in **walk-forward** (unica validazione realistica). Tra le config RF senza FS l'NPofB20 in walk-forward varia poco (0.574–0.594) mentre il Kappa varia molto (0.578–0.673): l'**oversampling** massimizza il Kappa (+0.095 vs baseline) al costo di un NPofB20 quasi invariato (−0.020) → miglior compromesso Kappa↔NPofB20. La riga `none+none` (NPofB20 0.594, Kappa 0.578) resta come alternativa "senza balancing" da citare.

---

## 6. Valutazione dei risultati (NotebookLM)

1. **Divario CV↔walk-forward (Kappa 0.88 → 0.67, NPofB20 0.89 → 0.57).** Pienamente coerente con la letteratura: la cross-validation è *ottimistica/irrealistica* su dataset ordinati temporalmente perché lascia usare dati del "futuro" per predire il "passato"; l'effetto è amplificato da feature come `prevBuggy`/`Age` che creano un forte legame temporale.
2. **Validazione ufficiale = walk-forward** (approccio time-series, ordine preservato). La CV si mantiene nel report **solo come termine di paragone** per dimostrare l'entità del bias temporale, **non** come performance attesa in produzione.
3. **Kappa 0.88 in CV = red flag di data leakage temporale.** In contesti reali di ingegneria del software i valori attesi di Kappa stanno tipicamente tra **0.1 e 0.4**; il nostro 0.88 va argomentato come *prova* della necessità di preservare l'ordine dei dati per non sovrastimare il modello.
4. **CFS che peggiora = risultato sperimentale valido.** La feature selection è un'euristica: se il dataset di partenza è già pulito e ben costruito, rimuovere feature può ridurre l'accuratezza in cambio di minore complessità. Si può citare InfoGain come confronto rapido, ma va soprattutto giustificato **perché il set completo è più informativo** per RF/IBk.
5. **Metrica prioritaria = NPofB20 (effort-aware).** Ordinare correttamente le classi per densità di bug è più utile al professionista della semplice accuratezza binaria; la config migliore massimizza il compromesso tra **Kappa (walk-forward)** e **NPofB20**.
6. **NPofB20 ≈ 0.57 è molto positivo:** ispezionando solo il **20% del codice (LOC)** si identifica il **57% dei bug**; un modello casuale ne troverebbe il 20% → guadagno quasi **triplo**.

---

## 7. Threats to Validity — specifici di M2

- **Leakage temporale residuo:** rischio se filtri/FS/balancing non fossero isolati nel training fold. *Mitigazione:* pipeline `FilteredClassifier` fittato per-fold; walk-forward cumulativo (test sempre su release futura).
- **Scelta del seed:** l'impatto della casualità su RF e SMOTE con **seed unico = 42** non è quantificato (nessuna analisi di varianza su più seed).
- **Tuning assente:** uso dei **parametri di default** di Weka invece di un'ottimizzazione degli iperparametri (RF 100 alberi, IBk k=1, NB senza kernel) → possibile sottostima del potenziale dei modelli.
- **Effort-aware limitato:** analisi ristretta alla **soglia del 20%** (NPofB20) invece dell'intera curva PofB.
- **(Ereditati da M1):** snoring residuo, tecnica Proportion per l'IV, linkage rate ticket↔commit, copertura NSmells 13/14 release.

---

## 8. Struttura del report M2 e presentazione (NotebookLM)

**Sezioni del report finale:**
1. **Introduzione** — richiamo agli obiettivi della milestone.
2. **Metodologia** — descrizione granulare di classificatori, filtri (FS, balancing) e tecnica di validazione.
3. **Research Questions / Risultati** — risposte puntuali (es. "Qual è il classificatore più accurato?", "Il balancing ha aiutato?", "CFS migliora?").
4. **Analisi di sensibilità** — come cambiano i risultati al variare dei parametri di M1 (snoring, Proportion, pct20 vs pct34, gh, churn).
5. **Discussione** — interpretazione (sorprese, discrepanze tra metriche, divario CV↔WF).
6. **Threats to Validity** — §7.

**Presentazione risultati:**
- **Tabella comparativa** completa sulla variante principale (le 16 config) — obbligatoria.
- **Appendice:** le altre 15 varianti come analisi di sensibilità.
- **Grafici:** *boxplot* del Kappa per classificatore (stabilità tra run/fold) e *grafici a barre* per il confronto delle metriche; opzionale curva NPofB.
