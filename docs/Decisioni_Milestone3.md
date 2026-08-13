# Decisioni — Milestone 3 (What-If Analysis / Actionable Defect Prediction)

> Registro passo-passo di **ogni** decisione, comprese quelle **obbligate** dalla specifica.
> Legenda stato: 🔒 obbligata (specifica/slide) · ✅ scelta (mia, con supporto NotebookLM) · 💡 consigliata (buona prassi) · ❓ da confermare.
> Progetto: `Milestone2WekaEvaluation`, package `com.milestone2.whatif`. Dataset di partenza (A): variante principale di M1 `pct34_total_gh0_churn0`.

## Metodo di lavoro
- Le scelte già prese si applicano passo-passo; nei dubbi si chiede a me o a NotebookLM.
- Si documenta ogni decisione (anche obbligata) con: **decisione**, **stato**, **motivazione**, **come è implementata** nel progetto.

---

## 1. Scopo della Milestone

Stimare l'**impatto preventivo di un'azione** (rimozione di code smell) sulla difettosità: quanti bug sarebbero stati evitati intervenendo su una **feature azionabile**. Non si misura più l'accuratezza (come in M2), ma si fa **inferenza** su dati reali e sintetici.

---

## 2. Decisioni metodologiche

### 2.1 Feature azionabile — ✅
**Decisione:** manipolare **NSmells** (numero di code smell per classe).
**Motivazione (NotebookLM):** deve essere una feature *actionable* — su cui lo sviluppatore ha controllo diretto — e *correlata al target*, altrimenti azzerarla non cambia la predizione. NSmells è la feature d'elezione (si può scrivere codice senza violazioni di manutenibilità); **non** sono azionabili LOC, numero di attributi, Age (ridurle richiederebbe di cambiare la funzionalità).
**Implementazione:** `Defaults.DEFAULT_WHAT_IF_FEATURE = "NSmells"`. `WhatIfFeatureSelector` sceglie NSmells se *zeroable*, altrimenti la feature numerica *zeroable* con **|correlazione|** massima col target; selezione forzabile via CLI `--what-if-feature`. Requisito *zeroable* (`Correlation.isZeroable`): la feature deve avere sia istanze a 0 sia istanze positive.

### 2.2 Costruzione dei dataset A / B+ / B / C — 🔒
**Decisione/definizione (NotebookLM, coerente col codice):**
- **A** — dataset originale completo (le release selezionate di M1, ~34%).
- **B+** — sottoinsieme di A con la feature azionabile **> 0** (classi che presentano lo smell).
- **B** — copia sintetica di B+ con la feature **forzata a 0** (scenario "smell rimosso").
- **C** — sottoinsieme di A con la feature **= 0** (gruppo di controllo, classi già senza smell).
**Confronto chiave:** **B+ vs B** misura l'efficacia teorica dell'intervento; la differenza di bug predetti è attribuibile alla presenza di quella feature.
**Implementazione:** `WhatIfDatasetBuilder.build(...)` — `subset(strictlyPositive=true)`=B+, `subset(false)`=C, `zeroedCopy(B+)`=B. B e B+ restano **appaiati per indice** (stessa cardinalità e ordine) → confronto a coppie corretto.

### 2.3 Classificatore e addestramento — ✅
**Decisione:** usare il **miglior classificatore di M2** (Random Forest), **addestrato sull'intero dataset A**, poi applicato in **inferenza** a B+, B, C.
**Motivazione (NotebookLM):** il modello migliore emerso dalla comparazione M2 con le sue configurazioni ottimali; l'inferenza sui quattro dataset produce i valori predetti da confrontare.
**Implementazione:** `WhatIfClassifierSelector` seleziona il best per **Kappa poi AUC** dai report M2 (o `--what-if-classifier`); `WhatIfPredictionService.evaluate` costruisce la pipeline `FilteredClassifier` (stesso preprocessing di M2), fa `buildClassifier(A)` e poi `distributionForInstance` su A/B+/B/C.
**❓ Da confermare in esecuzione:** allineare la config del preprocessing alla **best di M2** (RF, **nessuna FS**, **oversampling**); passare quindi `--feature-selection=none --balancing=oversampling` al run what-if.

### 2.4 Quantificazione dell'impatto — ✅
**Decisione:** impatto = **riduzione delle classi predette Buggy** passando da B+ a B, con i seguenti indicatori (già calcolati):
- **predictedRelievedCount** = istanze predette Buggy in B+ ma **non** in B → equivale a (Buggy_B+ − Buggy_B) a livello di istanza.
- **avoidableBuggyCount** = tra le "sollevate", quelle **realmente** Buggy → bug *evitabili*.
- **avoidableBuggyShare** = avoidableBuggy / (classi realmente Buggy in B+).
- **averagePositiveProbabilityReduction** = riduzione media di P(Buggy) tra B+ e B.
**Ruolo di C (NotebookLM):** le classi Buggy in **C** sono i **bug inevitabili** (unavoidable), che si manifestano indipendentemente dalla feature analizzata.
**Implementazione:** `WhatIfScenarioSummarizer.summarizeImpact` (coppie B+/B) + `summarize` per il conteggio Buggy attuale/predetto e P media di ciascun scenario (A/B+/B/C).

### 2.5 Validazione e metriche — 🔒
**Decisione:** in M3 **non** si usa la walk-forward; si esegue **inferenza** su dati esistenti e sintetici.
**Metriche attese (NotebookLM):** variazione assoluta dei bug predetti (B+→B), delta % di miglioramento, **proporzione di bug evitati** tra quelli potenzialmente evitabili (in B+). Opzionale: **percentuale di prevenzione globale** = bug evitati / bug totali in A. Opzionale: **test di Mann-Whitney** tra A e B per verificare che provengano dalla stessa popolazione (plausibilità del dataset sintetico).

---

## 3. Gap analysis — codice `whatif/` vs metodologia

**Già presente e conforme (nessun intervento):**
- Costruzione A/B+/B/C e appaiamento B+/B per indice (`WhatIfDatasetBuilder`, `WhatIfDatasetSet`).
- Feature azionabile = NSmells di default, con requisito *zeroable* e correlazione col target (`WhatIfFeatureSelector`, `Correlation.isZeroable`).
- Classificatore best per Kappa→AUC o esplicito; training su A + inferenza su B+/B/C (`WhatIfClassifierSelector`, `WhatIfPredictionService`).
- Impatto B+↔B: relieved, avoidable, share, riduzione media di probabilità (`WhatIfScenarioSummarizer`, `WhatIfImpactSummary`).
- Riepiloghi per scenario (conteggi Buggy attuali vs predetti, P media) e writer CSV (`WhatIfSummaryWriter`, `ScenarioSummary`).

**Gap da colmare:**
- **W1 — Config best di M2:** eseguire il what-if con RF + **no-FS** + **oversampling** (la config vincente in walk-forward), non con i default generici. *(parametro CLI in fase di run)*
- **W2 — Prevenzione globale su A:** oltre a `avoidableBuggyShare` (rapportata a B+), aggiungere il rapporto **bug evitati / bug totali in A** per l'impatto sull'intero progetto (indicato da NotebookLM). *(piccola aggiunta al summarizer/report)*
- **W3 — Test di Mann-Whitney (A vs B):** non implementato; valutare se aggiungerlo (opzionale secondo NotebookLM) per validare la plausibilità del dataset sintetico. ❓
- **W4 — Filtro azionabilità esplicito:** il selettore accetta qualsiasi feature *zeroable* correlata; con default NSmells è corretto, ma conviene **fissare `--what-if-feature=NSmells`** per evitare che, in assenza di NSmells, cada su una feature non azionabile. *(scelta a run-time)*
- **W5 — Report M3:** da produrre (tabella scenari + analisi impatto), vedi §5.

---

## 4. Threats to Validity — specifici di M3

- **Assunzione di invarianza:** azzerare NSmells assumendo che **tutte le altre feature restino identiche** ignora correlazioni causali (un refactoring reale potrebbe cambiare LOC, Churn, WMC). Il dataset B è quindi *sintetico* e ottimistico.
- **Causalità vs correlazione:** il modello ML cattura **correlazioni**, mentre la what-if assume implicitamente un **legame causale** tra feature azionabile e difettosità.
- **Bug inevitabili (C):** la quota di Buggy in C fissa un limite inferiore a ciò che l'intervento può prevenire; ignorarla sovrastimerebbe l'efficacia.
- **Dipendenza dal modello:** l'impatto stimato dipende dal classificatore scelto (RF) e dalla config di M2; un modello diverso darebbe numeri diversi.
- **(Ereditati M1/M2):** rumore nell'etichettatura (linkage 0.90, snoring, Proportion), copertura NSmells 13/14 release, seed unico.

---

## 5. Struttura del report M3 (NotebookLM)

1. **Metodologia** — costruzione di A, B, B+, C e processo di inferenza (training su A, predizione sui quattro dataset).
2. **Risultati** — tabella con classi Buggy **attuali vs predette** per ogni dataset (stile "slide 14" del materiale).
3. **Analisi dell'impatto** — grafici/tabelle con la **percentuale di difetti prevenibili** (B+→B) e la prevenzione globale su A.
4. **Discussione** — interpretazione **bug evitabili vs inevitabili** (ruolo di C).
5. **Threats to Validity** — §4 (invarianza, causalità vs correlazione).

---

## 6. Piano operativo (task)
- **M3-0** Gap-analysis del package `whatif/` *(questo documento)*. ✔️
- **M3-1** Eseguire il what-if su A=`pct34_total_gh0_churn0` con RF + no-FS + oversampling, feature=NSmells. ✔️
- **M3-2** Verificare gli output e aggiungere la prevenzione globale su A (W2). ✔️
- **M3-3** (opzionale) Test di Mann-Whitney A vs B (W3). ❓
- **M3-4** Report M3 (`.docx`, stile allineato a M1/M2) con tabelle e grafico d'impatto. *(in corso)*

---

## 8. Risultati M3 (esecuzione 20260813-101805)

Feature manipolata: **NSmells** (correlazione col target 0.427, la più alta tra le feature azionabili *zeroable*; B+ = 1875 = classi *smelly* di M1). Classificatore: **Random Forest** (config best M2: no-FS, oversampling), addestrato su A e applicato in inferenza.

| Scenario | Righe | Buggy reali | Buggy predette | P media |
|---|---|---|---|---|
| **A** (completo) | 3183 | 450 | 460 | 0.162 |
| **B+** (NSmells > 0) | 1875 | 413 | 420 | 0.246 |
| **B** (NSmells → 0) | 1875 | 413 | 383 | 0.193 |
| **C** (NSmells = 0 reale) | 1308 | 37 | 40 | 0.042 |

**Impatto B+ → B (rimozione degli smell):**
- Classi predette Buggy in meno: **420 − 383 = 37** (`predictedRelieved`).
- Di cui realmente buggy (**bug evitabili**): **31** (`avoidableBuggy`).
- Quota evitabile su B+ reali: 31/413 = **7.5%**.
- Riduzione media di P(Buggy): 0.246 → 0.193 = **−0.053**.

**Prevenzione globale su A (W2):**
- Su classi predette Buggy: 37/460 = **8.0%**.
- Su difetti reali: 31/450 = **6.9%**.

**Bug inevitabili (C):** 37 difetti reali si manifestano in classi **senza** smell = **8.2%** dei 450 difetti totali, non attribuibili a NSmells.

**Lettura:** rimuovere tutti i code smell ridurrebbe le classi segnalate come difettose dell'~8%, prevenendo circa **31 difetti reali (~7%)**; la maggior parte della difettosità non è attribuibile a NSmells (inclusi gli 37 bug in classi già pulite). Impatto reale modesto ma non trascurabile, coerente con la letteratura (gli smell spiegano solo una parte della difettosità).

Output: `output/whatif/what_if_summary.csv`, `output/whatif/feature_correlations.csv`; grafico scenari in `output/report/figs/whatif_scenarios.png`.

## 7. Punti aperti (❓)
1. ~~Config di preprocessing del run what-if = best di M2 (no-FS + oversampling)?~~ **RISOLTO:** sì, il run usa RF + `--feature-selection=none --balancing=oversampling`, feature `--whatif-feature=NSmells`, classificatore `--whatif-classifier=RANDOM_FOREST`.
2. Aggiungere Mann-Whitney (W3) o lasciarlo come nota nei threats?
3. ~~Fissare `--what-if-feature=NSmells` esplicito (W4)?~~ **RISOLTO:** sì, passato esplicitamente.

**Comando M3-1 (dalla root del progetto):**
```
.\run-analysis.cmd --data-dir=datasets\principal ^
  --class-attribute=Buggy --positive-class=yes ^
  --temporal-attribute=ReleaseId --size-attribute=LOC ^
  --validation=walk-forward --min-train-periods=1 ^
  --feature-selection=none --balancing=oversampling ^
  --whatif=true --whatif-feature=NSmells --whatif-classifier=RANDOM_FOREST ^
  --output-dir=output\whatif
```
