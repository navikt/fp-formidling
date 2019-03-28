package no.nav.foreldrepenger.melding.uttak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;

@Entity(name = "PeriodeResultatAarsak")
@DiscriminatorValue(PeriodeResultatÅrsak.DISCRIMINATOR)
public class PeriodeResultatÅrsak extends ÅrsakskodeMedLovreferanse {
    public static final String DISCRIMINATOR = "PERIODE_RESULTAT_AARSAK";

    public static final PeriodeResultatÅrsak UKJENT = new PeriodeResultatÅrsak("-");

    @Transient
    private String lovReferanse;

    PeriodeResultatÅrsak() {
        // For hibernate
    }

    PeriodeResultatÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    protected PeriodeResultatÅrsak(String kode, String discriminator) {
        super(kode, discriminator);
    }
}
