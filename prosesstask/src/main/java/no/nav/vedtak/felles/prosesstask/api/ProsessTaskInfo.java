package no.nav.vedtak.felles.prosesstask.api;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

/**
 * Representerer read-only data om en {@link ProsessTask}.
 */
public interface ProsessTaskInfo {

    String getSisteFeil();

    String getSisteFeilKode();

    int getAntallFeiledeForsøk();

    LocalDateTime getSistKjørt();

    String getTaskType();

    int getPriority();

    Long getId();

    String getPropertyValue(String key);

    LocalDateTime getNesteKjøringEtter();

    ProsessTaskStatus getStatus();

    String getGruppe();

    String getSekvens();

    String getAktørId();

    Long getKoblingId();

    String getPayloadAsString();

    Optional<ProsessTaskHendelse> getHendelse();

    Properties getProperties();

}
