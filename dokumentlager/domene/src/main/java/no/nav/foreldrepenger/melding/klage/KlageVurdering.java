package no.nav.foreldrepenger.melding.klage;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "KlageVurdering")
@DiscriminatorValue(KlageVurdering.DISCRIMINATOR)
public class KlageVurdering extends Kodeliste {

    public static final String DISCRIMINATOR = "KLAGEVURDERING";
    public static final KlageVurdering OPPHEVE_YTELSESVEDTAK = new KlageVurdering("OPPHEVE_YTELSESVEDTAK"); //$NON-NLS-1$
    public static final KlageVurdering STADFESTE_YTELSESVEDTAK = new KlageVurdering("STADFESTE_YTELSESVEDTAK"); //$NON-NLS-1$
    public static final KlageVurdering MEDHOLD_I_KLAGE = new KlageVurdering("MEDHOLD_I_KLAGE"); //$NON-NLS-1$
    public static final KlageVurdering AVVIS_KLAGE = new KlageVurdering("AVVIS_KLAGE"); //$NON-NLS-1$
    public static final KlageVurdering HJEMSENDE_UTEN_Å_OPPHEVE = new KlageVurdering("HJEMSENDE_UTEN_Å_OPPHEVE"); //$NON-NLS-1$
    public static final KlageVurdering UDEFINERT = new KlageVurdering("-"); //$NON-NLS-1$


    public KlageVurdering() {
        // for hibernate
    }

    private KlageVurdering(String kode) {
        super(kode, DISCRIMINATOR);
    }

}
