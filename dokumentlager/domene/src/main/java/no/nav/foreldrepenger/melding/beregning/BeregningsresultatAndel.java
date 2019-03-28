package no.nav.foreldrepenger.melding.beregning;

import java.util.Optional;

import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsresultatAndel {
    private int dagsats;
    private String stillingsprosent;
    private String aktivitetStatus; //Kode
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;
    private Boolean brukerErMottaker;

    public int getDagsats() {
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

    public boolean erBrukerMottaker() {
        return brukerErMottaker;
    }
}
