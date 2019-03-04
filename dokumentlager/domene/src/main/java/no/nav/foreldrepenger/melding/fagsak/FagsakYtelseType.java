package no.nav.foreldrepenger.melding.fagsak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "FagsakYtelseType")
@DiscriminatorValue(FagsakYtelseType.DISCRIMINATOR)
public class FagsakYtelseType extends Kodeliste {

    public static final String DISCRIMINATOR = "FAGSAK_YTELSE"; //$NON-NLS-1$
    public static final FagsakYtelseType ENGANGSTØNAD = new FagsakYtelseType("ES"); //$NON-NLS-1$
    public static final FagsakYtelseType FORELDREPENGER = new FagsakYtelseType("FP"); //$NON-NLS-1$
    public static final FagsakYtelseType ENDRING_FORELDREPENGER = new FagsakYtelseType("ENDRING_FP"); //$NON-NLS-1$
    public static final FagsakYtelseType SVANGERSKAPSPENGER = new FagsakYtelseType("SVP"); //$NON-NLS-1$

    public static final FagsakYtelseType UDEFINERT = new FagsakYtelseType("-"); //$NON-NLS-1$

    FagsakYtelseType() {
        // Hibernate trenger den
    }

    public FagsakYtelseType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public final boolean gjelderEngangsstønad() {
        return ENGANGSTØNAD.getKode().equals(this.getKode());
    }

    public final boolean gjelderForeldrepenger() {
        return FORELDREPENGER.getKode().equals(this.getKode());
    }

}
