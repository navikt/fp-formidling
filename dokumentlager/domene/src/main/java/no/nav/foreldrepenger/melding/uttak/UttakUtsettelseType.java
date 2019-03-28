package no.nav.foreldrepenger.melding.uttak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "UttakUtsettelseType")
@DiscriminatorValue(UttakUtsettelseType.DISCRIMINATOR)
public class UttakUtsettelseType extends Kodeliste {

    public static final String DISCRIMINATOR = "UTTAK_UTSETTELSE_TYPE";

    public static final UttakUtsettelseType ARBEID = new UttakUtsettelseType("ARBEID");
    public static final UttakUtsettelseType FERIE = new UttakUtsettelseType("FERIE");
    public static final UttakUtsettelseType SYKDOM_SKADE = new UttakUtsettelseType("SYKDOM_SKADE");
    public static final UttakUtsettelseType SØKER_INNLAGT = new UttakUtsettelseType("SØKER_INNLAGT");
    public static final UttakUtsettelseType BARN_INNLAGT = new UttakUtsettelseType("BARN_INNLAGT");
    public static final UttakUtsettelseType UDEFINERT = new UttakUtsettelseType("-");

    UttakUtsettelseType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    UttakUtsettelseType() {
        // For hibernate
    }
}
