package no.nav.foreldrepenger.melding.geografisk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Region")
@DiscriminatorValue(Region.DISCRIMINATOR)
public class Region extends Kodeliste {

    public static final String DISCRIMINATOR = "REGION";
    public static final Region NORDEN = new Region("NORDEN"); //$NON-NLS-1$
    public static final Region EOS = new Region("EOS"); //$NON-NLS-1$
    public static final Region TREDJELANDS_BORGER = new Region("ANNET"); //$NON-NLS-1$

    public static final Region UDEFINERT = new Region("-"); //$NON-NLS-1$

    Region() {
        // Hibernate trenger en
    }

    private Region(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
