package no.nav.foreldrepenger.melding.beregningsgrunnlag;

public class BeregningsgrunnlagAktivitetStatus {
    private Hjemmel hjemmel;
    private String aktivitetStatus;//Kodeliste.AktivitetStatus;

    public Hjemmel getHjemmel() {
        return hjemmel;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }
}
