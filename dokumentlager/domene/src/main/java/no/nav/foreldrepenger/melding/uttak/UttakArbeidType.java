package no.nav.foreldrepenger.melding.uttak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "UttakArbeidType")
@DiscriminatorValue(UttakArbeidType.DISCRIMINATOR)
public class UttakArbeidType extends Kodeliste {

    public static final String DISCRIMINATOR = "UTTAK_ARBEID_TYPE";

    public static final UttakArbeidType ORDINÆRT_ARBEID = new UttakArbeidType("ORDINÆRT_ARBEID");
    public static final UttakArbeidType SELVSTENDIG_NÆRINGSDRIVENDE = new UttakArbeidType("SELVSTENDIG_NÆRINGSDRIVENDE");
    public static final UttakArbeidType FRILANS = new UttakArbeidType("FRILANS");
    public static final UttakArbeidType ARBEIDSAVKLARINGSPENGER = new UttakArbeidType("ARBEIDSAVKLARINGSPENGER");
    public static final UttakArbeidType ANNET = new UttakArbeidType("ANNET");

    UttakArbeidType() {
        // For hibernate
    }

    public UttakArbeidType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public boolean erArbeidstakerEllerFrilans() {
        return ORDINÆRT_ARBEID.equals(this) || FRILANS.equals(this);
    }
}
