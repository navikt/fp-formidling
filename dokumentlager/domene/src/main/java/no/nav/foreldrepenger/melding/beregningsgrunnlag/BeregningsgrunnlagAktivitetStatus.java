package no.nav.foreldrepenger.melding.beregningsgrunnlag;

public class BeregningsgrunnlagAktivitetStatus {
    /*TODO Trenger hjemmel */
    private Hjemmel hjemmel;
    private AktivitetStatus aktivitetStatus;//Kodeliste.AktivitetStatus;

    public BeregningsgrunnlagAktivitetStatus(AktivitetStatus aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public Hjemmel getHjemmel() {
        return hjemmel;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }
}
