package no.nav.foreldrepenger.melding.geografisk;

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


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

    private static final Map<String, Språkkode> VERDIER = Map.of(nb.getKode(), nb, nn.getKode(), nn, en.getKode(), en);

    public static final Språkkode defaultNorsk(String kode) {
        return VERDIER.getOrDefault(kode, Språkkode.nb);
    }
}
