package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OpphørPeriode {
    private Årsak årsak;
    private String stønadsperiodeFom;
    private String stønadsperiodeTom;
    private int antallArbeidsgivere;

    public Årsak getÅrsak() {
        return årsak;
    }


    public String getStønadsperiodeFom() {
        return stønadsperiodeFom;
    }

    public String getStønadsperiodeTom() {
        return stønadsperiodeTom;
    }

    public int getAntallArbeidsgivere() {
            return antallArbeidsgivere;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (OpphørPeriode) object;
        return Objects.equals(årsak, that.årsak)
                && Objects.equals(stønadsperiodeFom, that.stønadsperiodeFom)
                && Objects.equals(stønadsperiodeTom, that.stønadsperiodeTom)
                && Objects.equals(antallArbeidsgivere, that.antallArbeidsgivere);
    }
    @Override
    public int hashCode() {
        return Objects.hash(årsak, stønadsperiodeFom, stønadsperiodeTom, antallArbeidsgivere);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private OpphørPeriode kladd;

        private Builder() {
            this.kladd = new OpphørPeriode();
        }

        public Builder medÅrsak(Årsak årsak) {
            this.kladd.årsak = årsak;
            return this;
        }

        public Builder medPeriodeFom(LocalDate date, Språkkode språkkode) {
            this.kladd.stønadsperiodeFom = formaterDato(date, språkkode);
            return this;
        }

        public Builder medPeriodeTom(LocalDate date, Språkkode språkkode) {
            this.kladd.stønadsperiodeTom = formaterDato(date, språkkode);
            return this;
        }

        public Builder medAntallArbeidsgivere(int antallArbeidsgivere) {
            this.kladd.antallArbeidsgivere = antallArbeidsgivere;
            return this;
        }

        public OpphørPeriode build() {
            return this.kladd;
        }
    }

}
