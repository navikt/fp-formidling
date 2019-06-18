/**
 * Her ligger all logikk for å fordele tasker på tvers av flere pollere,
 * plukke tasker avhengig av om der klare for å kjøres (status + neste_kjoering_etter tid er passert), og
 * besørge at tasker kjøres i rekkefølge de var ment (sekvensielt eller parallellt).
 * <p>
 * SELECT'en er basert på to ting som er verdt å forstå når man leser denne.
 * <ul>
 * <li>SELECT FOR UPDATE SKIP LOCKED: Benyttes til å spre tasker på tvers av flere pollere slik at de ikke går i
 * beina på hverandre. Gir dermed mulighet for økt skalerbarhet og fleksible kjøring</li>
 * <li>SQL WINDOW Functions: ANSI SQL standard for partisjonering av resultater. Brukes for å finne lavest
 * nummerererte tasker i en gruppe (de som skal kjøre før de andre). Ligner GROUP BY men kan opererere på
 * subresultater (så vesentlig kraftigere)</li>
 * </ul>
 */
SELECT pt.*
FROM prosess_task pt

WHERE pt.id
  IN (
        -- Må beytte en IN clause istdf. JOIN, ellers kaster SKIP LOCKED exception (ORA-02014).
        -- Gir oss ett ekstra nivå av subquery
        SELECT tbl.ID
        FROM
          (
            SELECT id
                , task_type
                   -- SQL Window Function: finn første sekvens i task_gruppe. (dersom task_gruppe er NULL brukes task_type her som substitutt)
                , FIRST_VALUE(task_sekvens) OVER (PARTITION BY coalesce(task_gruppe, task_type) ORDER BY task_sekvens ASC NULLS FIRST, siste_kjoering_ts ASC NULLS FIRST, prioritet DESC) AS foerste_sekvens
                , task_sekvens
                , status
                , feilede_forsoek
            FROM prosess_task pt
            WHERE
                -- bruker dette itdf. (status != 'FERDIG'). Innført for å bruke partisjoner med minst data, unngår skanning av alle partisjoner
                status in ('FEILET', 'KLAR', 'VENTER_SVAR', 'SUSPENDERT', 'VETO')
          ) tbl
            INNER JOIN prosess_task_type tt ON tt.kode = tbl.task_type
        WHERE
          -- filtrer vekk de som ikke har samme sekvensnr som første i gruppen, og som er KLAR
            tbl.task_sekvens=tbl.foerste_sekvens
          AND tbl.status IN ('KLAR')
          -- sjekk feilede forsøk
          AND tbl.feilede_forsoek < tt.feil_maks_forsoek
      )
  -- fjerner de som har mindre enn maks antall feilde forsøk
  -- håndterer at kjøring ikke skjer før angitt tidstempel
  AND (pt.neste_kjoering_etter IS NULL OR pt.neste_kjoering_etter < COALESCE(:neste_kjoering, CURRENT_TIMESTAMP))
  AND status = 'KLAR' -- effektiviserer planen med bedre indeks
  AND pt.id NOT IN (:skip_ids) -- sjekk mot skip ids i ytre loop ellers paavirkes rekkefølge
  -- sorter etter prioritet og når de sist ble kjørt
ORDER BY prioritet DESC, siste_kjoering_ts ASC NULLS FIRST, ID ASC
         FOR UPDATE SKIP LOCKED
