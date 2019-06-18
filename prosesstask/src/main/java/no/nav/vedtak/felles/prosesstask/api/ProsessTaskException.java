package no.nav.vedtak.felles.prosesstask.api;

import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.feil.Feil;

public class ProsessTaskException extends TekniskException {
    public ProsessTaskException(Feil feil) {
        super(feil);
    }
}
