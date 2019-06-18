package no.nav.vedtak.felles.prosesstask.api;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import no.nav.vedtak.feil.Feil;

/**
 * CDI Event for ProsessTask.
 * Publiseres når en ProsessTask opprettes eller endrer status.
 * <p>
 * ADVARSEL:<br>
 * Publisering skjer før den eventuelt lagres, så når eventer observeres synkront og det feiler kan dette avbryte
 * transaksjonen<br>
 * I CDI 2.0 vil dette kunne endres til asynkron håndtering.
 * <p>
 */
public class ProsessTaskEvent implements ProsessTaskInfo {

    /**
     * Kopi av underliggende datastruktur.
     * Merk at endringer på {@link #data} vil ikke ha noen effekt. Den er kun en kopi av underliggende struktur.
     */
    private final ProsessTaskInfo data;
    private Exception exception;
    private ProsessTaskStatus gammelStatus;
    private ProsessTaskStatus nyStatus;
    private Feil feil;

    public ProsessTaskEvent(ProsessTaskData data, ProsessTaskStatus gammelStatus, ProsessTaskStatus nyStatus, Feil feil, Exception e) {
        this.gammelStatus = gammelStatus;
        this.nyStatus = nyStatus;
        this.feil = feil;
        Objects.requireNonNull(data, "data"); //$NON-NLS-1$
        this.data = data;
        this.exception = e;
    }

    /**
     * Rapportert (original) exception hvis task har feilet.
     */
    public Exception getException() {
        return exception;
    }

    public ProsessTaskStatus getGammelStatus() {
        return gammelStatus;
    }

    public ProsessTaskStatus getNyStatus() {
        return nyStatus;
    }

    @Override
    public int getAntallFeiledeForsøk() {
        return data.getAntallFeiledeForsøk();
    }

    @Override
    public String getAktørId() {
        return data.getAktørId();
    }

    @Override
    public Long getKoblingId() {
        return data.getKoblingId();
    }

    @Override
    public String getGruppe() {
        return data.getGruppe();
    }

    /**
     * Rapportert feil hvis task har feilet.
     */
    public Feil getFeil() {
        return feil;
    }

    @Override
    public Optional<ProsessTaskHendelse> getHendelse() {
        return data.getHendelse();
    }

    @Override
    public Long getId() {
        return data.getId();
    }

    @Override
    public LocalDateTime getNesteKjøringEtter() {
        return data.getNesteKjøringEtter();
    }

    @Override
    public String getPayloadAsString() {
        return data.getPayloadAsString();
    }

    @Override
    public int getPriority() {
        return data.getPriority();
    }

    @Override
    public Properties getProperties() {
        // kopier for immutability
        return new Properties(data.getProperties());
    }

    @Override
    public String getPropertyValue(String key) {
        return data.getPropertyValue(key);
    }

    @Override
    public String getSekvens() {
        return data.getSekvens();
    }

    @Override
    public String getSisteFeil() {
        return data.getSisteFeil();
    }

    @Override
    public String getSisteFeilKode() {
        return data.getSisteFeilKode();
    }

    @Override
    public LocalDateTime getSistKjørt() {
        return data.getSistKjørt();
    }

    @Override
    public ProsessTaskStatus getStatus() {
        return data.getStatus();
    }

    @Override
    public String getTaskType() {
        return data.getTaskType();
    }

}
