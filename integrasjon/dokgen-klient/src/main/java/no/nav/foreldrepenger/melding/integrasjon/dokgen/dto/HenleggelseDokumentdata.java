package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class HenleggelseDokumentdata extends Dokumentdata {
    private boolean vanligBehandling;
    private boolean klage;
    private boolean anke;
    private boolean innsyn;
    private String opphavType;

    public static Builder ny() {
        return new Builder();
    }

    public boolean getVanligBehandling() {
        return vanligBehandling;
    }

    public boolean getKlage() {
        return klage;
    }

    public boolean getAnke() {
        return anke;
    }

    public boolean getInnsyn() {
        return innsyn;
    }

    public String getOpphavType() {
        return opphavType;
    }

    public static class Builder {
        private HenleggelseDokumentdata kladd;

        private Builder() {
            this.kladd = new HenleggelseDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medVanligBehandling(boolean vanligBehandling) {
            this.kladd.vanligBehandling = vanligBehandling;
            return this;
        }

        public Builder medKlage(boolean klage) {
            this.kladd.klage = klage;
            return this;
        }

        public Builder medAnke(boolean anke) {
            this.kladd.anke = anke;
            return this;
        }

        public Builder medInnsyn(boolean innsyn) {
            this.kladd.innsyn = innsyn;
            return this;
        }

        public Builder medOpphavType(String opphavType) {
            this.kladd.opphavType = opphavType;
            return this;
        }

        public HenleggelseDokumentdata build() { return this.kladd; }
    }
}
