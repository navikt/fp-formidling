package no.nav.vedtak.felles.prosesstask.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.vedtak.log.mdc.MDCOperations;

public class ProsessTaskGruppe {

    private final List<Entry> tasks = new ArrayList<>();
    private final Map<String, String> props = new HashMap<>();
    private int sekvensNr;

    public ProsessTaskGruppe() {
        // empty gruppe
    }

    public ProsessTaskGruppe(ProsessTaskData data) {
        // behold sekvens hvis satt fra før, ellers sett til 1
        String sekvens = data.getSekvens() != null ? data.getSekvens() : Integer.toString(++sekvensNr);
        tasks.add(new Entry(sekvens, data));
    }

    public List<Entry> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Legger til ny {@link ProsessTaskData}. Vil kjøres når foregående er ferdig kjørt. Hvis foregående feiler, vil
     * prosessen stoppe opp til det er løst.
     */
    public ProsessTaskGruppe addNesteSekvensiell(ProsessTaskData prosessTask) {
        sekvensNr++;
        leggTil(prosessTask);
        return this;
    }

    /**
     * støtter p.t. bare enkle fork-join (ikke multiple branch / sekvensielle steg sammensatt).
     */
    public ProsessTaskGruppe addNesteParallell(ProsessTaskData... prosessTasks) {
        sekvensNr++;
        for (ProsessTaskData pt : prosessTasks) {
            // alle får samme sekvensnr
            leggTil(pt);
        }
        return this;
    }

    private void leggTil(ProsessTaskData pt) {
        // kopier ned props til tasks siden de lagrer dette.
        props.entrySet().stream().forEach(e -> pt.setProperty(e.getKey(), e.getValue()));

        tasks.add(new Entry(Integer.toString(sekvensNr), pt));
    }

    public ProsessTaskGruppe addNesteParallell(Collection<ProsessTaskData> prosessTasks) {
        sekvensNr++;
        for (ProsessTaskData pt : prosessTasks) {
            leggTil(pt);
        }
        return this;
    }

    public void setBehandling(Long fagsakId, Long behandlingId, String aktørId) {
        this.getTasks().forEach(e -> e.getTask().setKobling(behandlingId, aktørId));
    }

    public void setCallId(String callId) {
        setProperty(ProsessTaskData.CALL_ID, callId);
    }

    public void setProperty(String key, String value) {
        props.put(key, value);
        for (Entry pt : tasks) {
            // skriv til tidligere lagt til (overskriver evt. tidligere verdier.)
            pt.task.setProperty(key, value);
        }
    }

    public void setCallIdFraEksisterende() {
        setCallId(MDCOperations.getCallId());
    }

    public static class Entry {
        private final String sekvens;
        private final ProsessTaskData task;

        Entry(String sekvens, ProsessTaskData task) {
            this.sekvens = sekvens;
            this.task = task;
        }

        public String getSekvens() {
            return sekvens;
        }

        public ProsessTaskData getTask() {
            return task;
        }

    }

}
