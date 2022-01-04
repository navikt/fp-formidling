package no.nav.foreldrepenger.melding.fagsak;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Dekningsgrad for foreldrepenger.
 */
@Embeddable
public class Dekningsgrad {

    public static final Dekningsgrad _100 = new Dekningsgrad(100);
    public static final Dekningsgrad _80 = new Dekningsgrad(80);

    @Column(name = "dekningsgrad")
    private int verdi;

    @SuppressWarnings("unused")
    private Dekningsgrad() {
        // for hibernate
    }

    public Dekningsgrad(int verdi) {
        if (verdi < 0 || verdi > 100) {
            throw new IllegalArgumentException("!( 0 < value < 100 )");
        }
        this.verdi = verdi;
    }

    public static Dekningsgrad grad(int verdi) {
        return new Dekningsgrad(verdi);
    }

    public int getVerdi() {
        return verdi;
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Dekningsgrad)) {
            return false;
        } else if (arg0 == this) {
            return true;
        }

        Dekningsgrad other = (Dekningsgrad) arg0;
        return Objects.equals(verdi, other.verdi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verdi);
    }
}
