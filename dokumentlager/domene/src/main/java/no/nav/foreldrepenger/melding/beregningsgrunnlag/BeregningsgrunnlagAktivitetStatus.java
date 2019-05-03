package no.nav.foreldrepenger.melding.beregningsgrunnlag;

public class BeregningsgrunnlagAktivitetStatus {
    private AktivitetStatus aktivitetStatus;

    public BeregningsgrunnlagAktivitetStatus(AktivitetStatus aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }
}
