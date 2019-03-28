package no.nav.foreldrepenger.melding.uttak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;

@Entity(name = "GraderingAvslagÅrsak")
@DiscriminatorValue(GraderingAvslagÅrsak.DISCRIMINATOR)
public class GraderingAvslagÅrsak extends ÅrsakskodeMedLovreferanse {
    public static final String DISCRIMINATOR = "GRADERING_AVSLAG_AARSAK";

    public static final GraderingAvslagÅrsak UKJENT = new GraderingAvslagÅrsak("-");
    public static final GraderingAvslagÅrsak GRADERING_FØR_UKE_7 = new GraderingAvslagÅrsak("4504");
    public static final GraderingAvslagÅrsak FOR_SEN_SØKNAD = new GraderingAvslagÅrsak("4501");
    public static final GraderingAvslagÅrsak MANGLENDE_GRADERINGSAVTALE = new GraderingAvslagÅrsak("4502");
    //TODO Ikke gyldig lenger, skal ikke brukes. Ligger for å støtte historiske behandlinger
    public static final GraderingAvslagÅrsak AVSLAG_PGA_100_PROSENT_ARBEID = new GraderingAvslagÅrsak("4523");

    GraderingAvslagÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    GraderingAvslagÅrsak() {
        // For hibernate
    }
}
