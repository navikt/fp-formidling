package no.nav.foreldrepenger.melding.geografisk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Landkoder")
@DiscriminatorValue(Landkoder.DISCRIMINATOR)
public class Landkoder extends Kodeliste {

    public static final String DISCRIMINATOR = "LANDKODER";

    public static final Landkoder UDEFINERT = new Landkoder("-"); //$NON-NLS-1$

    public static final Landkoder NOR = new Landkoder("NOR"); //$NON-NLS-1$
    public static final Landkoder SWE = new Landkoder("SWE"); //$NON-NLS-1$
    public static final Landkoder USA = new Landkoder("USA"); //$NON-NLS-1$
    public static final Landkoder PNG = new Landkoder("PNG"); //$NON-NLS-1$
    public static final Landkoder BEL = new Landkoder("BEL"); //$NON-NLS-1$


    Landkoder() {
        // Hibernate trenger en
    }

    private Landkoder(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
