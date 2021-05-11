package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.ArrayList;
import java.util.List;

public class BeregningsgrunnlagRegel {
    private String aktivitetStatus;
    private int antallArbeidsgivereIBeregningUtenEtterlønnSluttpakke;
    private boolean snNyoppstartet;
    private List<BeregningsgrunnlagAndel> andelListe = new ArrayList<>();

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    public List<BeregningsgrunnlagAndel> getAndelListe() {
        return andelListe;
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private BeregningsgrunnlagRegel kladd;

        private Builder() {
            this.kladd = new BeregningsgrunnlagRegel();
        }

        public Builder medAktivitetStatus(String aktivitetStatus) {
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
