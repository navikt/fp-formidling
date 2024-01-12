package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForlengetSaksbehandlingstidDokumentdata extends Dokumentdata {
    private VariantType variantType;
    private boolean død;
    private int behandlingsfristUker;
    private int antallBarn;

    public VariantType getVariantType() {
        return variantType;
    }

    public boolean getDød() {
        return død;
    }

    public int getBehandlingsfristUker() {
        return behandlingsfristUker;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (ForlengetSaksbehandlingstidDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(variantType, that.variantType) && Objects.equals(død, that.død)
            && Objects.equals(behandlingsfristUker, that.behandlingsfristUker) && Objects.equals(antallBarn, that.antallBarn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, variantType, død, behandlingsfristUker, antallBarn);
    }

    public enum VariantType {
        FORLENGET,
        MEDLEM,
        FORTIDLIG,
        KLAGE;
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private ForlengetSaksbehandlingstidDokumentdata kladd;

        private Builder() {
            this.kladd = new ForlengetSaksbehandlingstidDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medVariantType(VariantType variantType) {
            this.kladd.variantType = variantType;
            return this;
        }

        public Builder medDød(boolean død) {
            this.kladd.død = død;
            return this;
        }

        public Builder medBehandlingsfristUker(int behandlingsfristUker) {
            this.kladd.behandlingsfristUker = behandlingsfristUker;
            return this;
        }

        public Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public ForlengetSaksbehandlingstidDokumentdata build() {
            return this.kladd;
        }
    }
}
