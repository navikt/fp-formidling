package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BeregningsgrunnlagAktivitetStatus {
    private Hjemmel hjemmel;
    private String aktivitetStatus;//Kodeliste.AktivitetStatus;

    public BeregningsgrunnlagAktivitetStatus(String aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public static BeregningsgrunnlagAktivitetStatus fraDto(KodeDto kodedto) {
        return new BeregningsgrunnlagAktivitetStatus(kodedto.kode);
    }

    public Hjemmel getHjemmel() {
        return hjemmel;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }
}
