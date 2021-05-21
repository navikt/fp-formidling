package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

public class AnnenAktivitet {
    private String aktivitetStatus;
    private boolean gradering;
    private int utbetalingsgrad;
    private int prosentArbeid;

    public boolean isGradering() {
        return gradering;
    }

    public int getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private AnnenAktivitet kladd;

        private Builder() {
            this.kladd = new AnnenAktivitet();
        }

        public Builder medAktivitetStatus(String aktivitetStatus) {
            this.kladd.aktivitetStatus = aktivitetStatus;
            return this;
        }

        public Builder medGradering(boolean gradering) {
            this.kladd.gradering = gradering;
            return this;
        }

        public Builder medUtbetalingsgrad(int utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Builder medProsentArbeid(int prosentArbeid) {
            this.kladd.prosentArbeid = prosentArbeid;
            return this;
        }

        public AnnenAktivitet build() {
            return this.kladd;
        }
    }
}
