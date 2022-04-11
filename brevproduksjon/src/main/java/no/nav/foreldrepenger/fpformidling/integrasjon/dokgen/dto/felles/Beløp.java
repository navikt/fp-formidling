package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Sikrer at beløp avrundes riktig.
 */
public class Beløp {

    @JsonValue
    private long verdi;

    private Beløp(BigDecimal verdi) {
        this.verdi = verdi.setScale(1, RoundingMode.HALF_UP).longValue();
    }

    @JsonCreator
    public static Beløp of(BigDecimal verdi) {
        if (verdi == null) {
            throw new IllegalStateException("Ugyldig verdi null");
        }
        return new Beløp(verdi);
    }

    @JsonCreator
    public static Beløp of(long verdi) {
        return new Beløp(BigDecimal.valueOf(verdi));
    }

    @JsonCreator
    public static Beløp of(double verdi) {
        return new Beløp(BigDecimal.valueOf(verdi));
    }

    public long getVerdi() {
        return verdi;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Beløp) object;
        return Objects.equals(verdi, that.verdi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verdi);
    }

    @Override
    public String toString() {
        return "Beløp{" +
                "verdi=" + verdi +
                '}';
    }
}
