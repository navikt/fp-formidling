package no.nav.foreldrepenger.melding.ytelsefordeling;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Årsak")
@DiscriminatorValue(Årsak.DISCRIMINATOR)
public class Årsak extends Kodeliste {
    public static final String DISCRIMINATOR = "AARSAK_TYPE";

    public static final Årsak UDEFINERT = new Årsak("-");

    Årsak() {
    }

    Årsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    Årsak(String kode, String discriminator) {
        super(kode, discriminator);
    }
}
