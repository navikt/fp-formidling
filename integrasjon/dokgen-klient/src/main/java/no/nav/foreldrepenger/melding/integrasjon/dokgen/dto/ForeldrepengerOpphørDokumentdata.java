package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForeldrepengerOpphørDokumentdata extends Dokumentdata {
    private boolean erSøkerDød;
    private String relasjonskode;
    private boolean gjelderFødsel;
    private int antallBarn;
    private long halvG;
    private String lovhjemmelForAvslag;
    private int antallÅrsaker;
    private List<String> avslagÅrsaker;
    private int klagefristUker;
    private String kontaktTelefonnummer;
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

    public int getAntallÅrsaker() {
        return antallÅrsaker;
    }

    public List<String> getAvslagÅrsaker() {
        return avslagÅrsaker;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public String getKontaktTelefonnummer() {
        return kontaktTelefonnummer;
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (ForeldrepengerOpphørDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(erSøkerDød, that.erSøkerDød)
                && Objects.equals(relasjonskode, that.relasjonskode)
                && Objects.equals(klagefristUker, that.klagefristUker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, erSøkerDød, relasjonskode, klagefristUker);
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

        public Builder medAntallÅrsaker(int antallÅrsaker) {
            this.kladd.antallÅrsaker = antallÅrsaker;
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

        public Builder medKontaktTelefonNummer(String kontaktTlf) {
            this.kladd.kontaktTelefonnummer = kontaktTlf;
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
