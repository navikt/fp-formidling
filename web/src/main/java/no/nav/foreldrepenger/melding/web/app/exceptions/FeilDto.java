package no.nav.foreldrepenger.melding.web.app.exceptions;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.melding.web.app.exceptions.FeilType.GENERELL_FEIL;

import java.util.Collection;

public record FeilDto(String feilmelding, FeilType type, Collection<FeltFeilDto> feltFeil) {

    public FeilDto(String feilmelding, FeilType type) {
        this(feilmelding, type, emptyList());
    }

    public FeilDto(String feilmelding, Collection<FeltFeilDto> feltFeil) {
        this(feilmelding, GENERELL_FEIL, feltFeil);
    }

    public FeilDto(String feilmelding) {
        this(feilmelding, GENERELL_FEIL, emptyList());
    }

}
