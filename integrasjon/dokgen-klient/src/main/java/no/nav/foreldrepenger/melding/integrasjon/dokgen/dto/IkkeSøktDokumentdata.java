package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class IkkeSøktDokumentdata extends Dokumentdata {
    private String arbeidsgiverNavn;
    private String mottattDato;

    public static Builder ny() {
        return new Builder();
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    public static class Builder {
        private IkkeSøktDokumentdata kladd;

        private Builder() {
            this.kladd = new IkkeSøktDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medMottattDato(String mottattDato) {
            this.kladd.mottattDato = mottattDato;
            return this;
        }

        public IkkeSøktDokumentdata build() {
            return this.kladd;
        }
    }
}
