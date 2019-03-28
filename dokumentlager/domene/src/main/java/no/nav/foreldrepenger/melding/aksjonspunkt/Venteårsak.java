package no.nav.foreldrepenger.melding.aksjonspunkt;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


@Entity(name = "Venteårsak")
@DiscriminatorValue(Venteårsak.DISCRIMINATOR)
public class Venteårsak extends Kodeliste {
    public static final String DISCRIMINATOR = "VENT_AARSAK";

    public static final Venteårsak VENT_PÅ_BRUKERTILBAKEMELDING = new Venteårsak("VENT_PÅ_BRUKERTILBAKEMELDING"); //$NON-NLS-1$
    public static final Venteårsak VENT_PÅ_TILBAKEKREVINGSGRUNNLAG = new Venteårsak("VENT_PÅ_TILBAKEKREVINGSGRUNNLAG"); //$NON-NLS-1$
    public static final Venteårsak UDEFINERT = new Venteårsak("-"); //$NON-NLS-1$

    public Venteårsak() {
    }

    public Venteårsak(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
