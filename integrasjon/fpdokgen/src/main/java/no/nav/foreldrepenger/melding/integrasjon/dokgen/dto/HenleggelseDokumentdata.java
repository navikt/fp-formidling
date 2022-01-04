package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class HenleggelseDokumentdata extends Dokumentdata {
    private boolean vanligBehandling;
    private boolean klage;
    private boolean anke;
    private boolean innsyn;
    private String opphavType;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (HenleggelseDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(vanligBehandling, that.vanligBehandling)
                && Objects.equals(klage, that.klage)
                && Objects.equals(anke, that.anke)
                && Objects.equals(innsyn, that.innsyn)
                && Objects.equals(opphavType, that.opphavType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, vanligBehandling, klage, anke, innsyn, opphavType);
    }

    public static Builder ny() {
        return new Builder();
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
