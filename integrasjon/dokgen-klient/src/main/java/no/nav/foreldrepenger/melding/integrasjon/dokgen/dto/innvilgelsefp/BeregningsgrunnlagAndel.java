package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;

public class BeregningsgrunnlagAndel {
    private AktivitetStatus aktivitetStatus;
    private String arbeidsgiverNavn;
    private long dagsats;
    private long månedsinntekt;
    private long årsinntekt;
    private boolean etterlønnSluttpakke;

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public long getDagsats() {
        return dagsats;
    }

    public long getMånedsinntekt() {
        return månedsinntekt;
    }

    public long getÅrsinntekt() {
        return årsinntekt;
    }

    public boolean isEtterlønnSluttpakke() {
        return etterlønnSluttpakke;
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private BeregningsgrunnlagAndel kladd;

        private Builder() {
            this.kladd = new BeregningsgrunnlagAndel();
        }

        public Builder medAktivitetStatus(AktivitetStatus aktivitetStatus) {
            this.kladd.aktivitetStatus = aktivitetStatus;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medDagsats(long dagsats) {
            this.kladd.dagsats = dagsats;
            return this;
        }

        public Builder medMånedsinntekt(long månedsinntekt) {
            this.kladd.månedsinntekt = månedsinntekt;
            return this;
        }

        public Builder medÅrsinntekt(long årsinntekt) {
            this.kladd.årsinntekt = årsinntekt;
            return this;
        }

        public Builder medEtterlønnSluttpakke(boolean etterlønnSluttpakke) {
            this.kladd.etterlønnSluttpakke = etterlønnSluttpakke;
            return this;
        }

        public BeregningsgrunnlagAndel build() {
            return this.kladd;
        }
    }
}
