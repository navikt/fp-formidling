package no.nav.foreldrepenger.melding.personopplysning;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "SivilstandType")
@DiscriminatorValue(SivilstandType.DISCRIMINATOR)
public class SivilstandType extends Kodeliste {
    public static final String DISCRIMINATOR = "SIVILSTAND_TYPE";

    public static final SivilstandType ENKEMANN = new SivilstandType("ENKE");  //$NON-NLS-1$
    public static final SivilstandType GIFT = new SivilstandType("GIFT");  //$NON-NLS-1$
    public static final SivilstandType GJENLEVENDE_PARTNER = new SivilstandType("GJPA");  //$NON-NLS-1$
    public static final SivilstandType GIFT_ADSKILT = new SivilstandType("GLAD");  //$NON-NLS-1$
    public static final SivilstandType UOPPGITT = new SivilstandType("NULL");  //$NON-NLS-1$
    public static final SivilstandType REGISTRERT_PARTNER = new SivilstandType("REPA");  //$NON-NLS-1$
    public static final SivilstandType SAMBOER = new SivilstandType("SAMB");  //$NON-NLS-1$
    public static final SivilstandType SEPARERT_PARTNER = new SivilstandType("SEPA");  //$NON-NLS-1$
    public static final SivilstandType SEPARERT = new SivilstandType("SEPR");  //$NON-NLS-1$
    public static final SivilstandType SKILT = new SivilstandType("SKIL");  //$NON-NLS-1$
    public static final SivilstandType SKILT_PARTNER = new SivilstandType("SKPA");  //$NON-NLS-1$
    public static final SivilstandType UGIFT = new SivilstandType("UGIF");  //$NON-NLS-1$


    protected SivilstandType() {
        // Hibernate trenger en
    }

    private SivilstandType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
