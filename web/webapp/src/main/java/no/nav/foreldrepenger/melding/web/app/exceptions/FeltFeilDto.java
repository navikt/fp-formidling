package no.nav.foreldrepenger.melding.web.app.exceptions;

import java.io.Serializable;

public class FeltFeilDto implements Serializable {

    private final String navn;
    private final String melding;

    public FeltFeilDto(String navn, String melding) {
        this.navn = navn;
        this.melding = melding;
    }

    public String getNavn() {
        return navn;
    }

    public String getMelding() {
        return melding;
    }
}
