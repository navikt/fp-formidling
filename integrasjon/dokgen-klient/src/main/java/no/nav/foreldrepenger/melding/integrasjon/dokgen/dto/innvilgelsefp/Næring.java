package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

public class Næring {
    private boolean gradering;
    private int uttaksgrad;
    private int prosentArbeid;
    private int sistLignedeÅr;

    public static Builder ny() {
        return new Builder();
    }

    public boolean isGradering() {
        return gradering;
    }

    public int getSistLignedeÅr() {
        return sistLignedeÅr;
    }

    public static class Builder {
        private Næring kladd;

        private Builder() {
            this.kladd = new Næring();
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

        public Builder medSistLignedeÅr(int sistLignedeÅr) {
            this.kladd.sistLignedeÅr = sistLignedeÅr;
            return this;
        }

        public Næring build() {
            return this.kladd;
        }
    }

}
