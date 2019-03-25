package no.nav.foreldrepenger.melding.behandling;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "KonsekvensForYtelsen")
@DiscriminatorValue(KonsekvensForYtelsen.DISCRIMINATOR)
public class KonsekvensForYtelsen extends Kodeliste {

    public static final String DISCRIMINATOR = "KONSEKVENS_FOR_YTELSEN";

    public static final KonsekvensForYtelsen FORELDREPENGER_OPPHØRER = new KonsekvensForYtelsen("FORELDREPENGER_OPPHØRER"); //$NON-NLS-1$
    public static final KonsekvensForYtelsen ENDRING_I_BEREGNING = new KonsekvensForYtelsen("ENDRING_I_BEREGNING"); //$NON-NLS-1$
    public static final KonsekvensForYtelsen ENDRING_I_UTTAK = new KonsekvensForYtelsen("ENDRING_I_UTTAK"); //$NON-NLS-1$
    public static final KonsekvensForYtelsen ENDRING_I_FORDELING_AV_YTELSEN = new KonsekvensForYtelsen("ENDRING_I_FORDELING_AV_YTELSEN"); //$NON-NLS-1$
    public static final KonsekvensForYtelsen INGEN_ENDRING = new KonsekvensForYtelsen("INGEN_ENDRING"); //$NON-NLS-1$

    public static final KonsekvensForYtelsen UDEFINERT = new KonsekvensForYtelsen("-"); //$NON-NLS-1$

    public KonsekvensForYtelsen() {
        //
    }

    private KonsekvensForYtelsen(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
