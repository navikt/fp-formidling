package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class VarselOmRevurderingDokumentdata extends Dokumentdata {
    private String terminDato;
    private String fristDato;
    private int antallBarn;
    private String advarselKode;
    private boolean flereOpplysninger;

    public static Builder ny() {
        return new Builder();
    }

    public String getTerminDato() {
        return terminDato;
    }

    public String getFristDato() {
        return fristDato;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public String getAdvarselKode() {
        return advarselKode;
    }

    public boolean isFlereOpplysninger() {
        return flereOpplysninger;
    }

    public static class Builder {
        private VarselOmRevurderingDokumentdata kladd;

        private Builder() {
            this.kladd = new VarselOmRevurderingDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medTerminDato(String terminDato) {
            this.kladd.terminDato = terminDato;
            return this;
        }

        public Builder medFristDato(String fristDato) {
            this.kladd.fristDato = fristDato;
            return this;
        }

        public Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public Builder medAdvarselKode(String advarselKode) {
            this.kladd.advarselKode = advarselKode;
            return this;
        }

        public Builder medFlereOpplysninger(boolean flereOpplysninger) {
            this.kladd.flereOpplysninger = flereOpplysninger;
            return this;
        }

        public VarselOmRevurderingDokumentdata build() {
            return this.kladd;
        }
    }
}
