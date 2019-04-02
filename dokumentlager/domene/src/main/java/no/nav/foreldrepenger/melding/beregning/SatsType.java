package no.nav.foreldrepenger.melding.beregning;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "SatsType")
@DiscriminatorValue(SatsType.DISCRIMINATOR)
public class SatsType extends Kodeliste {
    public static final String DISCRIMINATOR = "SATS_TYPE";

    public static final SatsType ENGANG = new SatsType("ENGANG"); //$NON-NLS-1$
    public static final SatsType GRUNNBELØP = new SatsType("GRUNNBELØP"); //$NON-NLS-1$
    public static final SatsType GSNITT = new SatsType("GSNITT"); //$NON-NLS-1$
    public static final SatsType UDEFINERT = new SatsType("-"); //$NON-NLS-1$

    SatsType() {
        // Hibernate trenger den
    }

    private SatsType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
