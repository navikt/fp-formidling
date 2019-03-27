package no.nav.foreldrepenger.melding.uttak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "PeriodeResultatType")
@DiscriminatorValue(PeriodeResultatType.DISCRIMINATOR)
public class PeriodeResultatType extends Kodeliste {
    public static final String DISCRIMINATOR = "PERIODE_RESULTAT_TYPE";

    public static final PeriodeResultatType INNVILGET = new PeriodeResultatType("INNVILGET");
    public static final PeriodeResultatType AVSLÅTT = new PeriodeResultatType("AVSLÅTT");
    public static final PeriodeResultatType IKKE_FASTSATT = new PeriodeResultatType("IKKE_FASTSATT");
    public static final PeriodeResultatType MANUELL_BEHANDLING = new PeriodeResultatType("MANUELL_BEHANDLING");
    /**
     * @deprecated (TODO : denne kan fjernes når beregning har sluttet å bruke den ( husk å slette fra kodeverk også))
     */
    @Deprecated
    public static final PeriodeResultatType GYLDIG_UTSETTELSE = new PeriodeResultatType("GYLDIG_UTSETTELSE");
    /**
     * @deprecated (TODO : denne kan fjernes når beregning har sluttet å bruke den ( husk å slette fra kodeverk også))
     */
    @Deprecated
    public static final PeriodeResultatType UGYLDIG_UTSETTELSE = new PeriodeResultatType("UGYLDIG_UTSETTELSE");

    PeriodeResultatType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    PeriodeResultatType() {
        // For hibernate
    }
}
