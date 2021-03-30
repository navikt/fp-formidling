package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;

public class BeregningsgrunnlagRegel {
    private AktivitetStatus aktivitetStatus;
    private int antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke;
    private boolean snNyoppstartet;
    private List<BeregningsgrunnlagAndel> andelListe = new ArrayList<>();

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
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
            this.kladd.aktivitetStatus = aktivitetStatus;
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
