package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "PersonstatusType")
@DiscriminatorValue(PersonstatusType.DISCRIMINATOR)
public class PersonstatusType extends Kodeliste {

    public static final String DISCRIMINATOR = "PERSONSTATUS_TYPE";
    public static final PersonstatusType ABNR = new PersonstatusType("ABNR"); //$NON-NLS-1$
    public static final PersonstatusType ADNR = new PersonstatusType("ADNR"); //$NON-NLS-1$
    public static final PersonstatusType BOSA = new PersonstatusType("BOSA"); //$NON-NLS-1$
    public static final PersonstatusType DØD = new PersonstatusType("DØD"); //$NON-NLS-1$
    public static final PersonstatusType FOSV = new PersonstatusType("FOSV"); //$NON-NLS-1$
    public static final PersonstatusType FØDR = new PersonstatusType("FØDR"); //$NON-NLS-1$
    public static final PersonstatusType UFUL = new PersonstatusType("UFUL"); //$NON-NLS-1$
    public static final PersonstatusType UREG = new PersonstatusType("UREG"); //$NON-NLS-1$
    public static final PersonstatusType UTAN = new PersonstatusType("UTAN"); //$NON-NLS-1$
    public static final PersonstatusType UTPE = new PersonstatusType("UTPE"); //$NON-NLS-1$
    public static final PersonstatusType UTVA = new PersonstatusType("UTVA"); //$NON-NLS-1$

    public static final PersonstatusType UDEFINERT = new PersonstatusType("-"); //$NON-NLS-1$

    PersonstatusType() {
        // Hibernate trenger en
    }

    private PersonstatusType(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
