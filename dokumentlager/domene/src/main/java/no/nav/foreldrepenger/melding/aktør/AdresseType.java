package no.nav.foreldrepenger.melding.aktør;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "AdresseType")
@DiscriminatorValue(AdresseType.DISCRIMINATOR)
public class AdresseType extends Kodeliste {

    public static final String DISCRIMINATOR = "ADRESSE_TYPE";

    public static final AdresseType BOSTEDSADRESSE = new AdresseType("BOSTEDSADRESSE");  //$NON-NLS-1$
    public static final AdresseType POSTADRESSE = new AdresseType("POSTADRESSE");  //$NON-NLS-1$
    public static final AdresseType POSTADRESSE_UTLAND = new AdresseType("POSTADRESSE_UTLAND");  //$NON-NLS-1$
    public static final AdresseType MIDLERTIDIG_POSTADRESSE_NORGE = new AdresseType("MIDLERTIDIG_POSTADRESSE_NORGE");  //$NON-NLS-1$
    public static final AdresseType MIDLERTIDIG_POSTADRESSE_UTLAND = new AdresseType("MIDLERTIDIG_POSTADRESSE_UTLAND");  //$NON-NLS-1$
    public static final AdresseType UKJENT_ADRESSE = new AdresseType("UKJENT_ADRESSE");  //$NON-NLS-1$

    public static final List<AdresseType> kjentePostadressetyper = Collections.unmodifiableList(
            Arrays.asList(
                    AdresseType.BOSTEDSADRESSE,
                    AdresseType.POSTADRESSE,
                    AdresseType.POSTADRESSE_UTLAND,
                    AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE,
                    AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND,
                    AdresseType.UKJENT_ADRESSE
            )
    );

    public AdresseType() {
        // Hibernate trenger en
    }

    public AdresseType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
