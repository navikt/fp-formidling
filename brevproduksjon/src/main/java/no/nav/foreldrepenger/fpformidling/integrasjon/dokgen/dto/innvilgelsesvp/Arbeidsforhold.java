package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Arbeidsforhold {
    private String arbeidsgiverNavn;
    private long månedsinntekt;

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public long getMånedsinntekt() {
        return månedsinntekt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Arbeidsforhold) object;
        return Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn)
                && Objects.equals(månedsinntekt, that.månedsinntekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverNavn, månedsinntekt);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private Arbeidsforhold kladd;

        private Builder() {
            this.kladd = new Arbeidsforhold();
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medMånedsinntekt(long månedsinntekt) {
            this.kladd.månedsinntekt = månedsinntekt;
            return this;
        }

        public Arbeidsforhold build() {
            return this.kladd;
        }
    }
}