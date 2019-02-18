package no.nav.foreldrepenger.melding.web.app.exceptions;

import java.util.Collection;

public class Valideringsfeil extends RuntimeException {
    private final Collection<FeltFeilDto> feltFeil;

    public Valideringsfeil(Collection<FeltFeilDto> feltFeil) {
        this.feltFeil = feltFeil;
    }

    public Collection<FeltFeilDto> getFeltFeil() {
        return feltFeil;
    }

}
