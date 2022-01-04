package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Beløp;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Frilanser {
    private Beløp månedsinntekt;

    public Beløp getMånedsinntekt() {
        return månedsinntekt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Frilanser) object;
        return Objects.equals(månedsinntekt, that.månedsinntekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(månedsinntekt);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static Builder ny(Frilanser eksisterende) {
        return new Builder(eksisterende);
    }

    public static class Builder {
        private Frilanser kladd;

        private Builder() {
            this.kladd = new Frilanser();
        }

        private Builder(Frilanser eksisterende) {
            this.kladd = new Frilanser();
            if (eksisterende != null) {
                this.kladd.månedsinntekt = eksisterende.getMånedsinntekt();
            }
        }

        public Builder leggTilMånedsinntekt(BigDecimal månedsinntekt) {
            if (this.kladd.månedsinntekt != null) {
                this.kladd.månedsinntekt = Beløp.of(this.kladd.månedsinntekt.getVerdi() + månedsinntekt.longValue());
            } else {
                this.kladd.månedsinntekt = Beløp.of(månedsinntekt);
            }
            return this;
        }

        public Frilanser build() {
            return this.kladd;
        }
    }
}