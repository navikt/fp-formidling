package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class InfoTilAnnenForelderDokumentdata extends Dokumentdata{
    private String behandlingsÅrsak;
    private String sisteUttaksdagMor;

    public static InfoTilAnnenForelderDokumentdata.Builder ny() { return new InfoTilAnnenForelderDokumentdata.Builder(); }

    public String getBehandlingsÅrsak() { return behandlingsÅrsak; }

    public String getSisteUttaksdagMor() { return sisteUttaksdagMor; }

    public static class Builder {
        private InfoTilAnnenForelderDokumentdata kladd;

        private Builder() {
            this.kladd = new InfoTilAnnenForelderDokumentdata();
        }

        public InfoTilAnnenForelderDokumentdata.Builder medFellesDokumentData(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public InfoTilAnnenForelderDokumentdata.Builder medBehandlingÅrsak(String behandlingÅrsak) {
            this.kladd.behandlingsÅrsak = behandlingÅrsak;
            return this;
        }

        public InfoTilAnnenForelderDokumentdata.Builder medSisteUttaksdagMor(String sisteUttaksdagMor) {
            this.kladd.sisteUttaksdagMor = sisteUttaksdagMor;
            return this;
        }

        public InfoTilAnnenForelderDokumentdata build() {
            return this.kladd;
        }
    }

}
