package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class IngenEndringDokumentdata extends Dokumentdata {

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private IngenEndringDokumentdata kladd;

        private Builder() {
            this.kladd = new IngenEndringDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public IngenEndringDokumentdata build() {
            return this.kladd;
        }
    }
}
