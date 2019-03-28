package no.nav.foreldrepenger.melding.uttak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Stønadskontotype")
@DiscriminatorValue(StønadskontoType.DISCRIMINATOR)
public class StønadskontoType extends Kodeliste {

    public static final String DISCRIMINATOR = "STOENADSKONTOTYPE";

    public static final StønadskontoType FELLESPERIODE = new StønadskontoType("FELLESPERIODE");
    public static final StønadskontoType MØDREKVOTE = new StønadskontoType("MØDREKVOTE");
    public static final StønadskontoType FEDREKVOTE = new StønadskontoType("FEDREKVOTE");
    public static final StønadskontoType FORELDREPENGER = new StønadskontoType("FORELDREPENGER");
    public static final StønadskontoType FLERBARNSDAGER = new StønadskontoType("FLERBARNSDAGER");
    public static final StønadskontoType FORELDREPENGER_FØR_FØDSEL = new StønadskontoType("FORELDREPENGER_FØR_FØDSEL");

    public static final StønadskontoType UDEFINERT = new StønadskontoType("-");

    StønadskontoType() {
        // For hibernate
    }

    public StønadskontoType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
