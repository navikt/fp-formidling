package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "Tema")
@DiscriminatorValue(Tema.DISCRIMINATOR)
public class Tema extends Kodeliste {

    public static final String DISCRIMINATOR = "TEMA";

    public static final Tema SYKEPENGER = new Tema("SYK");
    public static final Tema FORELDREPENGER = new Tema("FOR");
    public static final Tema OMSORGPLEIEROGOPPLÆRINGSPENGER = new Tema("OMS");

    /**
     * Alle kodeverk må ha en verdi, det kan ikke være null i databasen. Denne koden gjør samme nytten.
     */
    public static final Tema UDEFINERT = new Tema("-"); //$NON-NLS-1$

    public Tema() {
        // For Hibernate
    }

    public Tema(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
