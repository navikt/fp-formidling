package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OpphørPeriode {
    private Årsak årsak;
    @JsonIgnore
    private LocalDate stønadsperiodeFomDate;
    private String stønadsperiodeFom;
    @JsonIgnore
    private LocalDate stønadsperiodeTomDate;
    private String stønadsperiodeTom;
    @JsonIgnore
    private Språkkode språkkode;
    private List<String> arbeidsgivere;

    public Årsak getÅrsak() {
        return årsak;
    }

    public LocalDate getStønadsperiodeFomDate() {
        return stønadsperiodeFomDate;
    }

    public LocalDate getStønadsperiodeTomDate() {
        return stønadsperiodeTomDate;
    }

    public String getStønadsperiodeFom() {
        return stønadsperiodeFom;
    }

    public String getStønadsperiodeTom() {
        return stønadsperiodeTom;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    public List<String> getArbeidsgivere() {
        if (arbeidsgivere!= null) {
            return arbeidsgivere;
        }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (OpphørPeriode) object;
        return Objects.equals(årsak, that.årsak)
                && Objects.equals(stønadsperiodeFom, that.stønadsperiodeFom)
                && Objects.equals(stønadsperiodeTom, that.stønadsperiodeTom)
                && Objects.equals(arbeidsgivere, that.arbeidsgivere);
    }
    @Override
    public int hashCode() {
        return Objects.hash(årsak, stønadsperiodeFom, stønadsperiodeTom, arbeidsgivere);
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
            this.kladd.stønadsperiodeFomDate = date;
            this.kladd.språkkode = språkkode;
            return this;
        }

        public Builder medPeriodeTom(LocalDate date, Språkkode språkkode) {
            this.kladd.stønadsperiodeTom = formaterDato(date, språkkode);
            this.kladd.stønadsperiodeTomDate = date;
            this.kladd.språkkode = språkkode;
            return this;
        }

        public Builder medArbeidsgivere(List<String> arbeidsgivere) {
            this.kladd.arbeidsgivere = arbeidsgivere;
            return this;
        }

        public OpphørPeriode build() {
            return this.kladd;
        }
    }

}
