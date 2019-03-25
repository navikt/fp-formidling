package no.nav.foreldrepenger.melding.behandling.beregningsgrunnlag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "PeriodeÅrsak")
@DiscriminatorValue(PeriodeÅrsak.DISCRIMINATOR)
public class PeriodeÅrsak extends Kodeliste {

    public static final String DISCRIMINATOR = "PERIODE_AARSAK";

    public static final PeriodeÅrsak NATURALYTELSE_BORTFALT = new PeriodeÅrsak("NATURALYTELSE_BORTFALT"); //$NON-NLS-1$
    public static final PeriodeÅrsak ARBEIDSFORHOLD_AVSLUTTET = new PeriodeÅrsak("ARBEIDSFORHOLD_AVSLUTTET"); //$NON-NLS-1$
    public static final PeriodeÅrsak NATURALYTELSE_TILKOMMER = new PeriodeÅrsak("NATURALYTELSE_TILKOMMER"); //$NON-NLS-1$
    public static final PeriodeÅrsak ENDRING_I_REFUSJONSKRAV = new PeriodeÅrsak("ENDRING_I_REFUSJONSKRAV"); //$NON-NLS-1$
    public static final PeriodeÅrsak REFUSJON_OPPHØRER = new PeriodeÅrsak("REFUSJON_OPPHØRER"); //$NON-NLS-1$
    public static final PeriodeÅrsak GRADERING = new PeriodeÅrsak("GRADERING"); //$NON-NLS-1$

    public static final PeriodeÅrsak UDEFINERT = new PeriodeÅrsak("-"); //$NON-NLS-1$

    PeriodeÅrsak() {
        // Hibernate trenger en
    }

    private PeriodeÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
