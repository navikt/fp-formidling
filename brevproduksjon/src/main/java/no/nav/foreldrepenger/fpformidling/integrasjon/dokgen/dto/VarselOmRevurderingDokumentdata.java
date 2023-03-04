package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VarselOmRevurderingDokumentdata extends Dokumentdata {
    private String terminDato;
    private String fristDato;
    private int antallBarn;
    private String advarselKode;
    private boolean flereOpplysninger;
    private boolean kreverSammenhengendeUttak;

    public String getTerminDato() {
        return terminDato;
    }

    public String getFristDato() {
        return fristDato;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public String getAdvarselKode() {
        return advarselKode;
    }

    public boolean getFlereOpplysninger() {
        return flereOpplysninger;
    }

    public boolean getKreverSammenhengendeUttak() {
        return kreverSammenhengendeUttak;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (VarselOmRevurderingDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(terminDato, that.terminDato) && Objects.equals(fristDato, that.fristDato)
            && Objects.equals(antallBarn, that.antallBarn) && Objects.equals(advarselKode, that.advarselKode) && Objects.equals(flereOpplysninger,
            that.flereOpplysninger) && Objects.equals(kreverSammenhengendeUttak, that.kreverSammenhengendeUttak);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, terminDato, fristDato, antallBarn, advarselKode, flereOpplysninger, kreverSammenhengendeUttak);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private VarselOmRevurderingDokumentdata kladd;

        private Builder() {
            this.kladd = new VarselOmRevurderingDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medTerminDato(String terminDato) {
            this.kladd.terminDato = terminDato;
            return this;
        }

        public Builder medFristDato(String fristDato) {
            this.kladd.fristDato = fristDato;
            return this;
        }

        public Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public Builder medAdvarselKode(String advarselKode) {
            this.kladd.advarselKode = advarselKode;
            return this;
        }

        public Builder medFlereOpplysninger(boolean flereOpplysninger) {
            this.kladd.flereOpplysninger = flereOpplysninger;
            return this;
        }

        public Builder medKreverSammenhengendeUttak(boolean kreverSammenhengendeUttak) {
            this.kladd.kreverSammenhengendeUttak = kreverSammenhengendeUttak;
            return this;
        }

        public VarselOmRevurderingDokumentdata build() {
            return this.kladd;
        }
    }
}
