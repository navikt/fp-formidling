package no.nav.foreldrepenger.melding.verge;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "BrevMottaker")
@DiscriminatorValue(BrevMottaker.DISCRIMINATOR)
public class BrevMottaker extends Kodeliste {
    public static final String DISCRIMINATOR = "BREV_MOTTAKER";

    public static final BrevMottaker VERGE = new BrevMottaker("VERGE"); //$NON-NLS-1$   Verge skal motta brev
    public static final BrevMottaker SØKER = new BrevMottaker("SOEKER"); //$NON-NLS-1$  Søker skal motta brev
    public static final BrevMottaker BEGGE = new BrevMottaker("BEGGE"); //$NON-NLS-1$ Både verge og søker skal motta brev
    public static final BrevMottaker UDEFINERT = new BrevMottaker("-"); //$NON-NLS-1$

    public BrevMottaker() {
    }

    public BrevMottaker(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
