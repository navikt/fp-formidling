package no.nav.foreldrepenger.melding.behandling;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "InnsynResultatType")
@DiscriminatorValue(InnsynResultatType.DISCRIMINATOR)
public class InnsynResultatType extends Kodeliste {

    public static final String DISCRIMINATOR = "INNSYN_RESULTAT_TYPE";

    public static final InnsynResultatType INNVILGET = new InnsynResultatType("INNV"); //$NON-NLS-1$
    public static final InnsynResultatType DELVIS_INNVILGET = new InnsynResultatType("DELV"); //$NON-NLS-1$
    public static final InnsynResultatType AVVIST = new InnsynResultatType("AVVIST"); //$NON-NLS-1$
    public static final InnsynResultatType UDEFINERT = new InnsynResultatType("-"); //$NON-NLS-1$


    InnsynResultatType() {
        // Hibernate trenger en
    }

    private InnsynResultatType(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
