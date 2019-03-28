package no.nav.foreldrepenger.melding.behandling.beregning;

import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsresultatAndel {
    private String dagsats;
    private String stillingsprosent;
    private String aktivitetStatus; //Kode
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;

    public String getDagsats() {
        return dagsats;
    }

    public String getStillingsprosent() {
        return stillingsprosent;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver);
    }

    public ArbeidsforholdRef getArbeidsforholdRef() {
        return arbeidsforholdRef;
    }
}
