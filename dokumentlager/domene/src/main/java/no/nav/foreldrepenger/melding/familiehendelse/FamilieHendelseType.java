package no.nav.foreldrepenger.melding.familiehendelse;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "FamilieHendelseType")
@DiscriminatorValue(FamilieHendelseType.DISCRIMINATOR)
public class FamilieHendelseType extends Kodeliste {

    public static final String DISCRIMINATOR = "FAMILIE_HENDELSE_TYPE";

    public static final FamilieHendelseType ADOPSJON = new FamilieHendelseType("ADPSJN"); //$NON-NLS-1$   Adopsjon
    public static final FamilieHendelseType OMSORG = new FamilieHendelseType("OMSRGO"); //$NON-NLS-1$  Omsorgoverdragelse
    public static final FamilieHendelseType FØDSEL = new FamilieHendelseType("FODSL"); //$NON-NLS-1$  fodsel
    public static final FamilieHendelseType TERMIN = new FamilieHendelseType("TERM"); //$NON-NLS-1$  termin
    public static final FamilieHendelseType UDEFINERT = new FamilieHendelseType("-"); //$NON-NLS-1$

    public FamilieHendelseType() {
    }

    public FamilieHendelseType(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
