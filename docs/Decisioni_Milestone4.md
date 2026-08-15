# Decisioni — Milestone 4 (Refactoring automatico con LLM verso "zero smell")

> Registro passo-passo di **ogni** decisione, comprese quelle **obbligate** dalla specifica.
> Legenda stato: 🔒 obbligata (specifica/slide) · ✅ scelta (mia, con supporto NotebookLM) · 💡 consigliata (buona prassi) · ❓ da confermare.
> Continuità: M1 (dataset) → M2 (classificazione) → M3 (what-if su NSmells) → **M4 (refactoring LLM)**.

## Metodo di lavoro
- Le scelte già prese si applicano passo-passo; nei dubbi si chiede a me o a NotebookLM.
- Si documenta ogni decisione con: **decisione**, **stato**, **motivazione**, **come è implementata/verificata**.

---

## 1. Scopo della Milestone (NotebookLM)

Investigare se sia possibile ottenere **"zero smell"** tramite **refactoring automatico** con un **LLM** (es. GitHub Copilot), partendo dalle classi *smelly + difettose* identificate in M3. Si sottopongono le classi all'LLM con **diversi livelli di input** e si verifica se il codice generato **compila**, **rimuove gli smell** e **mantiene la funzionalità** originale.

## 2. Attività, varianti e livelli di input — 🔒 (raffinato da NotebookLM)

Per ciascuna classe scelta si genera un insieme di **varianti a contesto di test crescente**, per verificare se **più informazioni di test** migliorino il refactoring dell'LLM. Schema definitivo (NotebookLM):

| Variante | Contesto fornito a Copilot |
|---|---|
| **C0** | *baseline* — classe originale non modificata (misure di riferimento). |
| **C1** | solo **codice sorgente** (refactoring "al buio"). |
| **C2** | codice + test **Black-box** (BB). |
| **C3** | codice + test BB + suite raffinata su **Control Flow coverage** (CF, JaCoCo). |
| **C4** | codice + test BB + CF + suite raffinata su **Mutation Testing** (MT, PIT). |

Per ogni variante (classe × C1…C4) si misurano gli esiti: **compila (sì/no)**, **smell residui** (ri-scansione), **funzionalità preservata** (i miei test passano), più il **delta** su coverage/mutation e su WMC/LOC (§5bis).

**Origine dei test (imposto, NotebookLM):** i test che caratterizzano le classi e fanno da oracolo/contesto devono essere **sviluppati da me**, non quelli nativi di AVRO.
- **Disabilitare/rimuovere** tutti i test nativi del progetto Apache AVRO dalla copia di lavoro, così che l'esito pass/fail dipenda solo dal mio lavoro e non da config/dipendenze della community.
- I miei test fungono da **oracolo di regressione**: se una variante CX non passa i test che C0 superava, l'LLM ha introdotto un errore funzionale/allucinazione.

**Famiglie di test da produrre (per classe, in cartelle separate):**
- **Manuali (BB):** Black-box con **Category Partition** + **Boundary Value Analysis** sulle funzionalità.
- **Evoluti (CF & MT):** la suite manuale viene raffinata/ampliata osservando la **Control Flow coverage (JaCoCo)** e la **Mutation Analysis (PIT)**.
- **Automatici:** generati con approcci **Random (Randoop)**, **LLM-generated** (prompt di test generation) e **Coverage-guided (EvoSuite)**.

## 3. Deliverable — 🔒

- **Codice rifattorizzato** prodotto dall'LLM nelle varie condizioni.
- **`classes.txt`** — elenco delle **2 classi** su cui si è lavorato per la parte di testing, in **ordine alfabetico** e con il **path completo del package**.

## 4. Selezione delle 2 classi — ✅ (algoritmo del nome, NotebookLM)

**Procedura imposta (3 step):**
1. **Ranking:** ordinare le classi dell'ultima release (**1.5.4**) per **NSmells** decrescente. *(Considerate le sole classi **top-level** — una per file — perché NSmells è un conteggio **per-file** replicato sulle classi annidate; tie-break su WMC decrescente.)*
2. **Filtraggio (scelta soggettiva documentata):** scartate le classi **banali** con due criteri combinati:
   - **complessità** — WMC < 10 (pochi metodi / metodi troppo semplici);
   - **sostanza** — NSmells < 3 (troppo pochi smell: il "delta" qualitativo post-refactoring sarebbe povero e poco indicativo delle capacità dell'LLM — indicazione NotebookLM).
   Restano **N = 37** classi.
3. **Algoritmo del nome:** prima lettera del **nome** = **M** (Matteo) = 13ª lettera → **X = 13 mod 5 = 3** → si selezionano la **4ª** (prima+3) e la **quartultima** (ultima−3 = posizione 34) del ranking filtrato.

**Classi selezionate:**

| Ruolo | Posizione | Classe | NSmells | WMC | LOC | Buggy |
|---|---|---|---|---|---|---|
| Testa (complesso) | 4ª | **org.apache.avro.generic.GenericData** | 15 | 136 | 633 | yes |
| Coda (più semplice) | 34ª (N−3) | **org.apache.avro.io.JsonDecoder** | 3 | 78 | 424 | yes |

**Motivazione della coppia (NotebookLM):** l'algoritmo "testa e coda" fa lavorare su **due livelli di difficoltà** (molti smell/molto complessa vs meno smell/più semplice), per discutere come i livelli di input dell'LLM incidano al variare della complessità. La testa (GenericData) è **stabile** rispetto al filtro (4ª per NSmells con qualsiasi soglia ragionevole). Per la coda si è aggiunto il vincolo **NSmells ≥ 3** su indicazione di NotebookLM: una classe con **1 solo smell** (era `ByteBufferOutputStream`) darebbe un'analisi del delta troppo povera; `JsonDecoder` — 3 smell ma **WMC 78** di logica di parsing — offre "sostanza algoritmica" su cui l'LLM può effettivamente ragionare, pur restando il task più semplice della coppia. Lavorare a livello di **classe** (non di singolo metodo) è volutamente più difficile per l'LLM.

**Alternativa considerata:** NSmells ≥ 2 → coda = `org.apache.avro.util.WeakIdentityHashMap` (2 smell, WMC 17); scartata perché meno ricca di logica di JsonDecoder.

**Deliverable `classes.txt`** (root del progetto), nomi completi in ordine alfabetico:
```
org.apache.avro.generic.GenericData
org.apache.avro.io.JsonDecoder
```

**Testabilità (Mockito):** dipendenze complesse non escludono una classe — vanno gestite con **mock** per isolare la SUT (indicazione del corso).

## 5. Protocollo di verifica — 💡

- **Compilazione:** build Maven del modulo AVRO dopo la sostituzione della classe.
- **Smell residui:** ri-scansione **SonarCloud source-only** (stessa modalità di M1) sul file rifattorizzato → confronto NSmells prima/dopo.
- **Funzionalità preservata:** esecuzione dei **miei test** (oracolo); una regressione = funzionalità non preservata.
- **Adeguatezza dei test:** **JaCoCo** (statement/branch) + **PIT** (mutation score) — vedi §5bis.

## 5bis. Protocollo del ciclo di refactoring e metriche — ✅ (NotebookLM)

**Strumenti di adeguatezza:** **JaCoCo** (coverage strutturale statement/branch) e **PIT** (mutation score).
**Quando misurare:** prima su **C0** per *guidare* il raffinamento dei test manuali (CF/MT); poi sulle **varianti rifattorizzate** per documentare il **delta** qualitativo. Nessuna soglia fissa (non serve il 100%): va dimostrato un **miglioramento consapevole** (es. coprire un metodo complesso conta più che saturare un metodo banale).

**Sequenza (confronto credibile):**
1. **Baseline C0** — misura smell (SonarCloud) + adeguatezza test (JaCoCo/PIT) sulla classe originale.
2. **Sintesi varianti** — Copilot genera C1 (solo codice), C2 (+BB), C3 (+BB+CF), C4 (+BB+CF+MT).
3. **Verifica** — per ogni variante: compilazione, riesecuzione dei test di C0, nuova misura smell + coverage/mutation.
4. **Analisi Delta** — confronto quantitativo e qualitativo (smell rimossi vs aggiunti, coverage, WMC/LOC).

**Definizione di "successo" (equilibrio di fattori, non solo test verdi):**
- **Compilabilità + pass rate:** compila e non regredisce sulle funzionalità originali.
- **Rimozione smell:** obiettivo primario, portare a **zero** (o ridurre drasticamente) gli smell SonarCloud.
- **Non peggioramento metriche:** l'LLM non deve pulire gli smell aumentando **WMC/LOC** (complessità e dimensione correlano con la difettosità).
- **Controllo intellettuale:** ogni anomalia va **giustificata logicamente** — la valutazione si basa su questa capacità, non sulla perfezione dell'LLM.

## 6. Threats to Validity — specifici di M4 (NotebookLM)

- **Data leakage:** l'LLM potrebbe aver **già visto** il codice di Apache AVRO in addestramento, influenzando il risultato.
- **Output non deterministico:** natura probabilistica dell'LLM → risultati diversi a parità di prompt; fissare/annotare **modello, versione, prompt**.
- **Implementation dependence:** i test **white-box** generati automaticamente possono fallire su un refactoring *corretto* solo perché legati a dettagli implementativi di C0.
- **Human bias / over-trust:** eccessivo affidamento nelle risposte dell'LLM senza adeguata verifica umana.
- **Definizione di "smell":** la ri-scansione deve usare gli **stessi ruleset** di M1, altrimenti il confronto non è valido.
- **Funzionalità vs test:** "funzionalità preservata" è verificata solo fin dove arrivano i test; coverage bassa può mascherare regressioni.

## 7. Piano operativo (task)
- **M4-0** Roadmap + shortlist classi *(questo documento)*. ✔️
- **M4-1** Algoritmo del nome, scelta delle 2 classi, `classes.txt`. ✔️
- **M4-2** Estrarre il sorgente delle 2 classi (release 1.5.4), disabilitare i test nativi, scrivere i test propri (**BB/Category Partition + BVA**, poi evoluti su **JaCoCo** e **PIT**, + automatici Randoop/EvoSuite/LLM), configurare JaCoCo + PIT nel `pom.xml`. Misurare la **baseline C0** (smell + coverage + mutation). *(scaffold BB + pom fatti — vedi §9)*
- **M4-3** Eseguire il refactoring LLM (Copilot) generando **C1–C4** × 2 classi *(esecuzione locale dell'utente)*, annotando **modello, versione e prompt esatti**.
- **M4-4** Verifiche per ogni variante: **compila** / **smell residui** (ri-scansione SonarCloud stesso ruleset di M1) / **test passano** / **delta coverage-mutation-WMC-LOC**; tabella risultati (classe × variante × esito).
- **M4-5** Confluenza nel **report finale integrato** M1→M4.

## 8. Punti aperti / risolti
1. ~~Quali **2 classi**~~ **RISOLTO** (§4): GenericData (testa) + JsonDecoder (coda), via algoritmo del nome (M → X=3) + filtro WMC ≥ 10 **e NSmells ≥ 3**.
2. ~~**Soglie di coverage**~~ **RISOLTO** (§5bis): niente soglia fissa; le varianti si distinguono per *contesto* (C2=BB, C3=+CF/JaCoCo, C4=+MT/PIT) e si valuta il **miglioramento consapevole**, non una percentuale target.
3. **LLM** da usare: GitHub Copilot (default del corso) — confermare la versione/modello effettivamente disponibile. ❓
4. **Convenzione lettera** = nome (Matteo → M → X=3), confermata dall'utente. Se il docente intende il cognome (La Gioia → L → X=2), testa=BinaryData: da verificare a lezione. ❓

## 9. Log implementazione M4-2 — ✅

**Sorgenti C0 esaminati** (release 1.5.4, branch `m4-genericdata`/`m4-jsondecoder` del fork):
- `GenericData` — API pubblica: `validate`, `induce`, `toString`, `compare`, `hashCode`, `resolveUnion`, più classi annidate `Record`, `Array`, `Fixed`, `EnumSymbol`.
- `JsonDecoder` — costruttori **package-private** (si istanzia via `DecoderFactory.get().jsonDecoder(schema, …)`); letture `readInt/Long/Float/Double/Boolean/String/Null/Bytes/Enum/Array/Map`, `readIndex` (union).

**Test suite Black-box create** (`src/test/java/com/milestone4/`), Category Partition + BVA:
- `genericdata/bb/TestGenericDataBB.java` — validate/induce/toString/compare/hashCode/resolveUnion.
- `genericdata/bb/TestGenericDataStructures.java` — Record/Array/Fixed/EnumSymbol.
- `jsondecoder/bb/TestJsonDecoderBB.java` — primitivi + casi d'errore + precondizioni null.
- `jsondecoder/bb/TestJsonDecoderComplex.java` — bytes/enum/array/map/union.
- Vincoli: **JUnit 4.8.2** (niente `assertThrows` → `@Test(expected=…)`); Surefire include solo file `Test*`.

**Disabilitazione test nativi (scelta d'implementazione):** invece di cancellare i test AVRO, override Surefire nel `pom.xml` del modulo con `includes combine.self="override"` → `**/milestone4/**/Test*.java`. Reversibile e non distruttivo; l'oracolo resta solo il mio.

**Configurazione `pom.xml` (`lang/java/avro`):**
- **JaCoCo 0.8.11** nel build (goal `prepare-agent` + `report` in fase `test`) → report in `target/site/jacoco/`.
- **PIT 1.15.0** in profilo opt-in `mutation` (targetClasses = GenericData\*/JsonDecoder\*, targetTests = `com.milestone4.*`), per non rompere il build normale.

**Comandi baseline C0 (da eseguire in locale, io non compilo Java):**
```
mvn -pl lang/java/avro test                                            # esegue i miei test + JaCoCo
mvn -pl lang/java/avro -Pmutation org.pitest:pitest-maven:mutationCoverage   # mutation score
```

**Fix build (JDK moderno):** AVRO 1.5.4 era su `source/target 1.6`, non più accettato dai JDK recenti (`error: Source option 6 is no longer supported`). Portati a **1.8** in `lang/java/pom.xml` (compiler pluginManagement). Il modulo lo eredita via `relativePath`.

**Esito baseline C0 — test funzionali (14/08/2026):** `mvn clean test` → **78/78 verdi** (TestGenericDataBB 35, TestGenericDataStructures 17, TestJsonDecoderBB 16, TestJsonDecoderComplex 10). Solo i miei test eseguiti (nativi esclusi). Le mie suite fungono quindi da **oracolo di regressione** valido per C1–C4.

**Baseline C0 — coverage JaCoCo (solo suite Black-box, `mvn clean test`):**

| Classe (top-level) | Line | Branch | Method | Instruction |
|---|---|---|---|---|
| `GenericData` | 109/254 = 42.9% | 92/219 = 42.0% | 16/26 = 61.5% | 503/1188 = 42.3% |
| `JsonDecoder` | 115/212 = 54.2% | 42/94 = 44.7% | 24/37 = 64.9% | 471/890 = 52.9% |

Con classi annidate incluse, `GenericData` → ~48.5% line / 42.7% branch. `GenericData.Array$Iterator` = 0% (iterator non esercitato). Rami scoperti principali: `GenericData` compare/hashCode/instanceOf sui vari tipi di schema, varianti toString/induce; `JsonDecoder` skip*, readFixed, rami doAction (record/union) ed error path.

**Fix ambiente PIT:** JDK utente = **Java 25** (class file major 69); PIT 1.15.0 non lo leggeva (ASM datato). Aggiornato a **PIT 1.25.9** nel profilo `mutation`.

**Baseline C0 — mutation testing (PIT 1.25.9, solo suite BB):** complessivo **158/398 killed = 40%**, test strength 78%, 195 no-coverage. Per classe:

| Classe | Mutazioni | Killed | Survived | No-coverage | Mutation | Test strength |
|---|---|---|---|---|---|---|
| `GenericData` | 296 | 104 | 33 | 159 | 35% | 76% |
| `JsonDecoder` | 102 | 54 | 12 | 36 | 53% | 82% |

Survivor concentrati (target `mt/`): GenericData `hashCode`(7), `writeEscapedString`(5), `compare`(4), `equals`(4), `hashCodeAdd`(3); JsonDecoder `configure`(2) + read* sparsi. No-coverage (target `cf/`): toString/induce/instanceOf/resolveUnion e rami complessi compare/hashCode in GenericData; skip*/readFixed/doAction/EOF in JsonDecoder.

**Interpretazione:** le suite BB sono efficaci dove arrivano (test strength 76–82%) ma coprono ~metà del codice; il salto C2→C3 (cf) deve alzare la coverage, C3→C4 (mt) deve uccidere i survivor elencati.

**Tier `cf/` (coverage-guided) — aggiunto ed eseguito:** `genericdata/cf/TestGenericDataCoverage.java` + `jsondecoder/cf/TestJsonDecoderCoverage.java`. Con BB+cf: **126/126 test verdi**; PIT complessivo **264/398 = 66%** (era 40%), line coverage 51%→**81%**, no-coverage 195→61. Per classe: GenericData 35%→65%, JsonDecoder 53%→72%.

**Tier `mt/` (mutation-guided) — aggiunto ed eseguito:** `genericdata/mt/TestGenericDataMutation.java` (48 casi: escape esatti, validate negativi, resolveUnion a branch non-primo per instanceOf/isX, matrice equals, hashCode discriminante, compare descending + compareTo) + `jsondecoder/mt/TestJsonDecoderMutation.java` (12 casi: configure return/reset, letture consecutive vs nextToken, skipFixed wrong-length, skipString map-key, doAction record-end).

**Progressione dei tier (contesto per Copilot C2/C3/C4) — baseline C0 misurata:**

| Tier | Test | Line cov (classi mutate) | Mutation score | Test strength |
|---|---|---|---|---|
| C2 = BB | 78 | 51% | 40% | 78% |
| C3 = BB+cf | 126 | 81% | 66% | 78% |
| C4 = BB+cf+mt | 186 | 87% | 83% | 91% |

Finale per classe (BB+cf+mt): `GenericData` 242/296 = **82%** mutation, 91% strength (24 survived, 30 no-cov); `JsonDecoder` 88/102 = **86%** mutation, 90% strength (10 survived, 4 no-cov).

**Finding (da citare nel report):** 12 survivor in `GenericData.writeEscapedString` cadono nel ramo di escaping dei caratteri di controllo, che contiene codice **buggato** (`4-builder.length()` e `builder.append(string.toUpperCase())` invece del singolo char) → bug latente + smell; utile come banco di prova per il refactoring LLM (se Copilot lo "pulisce" senza correggerlo, è un segnale).

**Smell C0 (SonarCloud, stesso ruleset di M1 — source-only, `sonar.sources=.`, `sonar.java.binaries=.`, `sonar.scm.disabled=true`):** scansione isolata sulle 2 classi via `sonar.inclusions`, projectVersion `m4-c0-baseline`, projectKey `StitchMl_avro`, org `stitchml`. Conteggi da API `measures/component_tree` (metrica `code_smells`):

| Classe | Smell C0 | WMC | LOC |
|---|---|---|---|
| `GenericData` | **15** | 136 | 633 |
| `JsonDecoder` | **3** | 78 | 424 |

**Baseline C0 COMPLETA (tutte le dimensioni):**

| Classe | Smell | Line cov | Mutation | Test strength | WMC | LOC |
|---|---|---|---|---|---|---|
| `GenericData` | 15 | 42.9% | 82% | 91% | 136 | 633 |
| `JsonDecoder` | 3 | 54.2% | 86% | 90% | 78 | 424 |

**Inventario smell C0 (regole SonarCloud, per la verifica delta di M4-4):**

*GenericData (15, debt 129 min):*
- `java:S3776` Cognitive Complexity ×5 — `compare` (36→15), `validate` (28→15), `toString` (24→15), `writeEscapedString` (29→15), `induce` (16→15).
- `java:S6213` identificatore ristretto (`record`) ×4 — righe 317, 462, 469, 527.
- `java:S1172` parametro inutilizzato (`name`) ×2 — `setField`, `getField`.
- `java:S1481` variabile locale inutilizzata (`hex`) ×1; `java:S1854` dead store a `hex` ×1 — riga 398.
- `java:S1161` manca `@Override` ×1 — riga 109; `java:S3358` ternario annidato ×1 — riga 647.

*JsonDecoder (3, debt 20 min):*
- `java:S1488` return diretto invece di variabile `result` ×1 — riga 241.
- `java:S1192` letterale `"fixed"` duplicato 3× ×1 — riga 277.
- `java:S3776` Cognitive Complexity `doAction` (20→15) ×1 — riga 428.

Nota: lo smell S3776 su `writeEscapedString` coincide col metodo **buggato** già segnalato → banco di prova per il refactoring (se Copilot riduce la complessità senza correggere il bug del ramo control-char, va evidenziato).

**Parte test + baseline di M4-2: COMPLETATA.** 186 test verdi (oracolo robusto), smell/coverage/mutation/WMC/LOC di C0 registrati e regole smell inventariate. Resta opzionale la famiglia `auto/` (Randoop/EvoSuite/LLM). Si procede a M4-3 (refactoring Copilot C1–C4).

## 10. Log M4-3 (refactoring LLM) — in corso
- Branch: `m4-c1-genericdata` creato da `m4-genericdata` (C0). Prima esecuzione test/PIT/sonar = ri-conferma C0 (codice non ancora modificato: 15 smell, 186 verdi, mutation 83%).
- Variante attesa C1 (solo codice): applicare Copilot col prompt C1, poi verificare compila / 186 verdi / smell↓ / WMC-LOC non peggiorati; annotare modello, versione, prompt esatto.
- **Modello Copilot:** `mai-code-1.1-flash`.
- **C1 GenericData ESEGUITA (dettaglio in `Log_Milestone4_Refactoring.md`):** compila, **186/186 verdi**, mutation 83% invariato, **smell 15→7**, ncloc↑ (709). Successo parziale. Finding: `writeEscapedString` corregge un bug latente sui caratteri di controllo → cambio di comportamento non coperto dall'oracolo (threat: coverage gap / implementation dependence). Residui: 4×S6213, 2×S1172, 1×S1161 (legati alla firma pubblica).
- **C2 GenericData ESEGUITA:** output Copilot **byte-identico a C1** (diff normalizzato + md5). I test BB nel contesto non hanno cambiato il refactoring → metriche = C1. **Finding:** per `mai-code-1.1-flash` il passaggio C1→C2 non porta benefici (ipotesi "più contesto ⇒ meglio" falsificata su questa classe).
- **C3 GenericData ESEGUITA (commit 8b0de6e9c):** compila, **186/186 verdi**, mutation 83% (362/437), **smell 15→7** (stessi residui di C1), ncloc 712. Diverso da C1 solo per 2 refactor cosmetici (`induceCollection`, `flipIfDescending`) → **esito equivalente**. Contesto `cf` = forma diversa, risultato identico.
- **C4 GenericData ESEGUITA (commit 29d8f20c5):** compila, **186/186 verdi**, mutation 83% (362/437), **smell 15→7**, ncloc 712. Diverso da C3 solo per rename `applyFieldOrder` → **esito equivalente**.
- **SINTESI GenericData (C1–C4):** convergenza totale a **7 smell** in tutte le varianti (C2 byte-identico a C1; C3/C4 cosmetici). I 7 residui sono legati alla firma pubblica (S6213/S1172 su `record`/`fieldName`) + 1 S1161. **Il contesto di test non ha migliorato l'esito** per `mai-code-1.1-flash`; `writeEscapedString` "corretto" (bug latente) in tutte le varianti senza far fallire l'oracolo (threat coverage gap). Dettaglio completo + tabella in `Log_Milestone4_Refactoring.md`.
- **C1 JsonDecoder ESEGUITA (commit 586d217d2 + fix build/test af16dda5c/469c2713c):** compila, **186/186 verdi**, mutation 83% (333/401), **smell 3→0 (bestValue=true)**, ncloc 370. Comportamento fedele (nessun bug fix silente). **Contrasto chiave con GenericData:** JsonDecoder arriva a **zero smell** già in C1 perché i suoi 3 smell (S1488/S1192/S3776) non toccano la firma pubblica; GenericData resta a 7 per vincoli d'API. Nota: i branch JsonDecoder erano privi di pom-fix e test suite (nati da 1.5.4 pre-M4-2), portati da `m4-genericdata`.
- **C2/C3/C4 JsonDecoder ESEGUITE (commit 4b6eba801/aaa337a73/0bf214d1d):** tutte **smell 0**, 186/186 verdi, mutation 83%. Differenze solo cosmetiche (nome costante `FIXED`/`FIXED_TYPE`, `JSON_FACTORY`, `readEnum`). C4 = C3.

## 11. M4-3 (refactoring LLM) — COMPLETATA (matrice 2×4)

Tutte le 8 varianti eseguite, verificate (compila / 186 verdi / mutation / smell / delta) e committate su branch dedicati; output archiviati in `docs/m4_variants/`, tracciamento completo in `Log_Milestone4_Refactoring.md`.

| Classe | Smell C0 → C1–C4 | Test | Mutation | Comportamento |
|---|---|---|---|---|
| GenericData | 15 → **7** (tutte) | 186 ✔️ | 83% | `writeEscapedString` cambia (bug fix silente non coperto) |
| JsonDecoder | 3 → **0** (tutte) | 186 ✔️ | 83% | fedele |

**Findings principali (per il report finale):**
1. L'LLM elimina gli smell **non vincolati all'API** (JsonDecoder→0), non quelli legati alla firma pubblica (GenericData→7: S6213/S1172/S1161).
2. **Più contesto di test (C1→C4) non migliora l'esito**: convergenza per entrambe le classi → ipotesi NotebookLM falsificata su questo modello/classi.
3. **Test verdi ≠ equivalenza**: bug fix silente in `writeEscapedString` non rilevato dall'oracolo → threat coverage gap; serve controllo intellettuale.
4. Suite robusta: mutation score/test strength invariati (83%/91%) attraverso tutti i refactoring → i test non dipendono dalla struttura interna.
