package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity(name = "SkjermlenkeType")
@DiscriminatorValue(SkjermlenkeType.DISCRIMINATOR)
public class SkjermlenkeType extends Kodeliste {

    public static final String DISCRIMINATOR = "SKJERMLENKE_TYPE"; //$NON-NLS-1$

    public static final SkjermlenkeType UDEFINERT = new SkjermlenkeType("-");


    public SkjermlenkeType() {
        //
    }

    public SkjermlenkeType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
