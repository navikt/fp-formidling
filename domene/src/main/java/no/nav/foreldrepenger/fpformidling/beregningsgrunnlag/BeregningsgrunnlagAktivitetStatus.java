package no.nav.foreldrepenger.fpformidling.beregningsgrunnlag;

import java.util.Objects;

public record BeregningsgrunnlagAktivitetStatus(AktivitetStatus aktivitetStatus) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeregningsgrunnlagAktivitetStatus that = (BeregningsgrunnlagAktivitetStatus) o;
        return aktivitetStatus == that.aktivitetStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetStatus);
    }
}
