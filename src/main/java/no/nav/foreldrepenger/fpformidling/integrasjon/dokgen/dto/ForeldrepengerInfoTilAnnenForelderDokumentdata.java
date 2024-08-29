package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForeldrepengerInfoTilAnnenForelderDokumentdata extends Dokumentdata {
    private String behandlingsÅrsak;
    private String sisteUttaksdagMor;

    public String getBehandlingsÅrsak() {
        return behandlingsÅrsak;
    }

    public String getSisteUttaksdagMor() {
        return sisteUttaksdagMor;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (ForeldrepengerInfoTilAnnenForelderDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(behandlingsÅrsak, that.behandlingsÅrsak) && Objects.equals(sisteUttaksdagMor,
            that.sisteUttaksdagMor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, behandlingsÅrsak, sisteUttaksdagMor);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private ForeldrepengerInfoTilAnnenForelderDokumentdata kladd;

        private Builder() {
            this.kladd = new ForeldrepengerInfoTilAnnenForelderDokumentdata();
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

        public ForeldrepengerInfoTilAnnenForelderDokumentdata build() {
            return this.kladd;
        }
    }
}
