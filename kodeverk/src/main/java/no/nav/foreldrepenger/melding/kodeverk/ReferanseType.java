package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "ReferanseType")
@DiscriminatorValue(ReferanseType.DISCRIMINATOR)
public class ReferanseType extends Kodeliste {

    public static final String DISCRIMINATOR = "REFERANSE_TYPE";

    public static final ReferanseType ORG_NR = new ReferanseType("ORG_NR"); //$NON-NLS-1$
    public static final ReferanseType AKTØR_ID = new ReferanseType("AKTØR_ID"); //$NON-NLS-1$
    public static final ReferanseType UDEFINERT = new ReferanseType("-"); //$NON-NLS-1$

    public ReferanseType() {
        // fur hibernate
    }

    public ReferanseType(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
