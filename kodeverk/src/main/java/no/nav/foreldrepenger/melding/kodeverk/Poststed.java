package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "Poststed")
@DiscriminatorValue(Poststed.DISCRIMINATOR)
public class Poststed extends Kodeliste {

    public static final String DISCRIMINATOR = "POSTSTED";
    public static final Poststed OSLO = new Poststed("0103");


    Poststed() {
        // Hibernate trenger den
    }

    public Poststed(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
