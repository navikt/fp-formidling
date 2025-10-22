package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BeregningsgrunnlagRegel {
    private AktivitetStatus regelStatus;
    private int antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke;
    private boolean snNyoppstartet;
    private List<BeregningsgrunnlagAndel> andelListe = new ArrayList<>();

    public AktivitetStatus getRegelStatus() {
        return regelStatus;
    }

    public List<BeregningsgrunnlagAndel> getAndelListe() {
        return andelListe;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (BeregningsgrunnlagRegel) object;
        return Objects.equals(regelStatus, that.regelStatus) && Objects.equals(antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke,
            that.antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke) && Objects.equals(snNyoppstartet, that.snNyoppstartet) && Objects.equals(
            andelListe, that.andelListe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regelStatus, antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke, snNyoppstartet, andelListe);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private BeregningsgrunnlagRegel kladd;

        private Builder() {
            this.kladd = new BeregningsgrunnlagRegel();
        }

        public Builder medAktivitetStatus(AktivitetStatus aktivitetStatus) {
            this.kladd.regelStatus = aktivitetStatus;
            return this;
        }

        public Builder medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(int antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke) {
            this.kladd.antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke = antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke;
            return this;
        }

        public Builder medSnNyoppstartet(boolean snNyoppstartet) {
            this.kladd.snNyoppstartet = snNyoppstartet;
            return this;
        }

        public Builder medAndelListe(List<BeregningsgrunnlagAndel> andelListe) {
            this.kladd.andelListe = andelListe;
            return this;
        }

        public BeregningsgrunnlagRegel build() {
            return this.kladd;
        }
    }
}
