package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AvslåttAktivitet {
    private Årsak årsak;
    private String arbeidsgiverNavn;
    private boolean erFL;
    private boolean erSN;

    public Årsak getÅrsak() {
        return årsak;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public boolean getErFL() {
        return erFL;
    }

    public boolean getErSN() {
        return erSN;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (AvslåttAktivitet) object;
        return Objects.equals(årsak, that.årsak) && Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn) && Objects.equals(erFL, that.erFL)
            && Objects.equals(erSN, that.erSN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(årsak, arbeidsgiverNavn, erFL, erSN);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private AvslåttAktivitet kladd;

        private Builder() {
            this.kladd = new AvslåttAktivitet();
        }

        public Builder medÅrsak(Årsak årsak) {
            this.kladd.årsak = årsak;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medErFL(boolean erFL) {
            this.kladd.erFL = erFL;
            return this;
        }

        public Builder medErSN(boolean erSN) {
            this.kladd.erSN = erSN;
            return this;
        }

        public AvslåttAktivitet build() {
            return this.kladd;
        }
    }
}
