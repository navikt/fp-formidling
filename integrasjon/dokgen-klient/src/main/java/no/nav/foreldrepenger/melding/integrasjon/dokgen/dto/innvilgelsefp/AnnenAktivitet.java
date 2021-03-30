package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;

public class AnnenAktivitet {
    private AktivitetStatus aktivitetStatus;
    private boolean gradering;
    private int uttaksgrad;
    private int prosentArbeid;

    public boolean isGradering() {
        return gradering;
    }

    public AktivitetStatus getAktivitetStatus() {
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

        public Builder medAktivitetStatus(AktivitetStatus aktivitetStatus) {
            this.kladd.aktivitetStatus = aktivitetStatus;
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

        public AnnenAktivitet build() {
            return this.kladd;
        }
    }

}
