package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver;

import no.nav.foreldrepenger.fpformidling.typer.AktørId;


public class ArbeidsgiverOpplysninger {

    private final String referanse;
    private final String navn;

    public ArbeidsgiverOpplysninger(AktørId aktørId, String navn) {
        this.referanse = aktørId.getId();
        this.navn = navn;
    }

    public ArbeidsgiverOpplysninger(String identifikator, String navn) {
        this.referanse = identifikator;
        this.navn = navn;
    }

    public String getReferanse() {
        return referanse;
    }

    public String getNavn() {
        return navn;
    }

}
