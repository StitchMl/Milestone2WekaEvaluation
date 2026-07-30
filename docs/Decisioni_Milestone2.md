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
- **M2-0** Review/gap-analysis del progetto *(questo documento)*.
- **M2-1** Collegare gli ARFF di AVRO (principale + 15) e i parametri di dominio.
- **M2-2** Confermare/ordinare i fattori FS e balancing (no-leakage) e i parametri (❓).
- **M2-3** Validazione 10×10 CV + walk-forward.
- **M2-4** Metriche complete + NPofB20; driver della matrice e tabella comparativa; sensibilità.

## 4. Punti aperti — RISOLTI (NotebookLM)
1. **IBk k = 1** (default Weka, tipico del corso; nessun confronto di k richiesto).
2. **Balancing 1:1**: `SpreadSubsample -M 1.0`; `Resample -B 1.0` con `-Z = 100·(maj−min)/min` calcolato per-dataset; SMOTE fino alla parità.
3. **Walk-forward**: `--min-train-periods=1`, training **cumulativo** (1..n−1), test sulla release n, prima predizione sulla release 2.
4. **RF/NB**: default Weka (RF 100 alberi; NB senza kernel).
5. **NPofB20**: effort = LOC, soglia 20% delle LOC totali, ranking per **densità P(bug)/LOC** decrescente (normalizzazione di Falessi).
