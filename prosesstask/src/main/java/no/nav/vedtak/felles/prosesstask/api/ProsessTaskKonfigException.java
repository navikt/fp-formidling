package no.nav.vedtak.felles.prosesstask.api;

import no.nav.vedtak.feil.Feil;

/**
 * Fatal feil som følge av konfigurasjonsfeil el.
 */
public class ProsessTaskKonfigException extends ProsessTaskException {
    public ProsessTaskKonfigException(Feil feil) {
        super(feil);
    }
}
