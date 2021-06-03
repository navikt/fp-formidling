package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InfoTilAnnenForelderDokumentdata extends Dokumentdata {
    private String behandlingsÅrsak;
    private String sisteUttaksdagMor;

    public String getBehandlingsÅrsak() { return behandlingsÅrsak; }

    public String getSisteUttaksdagMor() { return sisteUttaksdagMor; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (InfoTilAnnenForelderDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(behandlingsÅrsak, that.behandlingsÅrsak)
                && Objects.equals(sisteUttaksdagMor, that.sisteUttaksdagMor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, behandlingsÅrsak, sisteUttaksdagMor);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private InfoTilAnnenForelderDokumentdata kladd;

        private Builder() {
            this.kladd = new InfoTilAnnenForelderDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medBehandlingÅrsak(String behandlingÅrsak) {
            this.kladd.behandlingsÅrsak = behandlingÅrsak;
            return this;
        }

        public Builder medSisteUttaksdagMor(String sisteUttaksdagMor) {
            this.kladd.sisteUttaksdagMor = sisteUttaksdagMor;
            return this;
        }

        public InfoTilAnnenForelderDokumentdata build() {
            return this.kladd;
        }
    }
}
