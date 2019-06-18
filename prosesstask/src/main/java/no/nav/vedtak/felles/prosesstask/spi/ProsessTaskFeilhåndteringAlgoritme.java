package no.nav.vedtak.felles.prosesstask.spi;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTypeInfo;

/**
 * Interface for implementasjoner av feilhåndtering/backoff.
 */
public interface ProsessTaskFeilhåndteringAlgoritme {

    String kode();

    ForsinkelseStrategi getForsinkelseStrategi();

    /**
     * Avgjør om vi skal prøve på nytt.
     *
     * @param taskType     - type ProsessTask.
     * @param antallFeilet - antall ganger tidligere feilet.
     * @param exception    - (optional - kan være null).
     */
    boolean skalKjørePåNytt(ProsessTaskTypeInfo taskType, int antallFeilet, Exception exception);

    Feil hendelserNårIkkeKjøresPåNytt(Exception exception, ProsessTaskData prosessTaskData);
}
