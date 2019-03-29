package no.nav.foreldrepenger.melding.typer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import no.nav.foreldrepenger.melding.kodeverk.diff.ChangeTracked;
import no.nav.foreldrepenger.melding.kodeverk.diff.IndexKey;

/**
 * Beløp representerer kombinasjon av kroner og øre på standardisert format
 */
@Embeddable
public class Beløp implements Serializable, IndexKey {
    public static final Beløp ZERO = new Beløp(BigDecimal.ZERO);
    private static final RoundingMode AVRUNDINGSMODUS = RoundingMode.HALF_EVEN;

    @Column(name = "beloep", scale = 2)
    @ChangeTracked
    private BigDecimal verdi;

    protected Beløp() {
        // for hibernate
    }

    public Beløp(BigDecimal verdi) {
        this.verdi = verdi;
    }


    // Beleilig å kunne opprette gjennom int
    public Beløp(Integer verdi) {
        this.verdi = verdi == null ? null : new BigDecimal(verdi);
    }

    // Beleilig å kunne opprette gjennom string
    public Beløp(String verdi) {
        this.verdi = verdi == null ? null : new BigDecimal(verdi);
    }

    private BigDecimal skalertVerdi() {
        return verdi == null ? null : verdi.setScale(2, AVRUNDINGSMODUS);
    }

    @Override
    public String getIndexKey() {
        return skalertVerdi().toString();
    }

    public BigDecimal getVerdi() {
        return verdi;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        Beløp other = (Beløp) obj;
        return Objects.equals(skalertVerdi(), other.skalertVerdi());
    }

    @Override
    public int hashCode() {
        return Objects.hash(skalertVerdi());
    }

    @Override
    public String toString() {
        return "Beløp{" +
                "verdi=" + verdi +
                ", skalertVerdi=" + skalertVerdi() +
                '}';
    }

    public int compareTo(Beløp annetBeløp) {
        return verdi.compareTo(annetBeløp.getVerdi());
    }

    public boolean erNullEllerNulltall() {
        return verdi == null || erNulltall();
    }

    public boolean erNulltall() {
        return verdi != null && compareTo(Beløp.ZERO) == 0;
    }

    public Beløp multipliser(int multiplicand) {
        return new Beløp(this.verdi.multiply(BigDecimal.valueOf(multiplicand)));
    }

    public Beløp multipliser(double multiplicand) {
        return new Beløp(this.verdi.multiply(BigDecimal.valueOf(multiplicand)));
    }

    public Beløp adder(Beløp augend) {
        return new Beløp(this.verdi.add(augend.getVerdi()));
    }
}
