package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class EngangsstønadInnvilgelseDokumentdata extends Dokumentdata {
    private boolean revurdering;
    private boolean førstegangsbehandling;
    private boolean medhold;
    private String innvilgetBeløp;
    private int klagefristUker;
    private boolean død;
    private boolean fbEllerMedhold;
    private boolean erEndretSats;

    public static Builder ny() {
        return new Builder();
    }

    public boolean getRevurdering() { return revurdering; }

    public boolean getFørstegangsbehandling() { return førstegangsbehandling;  }

    public boolean getMedhold() { return medhold; }

    public String getInnvilgetBeløp() {
        return innvilgetBeløp;
    }

    public int getKlagefristUker() { return klagefristUker; }

    public boolean getDød() { return død; }

    public boolean getFbEllerMedhold() { return fbEllerMedhold; }


    public boolean getErEndretSats() { return erEndretSats; }

    public static class Builder {
        private EngangsstønadInnvilgelseDokumentdata kladd;

        private Builder() {
            this.kladd = new EngangsstønadInnvilgelseDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medRevurdering(boolean revurdering) {
            this.kladd.revurdering= revurdering;
            return this;
        }

        public Builder medFørstegangsbehandling(boolean førstegangsbehandling) {
            this.kladd.førstegangsbehandling = førstegangsbehandling;
            return this;
        }

        public Builder medMedhold(boolean medhold) {
            this.kladd.medhold = medhold;
            return this;
        }

        public Builder medInnvilgetBeløp(String innvilgetBeløp) {
            this.kladd.innvilgetBeløp = innvilgetBeløp;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medDød(boolean død) {
            this.kladd.død = død;
            return this;
        }

        public Builder medFbEllerMedhold(boolean fbEllerMedhold) {
            this.kladd.fbEllerMedhold = fbEllerMedhold;
            return this;
        }

        public Builder medErEndretSats(boolean erEndretSats) {
            this.kladd.erEndretSats = erEndretSats;
            return this;
        }

        public EngangsstønadInnvilgelseDokumentdata build() {
            return this.kladd;
        }
    }
}
