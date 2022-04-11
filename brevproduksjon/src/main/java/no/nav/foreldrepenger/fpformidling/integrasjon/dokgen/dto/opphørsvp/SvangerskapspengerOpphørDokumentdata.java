package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SvangerskapspengerOpphørDokumentdata extends Dokumentdata {
    private String opphørsdato;
    private boolean erSøkerDød;
    private String dødsdatoBarn;
    private String fødselsdato;
    private long halvG;
    private String lovhjemmel;
    private OpphørPeriode opphørtPeriode;
    private int klagefristUker;

    public String getOpphørsdato() {
        return opphørsdato;
    }

    public String getDødsdatoBarn() {
        return dødsdatoBarn;
    }

    public String getFødselsdato() {
        return fødselsdato;
    }

    public boolean getErSøkerDød() {
        return erSøkerDød;
    }

    public long getHalvG() {
        return halvG;
    }

    public String getLovhjemmel() {
        return lovhjemmel;
    }

    public OpphørPeriode getOpphørtPeriode() {
        return opphørtPeriode;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (SvangerskapspengerOpphørDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(opphørsdato, that.opphørsdato)
                && Objects.equals(dødsdatoBarn, that.dødsdatoBarn)
                && Objects.equals(fødselsdato, that.fødselsdato)
                && Objects.equals(erSøkerDød, that.erSøkerDød)
                && Objects.equals(halvG, that.halvG)
                && Objects.equals(lovhjemmel, that.lovhjemmel)
                && Objects.equals(opphørtPeriode, that.opphørtPeriode)
                && Objects.equals(klagefristUker, that.klagefristUker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, opphørsdato, dødsdatoBarn, fødselsdato, erSøkerDød, halvG, lovhjemmel, opphørtPeriode, klagefristUker);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private SvangerskapspengerOpphørDokumentdata kladd;

        private Builder() {
            this.kladd = new SvangerskapspengerOpphørDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medOpphørsdato(String opphørsdato) {
            this.kladd.opphørsdato = opphørsdato;
            return this;
        }

        public Builder medDødsdatoBarn(String dødsdato) {
            this.kladd.dødsdatoBarn = dødsdato;
            return this;
        }

        public Builder medFødselsdato(String fødselsdato) {
            this.kladd.fødselsdato = fødselsdato;
            return this;
        }

        public Builder medErSøkerDød(boolean erSøkerDød) {
            this.kladd.erSøkerDød = erSøkerDød;
            return this;
        }

        public Builder medHalvG(long halvG) {
            this.kladd.halvG = halvG;
            return this;
        }

        public Builder medLovhjemmel(String lovhjemmel) {
            this.kladd.lovhjemmel = lovhjemmel;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medOpphørPerioder(OpphørPeriode opphørtPeriode) {
            this.kladd.opphørtPeriode = opphørtPeriode;
            return this;
        }

        public SvangerskapspengerOpphørDokumentdata build() {
            return this.kladd;
        }
    }
}
