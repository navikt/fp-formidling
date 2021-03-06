package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class Prosent {

    public static final Prosent NULL = new Prosent(BigDecimal.ZERO);
    public static final Prosent HUNDRE = new Prosent(BigDecimal.valueOf(100));

    @JsonValue
    private BigDecimal verdi;

    private Prosent(BigDecimal verdi) {
        if (verdi == null || verdi.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Ugyldig prosent-verdi: " + (verdi == null ? "null" : verdi.toPlainString()));
        }
        this.verdi = verdi.setScale(1, RoundingMode.HALF_UP);;
    }

    public static Prosent of(BigDecimal verdi) {
        return new Prosent(verdi);
    }

    public boolean erStørreEnnHundreProsent() {
        return verdi.compareTo(HUNDRE.verdi) > 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Prosent) object;
        return Objects.equals(verdi, that.verdi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verdi);
    }

    @Override
    public String toString() {
        return "Prosent{" +
                "verdi=" + verdi.toPlainString() +
                '}';
    }
}
