package no.nav.vedtak.felles.prosesstask.api;

import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.feil.Feil;

public class ProsessTaskKritiskException extends TekniskException {
    public ProsessTaskKritiskException(Feil feil) {
        super(feil);
    }
}
