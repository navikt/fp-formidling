package no.nav.foreldrepenger.melding.klage;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;

@Entity(name = "KlageAvvistÅrsak")
@DiscriminatorValue(KlageAvvistÅrsak.DISCRIMINATOR)
public class KlageAvvistÅrsak extends ÅrsakskodeMedLovreferanse {

    public static final String DISCRIMINATOR = "KLAGE_AVVIST_AARSAK";
    private static final Map<String, KlageAvvistÅrsak> REG_KODER = new HashMap<>();

    public static final KlageAvvistÅrsak KLAGET_FOR_SENT = new KlageAvvistÅrsak("KLAGET_FOR_SENT"); //$NON-NLS-1$
    public static final KlageAvvistÅrsak KLAGE_UGYLDIG = new KlageAvvistÅrsak("KLAGE_UGYLDIG"); //$NON-NLS-1$
    public static final KlageAvvistÅrsak IKKE_PAKLAGD_VEDTAK = new KlageAvvistÅrsak("IKKE_PAKLAGD_VEDTAK"); //$NON-NLS-1$
    public static final KlageAvvistÅrsak KLAGER_IKKE_PART = new KlageAvvistÅrsak("KLAGER_IKKE_PART"); //$NON-NLS-1$
    public static final KlageAvvistÅrsak IKKE_KONKRET = new KlageAvvistÅrsak("IKKE_KONKRET"); //$NON-NLS-1$
    public static final KlageAvvistÅrsak IKKE_SIGNERT = new KlageAvvistÅrsak("IKKE_SIGNERT"); //$NON-NLS-1$


    public static final KlageAvvistÅrsak UDEFINERT = new KlageAvvistÅrsak("-"); //$NON-NLS-1$

    KlageAvvistÅrsak() {
        // for hibernate
    }

    private KlageAvvistÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
