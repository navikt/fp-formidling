package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForeldrepengerOpphørDokumentdata extends Dokumentdata {
    private boolean erSøkerDød;
    private String relasjonskode;
    private boolean gjelderFødsel;
    private int antallBarn;
    private long halvG;
    private String lovhjemmelForAvslag;
    private List<String> avslagÅrsaker;
    private int klagefristUker;
    private String barnDødsdato;
    private String opphørDato;
    private String fomStønadsdato;
    private String tomStønadsdato;

    public boolean erSøkerDød() {
        return erSøkerDød;
    }

    public String getRelasjonskode() {
        return relasjonskode;
    }

    public boolean erGjelderFødsel() {
        return gjelderFødsel;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public long getHalvG() {
        return halvG;
    }

    public String getLovhjemmelForAvslag() {
        return lovhjemmelForAvslag;
    }

    public List<String> getAvslagÅrsaker() {
        return avslagÅrsaker;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public String getBarnDødsdato() {
        return barnDødsdato;
    }

    public String getOpphørDato() {
        return opphørDato;
    }

    public String getFomStønadsdato() {
        return fomStønadsdato;
    }

    public String getTomStønadsdato() {
        return tomStønadsdato;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ForeldrepengerOpphørDokumentdata that = (ForeldrepengerOpphørDokumentdata) o;
        return erSøkerDød() == that.erSøkerDød()
                && erGjelderFødsel() == that.erGjelderFødsel()
                && getAntallBarn() == that.getAntallBarn()
                && getHalvG() == that.getHalvG()
                && getKlagefristUker() == that.getKlagefristUker()
                && Objects.equals(getRelasjonskode(), that.getRelasjonskode())
                && Objects.equals(getLovhjemmelForAvslag(), that.getLovhjemmelForAvslag())
                && Objects.equals(getAvslagÅrsaker(), that.getAvslagÅrsaker())
                && Objects.equals(getBarnDødsdato(), that.getBarnDødsdato())
                && Objects.equals(getOpphørDato(), that.getOpphørDato())
                && Objects.equals(getFomStønadsdato(), that.getFomStønadsdato())
                && Objects.equals(getTomStønadsdato(), that.getTomStønadsdato())
                && Objects.equals(getFelles(), that.getFelles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFelles(), erSøkerDød(), getRelasjonskode(), erGjelderFødsel(), getAntallBarn(), getHalvG(), getLovhjemmelForAvslag(), getAvslagÅrsaker(), getKlagefristUker(), getBarnDødsdato(), getOpphørDato(), getFomStønadsdato(), getTomStønadsdato());
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private ForeldrepengerOpphørDokumentdata kladd;

        private Builder() {
            this.kladd = new ForeldrepengerOpphørDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medErSøkerDød(boolean erSøkerDød) {
            this.kladd.erSøkerDød = erSøkerDød;
            return this;
        }

        public Builder medRelasjonskode(String relasjonskode) {
            this.kladd.relasjonskode = relasjonskode;
            return this;
        }

        public Builder medGjelderFødsel(boolean erFødsel) {
            this.kladd.gjelderFødsel = erFødsel;
            return this;
        }

        public Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public Builder medHalvG(long halvG) {
            this.kladd.halvG = halvG;
            return this;
        }

        public Builder medLovhjemmelForAvslag(String lovhjemmelForAvslag) {
            this.kladd.lovhjemmelForAvslag = lovhjemmelForAvslag;
            return this;
        }

        public Builder medAvslagÅrsaker(List<String> avslagÅrsaker) {
            this.kladd.avslagÅrsaker = avslagÅrsaker;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medBarnDødsdato(String dødsDato) {
            this.kladd.barnDødsdato = dødsDato;
            return this;
        }

        public Builder medOpphørDato(String opphørDato) {
            this.kladd.opphørDato = opphørDato;
            return this;
        }

        public Builder medFomStønadsdato(String fomStønadsdato) {
            this.kladd.fomStønadsdato = fomStønadsdato;
            return this;
        }

        public Builder medTomStønadsdato(String tomStønadsdato) {
            this.kladd.tomStønadsdato = tomStønadsdato;
            return this;
        }

        public ForeldrepengerOpphørDokumentdata build() {
            return this.kladd;
        }
    }
}
