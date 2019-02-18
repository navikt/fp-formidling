package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity(name = "Språkkode")
@DiscriminatorValue(Språkkode.DISCRIMINATOR)
public class Språkkode extends Kodeliste {
    public static final String DISCRIMINATOR = "SPRAAK_KODE";
    public static final Språkkode nb = new Språkkode("NB"); //$NON-NLS-1$
    public static final Språkkode nn = new Språkkode("NN"); //$NON-NLS-1$
    public static final Språkkode en = new Språkkode("EN"); //$NON-NLS-1$
    public static final Språkkode UDEFINERT = new Språkkode("-");  //$NON-NLS-1$

    Språkkode() {
    }

    private Språkkode(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
