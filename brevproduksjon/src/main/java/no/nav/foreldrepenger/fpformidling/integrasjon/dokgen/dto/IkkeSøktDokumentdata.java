package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IkkeSøktDokumentdata extends Dokumentdata {
    private String arbeidsgiverNavn;
    private String mottattDato;

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (IkkeSøktDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn)
                && Objects.equals(mottattDato, that.mottattDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, arbeidsgiverNavn, mottattDato);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private IkkeSøktDokumentdata kladd;

        private Builder() {
            this.kladd = new IkkeSøktDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medMottattDato(String mottattDato) {
            this.kladd.mottattDato = mottattDato;
            return this;
        }

        public IkkeSøktDokumentdata build() {
            return this.kladd;
        }
    }
}
