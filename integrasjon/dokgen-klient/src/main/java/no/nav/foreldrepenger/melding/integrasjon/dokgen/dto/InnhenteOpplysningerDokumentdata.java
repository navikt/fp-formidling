package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.List;

public class InnhenteOpplysningerDokumentdata extends Dokumentdata {
    private boolean førstegangsbehandling;
    private boolean revurdering;
    private boolean endringssøknad;
    private boolean død;
    private boolean klage;
    private String søknadDato;
    private String fristDato;
    private List<String> dokumentListe;

    public static Builder ny() {
        return new Builder();
    }

    public boolean getFørstegangsbehandling() { return førstegangsbehandling;  }

    public boolean getRevurdering() { return revurdering; }

    public boolean getEndringssøknad() { return endringssøknad; }

    public boolean getDød() { return død; }

    public boolean getKlage() { return klage; }

    public String getSøknadDato() { return søknadDato; }

    public String getFristDato() {
        return fristDato;
    }

    public List<String> getDokumentListe() {
        return dokumentListe;
    }

    public static class Builder {
        private InnhenteOpplysningerDokumentdata kladd;

        private Builder() {
            this.kladd = new InnhenteOpplysningerDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medFørstegangsbehandling(boolean førstegangsbehandling) {
            this.kladd.førstegangsbehandling = førstegangsbehandling;
            return this;
        }

        public Builder medRevurdering(boolean revurdering) {
            this.kladd.revurdering= revurdering;
            return this;
        }

        public Builder medEndringssøknad(boolean endringssøknad) {
            this.kladd.endringssøknad = endringssøknad;
            return this;
        }

        public Builder medDød(boolean død) {
            this.kladd.død = død;
            return this;
        }

        public Builder medKlage(boolean klage) {
            this.kladd.klage = klage;
            return this;
        }

        public Builder medSøknadDato(String søknadsdato) {
            this.kladd.søknadDato = søknadsdato;
            return this;
        }

        public Builder medFristDato(String fristDato) {
            this.kladd.fristDato = fristDato;
            return this;
        }

        public Builder medDokumentListe(List<String> dokumentListe) {
            this.kladd.dokumentListe = dokumentListe;
            return this;
        }

        public InnhenteOpplysningerDokumentdata build() {
            return this.kladd;
        }
    }
}
