package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.ArbeidsforholdInntektsmelding;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EtterlysInntektsmeldingDokumentdata extends Dokumentdata {
    private String søknadDato;

    private int antallIkkeMottattIM;
    private int antallMottattIM;
    private List<ArbeidsforholdInntektsmelding> inntektsmeldingerStatus;

    public String getSøknadDato() {
        return søknadDato;
    }

    public int getAntallIkkeMottattIM() {
        return antallIkkeMottattIM;
    }

    public int getAntallMottattIM() {
        return antallMottattIM;
    }
    public List<ArbeidsforholdInntektsmelding> getInntektsmeldingerStatus() {
        return inntektsmeldingerStatus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var that = (EtterlysInntektsmeldingDokumentdata) o;
        return Objects.equals(getSøknadDato(), that.getSøknadDato()) && Objects.equals(getInntektsmeldingerStatus(), that.getInntektsmeldingerStatus()) && Objects.equals(getAntallIkkeMottattIM(), that.getAntallIkkeMottattIM())
            && Objects.equals(getAntallMottattIM(), that.getAntallMottattIM());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSøknadDato(), getInntektsmeldingerStatus());
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

        public Builder medSøknadDato(String søknadDato) {
            this.kladd.søknadDato = søknadDato;
            return this;
        }

        public Builder medAntallIkkeMottattIM(int antallIkkeMottattIM) {
            this.kladd.antallIkkeMottattIM = antallIkkeMottattIM;
            return this;
        }

        public Builder medAntallMottattIM(int antallMottattIM) {
            this.kladd.antallMottattIM = antallMottattIM;
            return this;
        }

        public Builder medInntektsmeldingerStatus(List<ArbeidsforholdInntektsmelding> inntektsmeldingerStatus) {
            this.kladd.inntektsmeldingerStatus = inntektsmeldingerStatus;
            return this;
        }

        public EtterlysInntektsmeldingDokumentdata build() {
            return this.kladd;
        }
    }
}
