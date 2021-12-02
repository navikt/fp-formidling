package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;

import java.util.Objects;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SvangerskapspengerAvslagDokumentdata extends Dokumentdata {
    private Årsak årsak;
    private boolean erSøkerDød;
    private String stønadsdatoFom;
    private String mottattDato;
    private int antallArbeidsgivere;
    private long halvG;
    private String lovhjemmel;
    private int klagefristUker;

    public Årsak getÅrsak() {
        return årsak;
    }

    public boolean getErSøkerDød() {
        return erSøkerDød;
    }

    public String getStønadsdatoFom() {
        return stønadsdatoFom;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    public int getAntallArbeidsgivere() {
            return antallArbeidsgivere;
    }

    public long getHalvG() {
        return halvG;
    }

    public String getLovhjemmel() {
        return lovhjemmel;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (SvangerskapspengerAvslagDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(årsak, that.årsak)
                && Objects.equals(erSøkerDød, that.erSøkerDød)
                && Objects.equals(stønadsdatoFom, that.stønadsdatoFom)
                && Objects.equals(mottattDato, that.mottattDato)
                && Objects.equals(antallArbeidsgivere, that.antallArbeidsgivere)
                && Objects.equals(halvG, that.halvG)
                && Objects.equals(lovhjemmel, that.lovhjemmel)
                && Objects.equals(klagefristUker, that.klagefristUker);
    }
    @Override
    public int hashCode() {
        return Objects.hash(felles, årsak, erSøkerDød, stønadsdatoFom, mottattDato, antallArbeidsgivere, halvG, lovhjemmel, klagefristUker);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private SvangerskapspengerAvslagDokumentdata kladd;

        private Builder() {
            this.kladd = new SvangerskapspengerAvslagDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medÅrsak(Årsak årsak) {
            this.kladd.årsak = årsak;
            return this;
        }

        public Builder medErSøkerDød(boolean erSøkerDød) {
            this.kladd.erSøkerDød = erSøkerDød;
            return this;
        }

        public Builder medStønadsdatoFom(String stønadsdatoFom) {
            this.kladd.stønadsdatoFom = stønadsdatoFom;
            return this;
        }

        public Builder medMottattDato(String mottattDato) {
            this.kladd.mottattDato = mottattDato;
            return this;
        }

        public Builder medAntallArbeidsgivere(int antallArbeidsgivere) {
            this.kladd.antallArbeidsgivere = antallArbeidsgivere;
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

        public SvangerskapspengerAvslagDokumentdata build() {
            return this.kladd;
        }

    }
}
