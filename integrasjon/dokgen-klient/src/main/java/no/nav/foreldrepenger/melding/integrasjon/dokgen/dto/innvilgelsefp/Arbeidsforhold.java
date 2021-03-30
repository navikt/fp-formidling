package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

public class Arbeidsforhold {
    private String arbeidsgiverNavn;
    private boolean gradering;
    private int uttaksgrad;
    private int prosentArbeid;
    private int stillingsprosent;
    private int utbetalingsgrad;
    private NaturalytelseEndringType naturalytelseEndringType;
    private String naturalytelseEndringDato;
    private long naturalytelseNyDagsats;

    public boolean isGradering() {
        return gradering;
    }

    public NaturalytelseEndringType getNaturalytelseEndringType() {
        return naturalytelseEndringType;
    }

    public String getNaturalytelseEndringDato() {
        return naturalytelseEndringDato;
    }

    public long getNaturalytelseNyDagsats() {
        return naturalytelseNyDagsats;
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private Arbeidsforhold kladd;

        private Builder() {
            this.kladd = new Arbeidsforhold();
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medGradering(boolean gradering) {
            this.kladd.gradering = gradering;
            return this;
        }

        public Builder medUttaksgrad(int uttaksgrad) {
            this.kladd.uttaksgrad = uttaksgrad;
            return this;
        }

        public Builder medProsentArbeid(int prosentArbeid) {
            this.kladd.prosentArbeid = prosentArbeid;
            return this;
        }

        public Builder medStillingsprosent(int stillingsprosent) {
            this.kladd.stillingsprosent = stillingsprosent;
            return this;
        }

        public Builder medUtbetalingsgrad(int utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Builder medNaturalytelseEndringType(NaturalytelseEndringType naturalytelseEndringType) {
            this.kladd.naturalytelseEndringType = naturalytelseEndringType;
            return this;
        }

        public Builder medNaturalytelseEndringDato(String naturalytelseEndringDato) {
            this.kladd.naturalytelseEndringDato = naturalytelseEndringDato;
            return this;
        }

        public Builder medNaturalytelseNyDagsats(long naturalytelseNyDagsats) {
            this.kladd.naturalytelseNyDagsats = naturalytelseNyDagsats;
            return this;
        }

        public Arbeidsforhold build() {
            return this.kladd;
        }
    }
}
