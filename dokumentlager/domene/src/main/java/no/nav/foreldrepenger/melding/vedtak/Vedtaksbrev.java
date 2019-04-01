package no.nav.foreldrepenger.melding.vedtak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Vedtaksbrev")
@DiscriminatorValue(Vedtaksbrev.DISCRIMINATOR)
public class Vedtaksbrev extends Kodeliste {

    public static final String DISCRIMINATOR = "VEDTAKSBREV";

    public static final Vedtaksbrev AUTOMATISK = new Vedtaksbrev("AUTOMATISK"); //$NON-NLS-1$
    public static final Vedtaksbrev FRITEKST = new Vedtaksbrev("FRITEKST"); //$NON-NLS-1$
    public static final Vedtaksbrev INGEN = new Vedtaksbrev("INGEN"); //$NON-NLS-1$

    public static final Vedtaksbrev UDEFINERT = new Vedtaksbrev("-"); //$NON-NLS-1$

    public Vedtaksbrev() {
        //
    }

    private Vedtaksbrev(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
