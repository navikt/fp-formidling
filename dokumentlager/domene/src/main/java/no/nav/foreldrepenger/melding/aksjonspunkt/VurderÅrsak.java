package no.nav.foreldrepenger.melding.aksjonspunkt;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


@Entity(name = "VurderÅrsak")
@DiscriminatorValue(VurderÅrsak.DISCRIMINATOR)
public class VurderÅrsak extends Kodeliste {

    public static final String DISCRIMINATOR = "VURDER_AARSAK";

    public static final VurderÅrsak FEIL_FAKTA = new VurderÅrsak("FEIL_FAKTA"); //$NON-NLS-1$
    public static final VurderÅrsak FEIL_LOV = new VurderÅrsak("FEIL_LOV"); //$NON-NLS-1$
    public static final VurderÅrsak FEIL_REGEL = new VurderÅrsak("FEIL_REGEL"); //$NON-NLS-1$
    public static final VurderÅrsak ANNET = new VurderÅrsak("ANNET"); //$NON-NLS-1$

    public static final VurderÅrsak UDEFINERT = new VurderÅrsak("-"); //$NON-NLS-1$

    public VurderÅrsak() {
    }

    public VurderÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public VurderÅrsak(VurderÅrsak vurderÅrsak) {
        this(vurderÅrsak.getKode());
    }

}
