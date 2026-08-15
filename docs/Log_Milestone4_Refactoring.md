# Log Milestone 4 — Refactoring LLM (tracciamento completo prompt/output/metriche)

Regola di documentazione: per **ogni** variante si registra branch, **modello e versione Copilot**, **prompt esatto**, **output Copilot** (la classe rifattorizzata è salvata in `docs/m4_variants/<Classe>_<Cx>.java` oltre che committata sul branch), le **metriche di verifica** (compila, test verdi, mutation score, smell residui, complexity/LOC) e le **note comportamentali** (differenze osservate rispetto a C0).

Baseline C0 (riferimento per i delta):

| Classe | Smell | Test verdi | Mutation | Line cov | complexity(≈WMC) | LOC(ncloc) |
|---|---|---|---|---|---|---|
| GenericData | 15 | 186/186 | 83% (242/296 classe) | 87% | 136* | 633* |
| JsonDecoder | 3 | (incluse) | 86% (88/102 classe) | — | 78* | 424* |

\* WMC/LOC da CK/M1; `complexity`/`ncloc` verranno riletti da SonarCloud per confronto omogeneo.

---

## GenericData — Variante C1 (solo codice)

- **Branch:** `m4-c1-genericdata` (da `m4-genericdata` = C0).
- **Modello/versione Copilot:** `mai-code-1.1-flash` (~5.1 credits/run).
- **Output Copilot:** `docs/m4_variants/GenericData_C1.java` (compila: `mvn -q -DskipTests compile` OK, confermato dall'utente).

**Prompt esatto usato:**
> Refactor this Java class to remove all SonarSource (SonarCloud) code smells, following clean-code best practices. Constraints: do NOT change the public API or any observable behavior; keep it compilable on Java 8; do not add external dependencies; keep the nested classes (Record, Array, Fixed, EnumSymbol). Focus on: reducing Cognitive Complexity of `validate`, `toString`, `writeEscapedString`, `induce`, `compare` (extract private helpers); adding missing `@Override`; removing the unused `name` parameters and the dead `hex` variable; renaming the `record`/`enu` identifiers that clash with restricted keywords; extracting the nested ternary in `compare`. Return the full refactored class.

**Note comportamentali (analisi statica pre-run):**
- **Cambio di comportamento non coperto dall'oracolo:** in `writeEscapedString`, il ramo caratteri di controllo/unicode dell'originale era **buggato** (`builder.append(string.toUpperCase())` — l'intera stringa — e padding su `4-builder.length()`). Il refactoring (`appendUnicodeEscape`) produce il corretto `\uXXXX` (hex, padding a 4). Quindi l'output di `toString` per stringhe con `–`, `–`, ` –⃿` **cambia**. I test `mt` sugli escape coprono solo i casi puliti → i 186 test restano verdi pur essendoci una modifica funzionale reale. → Threat: *implementation dependence / coverage gap*; l'LLM ha "corretto" un bug latente senza che l'oracolo lo rilevi.
- Split di `validate`/`toString`/`induce`/`compare` in helper privati: fedele.
- API preservata: `record`/`name` restano come **parametri** di `setField`/`getField` (arità invariata) → i relativi `S6213`/`S1172` probabilmente **non** si azzerano.

**Metriche di verifica C1 (run 2026-08-14 11:32):**

| Metrica | C0 | C1 | Δ |
|---|---|---|---|
| Compila | sì | sì | = |
| Test verdi | 186/186 | 186/186 | = (nessuna regressione nell'oracolo) |
| Mutation (progetto, GenericData+JsonDecoder) | 330/398 = 83% | 361/436 = 83% | = (suite robusta al refactoring) |
| Test strength | 91% | 91% | = |
| Smell SonarCloud (GenericData) | 15 | **7** | **−8** |
| complexity (Sonar) | n/d* | 256 | — |
| ncloc (Sonar) | n/d* | 709 | — |
| WMC / LOC (CK, riferimento) | 136 / 633 | (da CK sul variant) | LOC↑ atteso |

\* C0 non scansionato per `complexity`/`ncloc` Sonar; confronto omogeneo richiede stesso tool. ncloc C1=709 > LOC(CK C0)=633: aumento atteso dall'estrazione di helper.

**Esito/giudizio C1:** **successo parziale.** Compila; nessuna regressione nell'oracolo (186 verdi); smell **dimezzati** (15→7) ma non azzerati (restano identificatori/parametri legati alla firma pubblica); mutation score invariato (83%) → i test non erano ancorati alla struttura interna. **Caveat comportamentale:** `writeEscapedString` cambia output sui caratteri di controllo (bug latente corretto), non rilevato dai test → equivalenza NON piena. LOC in aumento (helper) da pesare contro il calo smell.

**Regole smell residue C1 (7):** `java:S6213` ×4 (identificatore ristretto `record`, righe 301/386/598/605), `java:S1172` ×2 (parametro `fieldName` inutilizzato in `setField`/`getField`, righe 598/605), `java:S1161` ×1 (manca `@Override` su `Array.getSchema`, riga 109).
- Rimossi da C1 (8): 5×`S3776` (complessità), `S1481`+`S1854` (var `hex`), `S3358` (ternario annidato).
- Non risolti: gli identificatori/parametri legati alla **firma pubblica** (`record`, `fieldName`) — l'LLM ha rinominato `enu`→`enumSymbol` e il `record` di `getRecordSchema`→`recordData`, ma ha **reintrodotto** `record` nel nuovo helper `appendRecordToString` e l'ha lasciato in `setField`/`getField`; inoltre non ha aggiunto `@Override` a `Array.getSchema`.

---

## GenericData — Variante C2 (codice + BB)

- **Branch:** `m4-c2-genericdata` (da `m4-genericdata` = C0).
- **Modello/versione Copilot:** `mai-code-1.1-flash`.
- **Contesto aggiunto:** i 2 file BB (`TestGenericDataBB.java`, `TestGenericDataStructures.java`) oltre al sorgente.
- **Prompt esatto:** prompt C2 di `Prompt_Copilot_M4.md` (rimuovi smell, preserva API/comportamento, i test BB come specifica da tenere verdi).
- **Output Copilot:** `docs/m4_variants/GenericData_C2.java`.

**RISULTATO CHIAVE — output byte-identico a C1.** Verificato con diff normalizzato (rimozione whitespace): C1 e C2 coincidono (md5 identici degli archivi). Aggiungere i test Black-box al contesto **non ha modificato** il refactoring prodotto da `mai-code-1.1-flash`.

Implicazioni:
- Metriche C2 = C2 = C1: 186/186 test verdi, smell 15→7 (stesse 7 regole residue), mutation 83%, stesso cambio di comportamento in `writeEscapedString` (bug latente corretto). *(Metriche non ri-misurate perché il codice è identico byte-a-byte; se serve per il report si può ri-eseguire per conferma.)*
- **Finding:** l'ipotesi "più contesto di test ⇒ refactoring diverso/migliore" (attesa NotebookLM) è **falsificata** per questo modello/classe nel passaggio C1→C2. In particolare i test BB nel contesto **non** hanno impedito la correzione del bug di `writeEscapedString` — coerente col fatto che i test BB non coprono il ramo dei caratteri di controllo.

**Metriche C2:**

| Metrica | C0 | C2 | Δ vs C0 |
|---|---|---|---|
| Test verdi | 186/186 | 186/186 (= C1) | = |
| Mutation | 83% | 83% (= C1) | = |
| Smell | 15 | 7 (= C1) | −8 |
| Note | — | identico a C1 | contesto BB ininfluente |

## GenericData — Variante C3 (codice + BB + cf)

- **Branch:** `m4-c3-genericdata` (commit `8b0de6e9c`). **Modello:** `mai-code-1.1-flash`.
- **Contesto aggiunto:** BB + cf (`TestGenericDataCoverage.java`) oltre al sorgente.
- **Output Copilot:** `docs/m4_variants/GenericData_C3.java`.
- **Diff autorevole C1↔C3 (normalizzato):** DIVERSI ma solo per 2 elementi — `induceArray`→**`induceCollection`** (rename) e nuovo helper **`flipIfDescending`** estratto da `compareRecord`. Nessun'altra differenza di sostanza; `writeEscapedString` resta corretto (bug fix) come in C1.

**Metriche C3 (run 2026-08-15 21:15):**

| Metrica | C0 | C3 | Δ vs C0 |
|---|---|---|---|
| Test verdi | 186/186 | 186/186 | = |
| Mutation (progetto) | 330/398 = 83% | 362/437 = 83% | = |
| Test strength | 91% | 91% | = |
| Smell (GenericData) | 15 | **7** | −8 |
| complexity | n/d | 257 | ≈ C1 (256) |
| ncloc | n/d | 712 | ≈ C1 (709) |

**Smell residui C3 (7):** identici a C1 — 4×`S6213` (`record`), 2×`S1172` (`fieldName`), 1×`S1161` (`@Override` mancante su `Array.getSchema`). I 2 refactor extra non toccano queste regole.

**Esito/giudizio C3:** successo parziale = **equivalente a C1**. Il contesto `cf` ha prodotto un output *diverso nella forma* ma con **identico esito** (smell 7, comportamento preservato entro l'oracolo, mutation invariato). Contrasto interessante con C2 (byte-identico a C1): C2 uguale, C3 diverso-ma-equivalente. **Finding cumulativo C1→C2→C3:** aumentare il contesto di test **non** ha migliorato il risultato (smell fermi a 7, stesso bug fix silente in `writeEscapedString`) per `mai-code-1.1-flash` su questa classe.

## GenericData — Variante C4 (codice + BB + cf + mt)

- **Branch:** `m4-c4-genericdata` (commit `29d8f20c5`). **Modello:** `mai-code-1.1-flash`.
- **Contesto aggiunto:** BB + cf + mt (tutti e tre i tier) oltre al sorgente.
- **Output Copilot:** `docs/m4_variants/GenericData_C4.java`.
- **Diff C3->C4 (normalizzato):** differisce solo per il rename dell'helper `flipIfDescending`->`applyFieldOrder` (2 occorrenze) + formattazione multi-riga della chiamata `compare`. Nessuna differenza di sostanza; `writeEscapedString` resta corretto (bug fix) come C1-C3.

**Metriche C4 (run 2026-08-15 21:25):**

| Metrica | C0 | C4 | Delta vs C0 |
|---|---|---|---|
| Test verdi | 186/186 | 186/186 | = |
| Mutation (progetto) | 330/398 = 83% | 362/437 = 83% | = |
| Test strength | 91% | 91% | = |
| Smell (GenericData) | 15 | **7** | -8 |
| complexity | n/d | 257 | ~ C1/C3 |
| ncloc | n/d | 712 | ~ C1/C3 |

**Smell residui C4 (7):** identici a C1/C3 - 4x`S6213` (`record`), 2x`S1172` (`fieldName`), 1x`S1161` (`Array.getSchema`).

**Esito/giudizio C4:** successo parziale, **equivalente a C1/C3**. Il tier `mt` nel contesto non ha cambiato l'esito ne impedito il bug fix silente di `writeEscapedString`.

---

## SINTESI GenericData (C0 -> C1-C4)

| Variante | Contesto | Smell | Test | Mutation | ncloc | Note |
|---|---|---|---|---|---|---|
| **C0** | baseline | 15 | 186 ok | 83% | ~633 (CK) | - |
| **C1** | codice | **7** | 186 ok | 83% | 709 | bug fix silente writeEscapedString |
| **C2** | +BB | **7** | 186 ok | 83% | 709 | **byte-identico a C1** |
| **C3** | +BB+cf | **7** | 186 ok | 83% | 712 | +induceCollection, flipIfDescending (equivalente) |
| **C4** | +BB+cf+mt | **7** | 186 ok | 83% | 712 | +applyFieldOrder (equivalente) |

**Conclusioni GenericData (per il report):**
1. **Riduzione smell**: 15->7 (-53%) gia in C1 (solo codice). I 7 residui sono **strutturalmente legati alla firma pubblica** (parametri `record`/`fieldName` non rimovibili senza rompere l'API - S6213/S1172) e un `@Override` mancante (S1161): l'LLM non li tocca perche rispetta il vincolo "non cambiare l'API".
2. **Contesto di test ininfluente**: aumentando il contesto (C1->C4) l'esito **non migliora** (sempre 7 smell). C2 e byte-identico a C1; C3/C4 differiscono solo per rename/estrazioni cosmetiche -> smentisce, per `mai-code-1.1-flash` su questa classe, l'ipotesi "piu test nel prompt => refactoring migliore".
3. **Comportamento non pienamente preservato**: `writeEscapedString` viene "corretto" (bug latente sui caratteri di controllo) in **tutte** le varianti; i 186 test restano verdi perche l'oracolo non copre quel ramo -> threat *coverage gap / implementation dependence*.
4. **Non peggioramento metriche**: mutation score e test strength invariati (83%/91%); ncloc cresce (~+12%) per l'estrazione di helper - trade-off atteso.

## JsonDecoder — Variante C1 (solo codice)

- **Branch:** `m4-c1-jsondecoder` (commit `586d217d2`). **Modello:** `mai-code-1.1-flash`.
- **Output Copilot:** `docs/m4_variants/JsonDecoder_C1.java` (scritto sul branch via assistente per evitare problemi di salvataggio).
- **Prompt esatto:** prompt C1 (rimuovi smell, preserva API/comportamento, Java 8; return diretto invece di `result`; costante per il letterale "fixed"; riduci complessità di `doAction` con helper).

**Modifiche introdotte (fedeli, nessun bug fix silente):**
- `readByteArray` ritorna direttamente `in.getText().getBytes(CHARSET)` → risolve `S1488`.
- Costante `FIXED_TYPE = "fixed"` sostituisce le 3 occorrenze del letterale → risolve `S1192`.
- `doAction` spezzato in `handleFieldAdjustAction` / `handleRecordStart` / `handleRecordOrUnionEnd` → risolve `S3776`.
- Flatten di alcune `if/else` in `else if` (readString/skipString/read*): comportamento invariato.

**Metriche C1 JsonDecoder:**

| Metrica | C0 | C1 | Δ |
|---|---|---|---|
| Smell (SonarCloud) | 3 | **0** (bestValue=true) | **−3, ZERO** |
| complexity | (n/d C0) | 85 | — |
| ncloc | (n/d C0) | 370 | — |
| Test verdi | 186/186 | **186/186** | = |
| Mutation (progetto) | 83% | 333/401 = **83%** | = |
| Test strength | 91% | 91% | = |

**Nota build:** i branch JsonDecoder non avevano i fix ai `pom.xml` (source 1.8 + JaCoCo/PIT), presenti solo sui branch GenericData → il primo `mvn clean test` è fallito ("Source option 6"). Fix portato via `git checkout m4-genericdata -- lang/java/pom.xml lang/java/avro/pom.xml`. Lo `sonar-scanner` è source-only e ha comunque dato smell **0**.

**Esito/giudizio C1 JsonDecoder:** **successo pieno (3→0 smell, 186/186 verdi, mutation 83% invariato)**, a differenza di GenericData (7 residui). Motivo: i 3 smell di JsonDecoder **non** erano legati alla firma pubblica (letterale duplicato, variabile temporanea, complessità cognitiva) → tutti eliminabili senza toccare l'API. Comportamento fedele (nessuna correzione silente). Confermato dopo aver portato pom + test suite sul branch (commit `af16dda5c` build-fix, `469c2713c` test).

**Nota metodologica (per il report):** i branch JsonDecoder (`m4-jsondecoder` e figli) erano nati da `release-1.5.4` **prima** dell'infrastruttura M4-2, quindi privi dei fix `pom.xml` (source 1.8, JaCoCo/PIT, Surefire override) e delle test suite `com.milestone4.*`. Portati da `m4-genericdata` via `git checkout <branch> -- <path>`. Base `m4-jsondecoder` sistemata (commit `c2fd80777` pom, `dd88934e8` test) così le varianti C2–C4 li ereditano.

## JsonDecoder — Variante C2 (codice + BB)

- **Branch:** `m4-c2-jsondecoder` (commit `4b6eba801`). **Modello:** `mai-code-1.1-flash`. **Output:** `docs/m4_variants/JsonDecoder_C2.java`.
- **Diff C1↔C2:** solo rename della costante `FIXED_TYPE`→`FIXED` (+ un dettaglio javadoc). Funzionalmente equivalente a C1.
- **Metriche:** **186/186 verdi**, mutation 333/401 = **83%**, **smell 0** (bestValue=true), ncloc 370, complexity 85.
- **Esito:** successo pieno = C1. Il contesto BB non cambia l'esito (già 0 in C1); output cosmético-diverso.

## JsonDecoder — Variante C3 (codice + BB + cf)

- **Branch:** `m4-c3-jsondecoder` (commit `aaa337a73`). **Modello:** `mai-code-1.1-flash`. **Output:** `docs/m4_variants/JsonDecoder_C3.java`.
- **Diff vs C1/C2:** costante `JSON_FACTORY` (static final, era `jsonFactory`) + `readEnum` semplificato (rimossa la riga ridondante `in.getText();`, label in variabile). Comportamento invariato.
- **Metriche:** **186/186 verdi**, mutation 333/401 = **83%**, **smell 0**, ncloc 370, complexity 85.
- **Esito:** successo pieno = C1/C2. Contesto cf ininfluente sull'esito (già 0).

## JsonDecoder — Variante C4 (codice + BB + cf + mt)

- **Branch:** `m4-c4-jsondecoder` (commit `0bf214d1d`). **Modello:** `mai-code-1.1-flash`. **Output:** `docs/m4_variants/JsonDecoder_C4.java`.
- **Diff vs C3:** output Copilot **identico a C3** (JSON_FACTORY, FIXED, readEnum con label). Scritto sul branch via assistente (paste non salvato).
- **Metriche:** **186/186 verdi**, mutation 333/401 = **83%**, **smell 0**, ncloc 370, complexity 85.
- **Esito:** successo pieno = C1/C2/C3.

---

## SINTESI JsonDecoder (C0 → C1–C4)

| Variante | Contesto | Smell | Test | Mutation | ncloc | Note |
|---|---|---|---|---|---|---|
| **C0** | baseline | 3 | 186 ok | 83% | ~424 (CK) | S1488, S1192, S3776 |
| **C1** | codice | **0** | 186 ok | 83% | 370 | FIXED_TYPE + doAction helpers |
| **C2** | +BB | **0** | 186 ok | 83% | 370 | rename costante (FIXED) |
| **C3** | +BB+cf | **0** | 186 ok | 83% | 370 | JSON_FACTORY + readEnum pulito |
| **C4** | +BB+cf+mt | **0** | 186 ok | 83% | 370 | = C3 |

**Conclusioni JsonDecoder (per il report):**
1. **Zero smell già in C1** (3→0): i 3 smell (S1488 return diretto, S1192 letterale duplicato, S3776 complessità di `doAction`) **non** sono legati alla firma pubblica → tutti eliminabili senza cambiare l'API.
2. **Comportamento fedele in tutte le varianti**: nessuna correzione silente (contrariamente a `writeEscapedString` di GenericData). L'oracolo (186 verdi) e il mutation invariato (83%) confermano l'equivalenza.
3. **Contesto di test ininfluente sull'esito** (già 0 da C1): C1–C4 producono lo stesso risultato con differenze solo cosmetiche (nomi costante, formattazione) → coerente con GenericData.

## SINTESI COMPLESSIVA M4-3 (2 classi × 4 varianti)

| Classe | Smell C0 | Smell C1–C4 | Test | Mutation | Comportamento |
|---|---|---|---|---|---|
| `GenericData` | 15 | **7** (tutte) | 186 ok | 83% | cambia in `writeEscapedString` (bug fix silente, non coperto) |
| `JsonDecoder` | 3 | **0** (tutte) | 186 ok | 83% | fedele |

**Tesi centrale supportata dai dati:**
- L'LLM (`mai-code-1.1-flash`) **elimina gli smell risolvibili senza toccare l'API** (JsonDecoder → 0) ma **non** quelli vincolati alla firma pubblica (GenericData → 7 residui S6213/S1172/S1161).
- **Aumentare il contesto di test (C1→C4) NON migliora l'esito** per nessuna delle due classi: le varianti convergono (spesso identiche o cosmeticamente diverse). Ipotesi NotebookLM "più contesto ⇒ refactoring migliore" **falsificata** in questo caso.
- **"Test verdi" ≠ equivalenza piena**: il caso `writeEscapedString` mostra un cambio di comportamento non rilevato dall'oracolo (threat: coverage gap / implementation dependence). Il controllo intellettuale umano resta necessario.
