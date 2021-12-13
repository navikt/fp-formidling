package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EtterlysInntektsmeldingDokumentdata extends Dokumentdata {
    private String søknadDato;
    private String fristDato;

    public String getSøknadDato() {
        return søknadDato;
    }

    public String getFristDato() {
        return fristDato;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EtterlysInntektsmeldingDokumentdata that = (EtterlysInntektsmeldingDokumentdata) o;
        return Objects.equals(getSøknadDato(), that.getSøknadDato()) && Objects.equals(getFristDato(), that.getFristDato());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSøknadDato(), getFristDato());
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private EtterlysInntektsmeldingDokumentdata kladd;

        private Builder() {
            this.kladd = new EtterlysInntektsmeldingDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medSøknadsdato(String søknadDato) {
            this.kladd.søknadDato = søknadDato;
            return this;
        }

        public Builder medFristDato(String fristDato) {
            this.kladd.fristDato = fristDato;
            return this;
        }

        public EtterlysInntektsmeldingDokumentdata build() {
            return this.kladd;
        }
    }
}
