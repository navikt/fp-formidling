package no.nav.foreldrepenger.melding.behandling.inntektarbeidytelse;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Arbeidskategori")
@DiscriminatorValue(Arbeidskategori.DISCRIMINATOR)
public class Arbeidskategori extends Kodeliste {

    public static final String DISCRIMINATOR = "ARBEIDSKATEGORI";

    public static final Arbeidskategori FISKER = new Arbeidskategori("FISKER"); //$NON-NLS-1$
    public static final Arbeidskategori ARBEIDSTAKER = new Arbeidskategori("ARBEIDSTAKER"); //$NON-NLS-1$
    public static final Arbeidskategori SELVSTENDIG_NÆRINGSDRIVENDE = new Arbeidskategori("SELVSTENDIG_NÆRINGSDRIVENDE"); //$NON-NLS-1$
    public static final Arbeidskategori KOMBINASJON_ARBEIDSTAKER_OG_SELVSTENDIG_NÆRINGSDRIVENDE = new Arbeidskategori("KOMBINASJON_ARBEIDSTAKER_OG_SELVSTENDIG_NÆRINGSDRIVENDE"); //$NON-NLS-1$
    public static final Arbeidskategori SJØMANN = new Arbeidskategori("SJØMANN"); //$NON-NLS-1$
    public static final Arbeidskategori JORDBRUKER = new Arbeidskategori("JORDBRUKER"); //$NON-NLS-1$
    public static final Arbeidskategori DAGPENGER = new Arbeidskategori("DAGPENGER"); //$NON-NLS-1$
    public static final Arbeidskategori INAKTIV = new Arbeidskategori("INAKTIV"); //$NON-NLS-1$
    public static final Arbeidskategori KOMBINASJON_ARBEIDSTAKER_OG_JORDBRUKER = new Arbeidskategori("KOMBINASJON_ARBEIDSTAKER_OG_JORDBRUKER"); //$NON-NLS-1$
    public static final Arbeidskategori KOMBINASJON_ARBEIDSTAKER_OG_FISKER = new Arbeidskategori("KOMBINASJON_ARBEIDSTAKER_OG_FISKER"); //$NON-NLS-1$
    public static final Arbeidskategori FRILANSER = new Arbeidskategori("FRILANSER"); //$NON-NLS-1$
    public static final Arbeidskategori KOMBINASJON_ARBEIDSTAKER_OG_FRILANSER = new Arbeidskategori("KOMBINASJON_ARBEIDSTAKER_OG_FRILANSER"); //$NON-NLS-1$
    public static final Arbeidskategori KOMBINASJON_ARBEIDSTAKER_OG_DAGPENGER = new Arbeidskategori("KOMBINASJON_ARBEIDSTAKER_OG_DAGPENGER"); //$NON-NLS-1$
    public static final Arbeidskategori DAGMAMMA = new Arbeidskategori("DAGMAMMA"); //$NON-NLS-1$
    public static final Arbeidskategori UGYLDIG = new Arbeidskategori("UGYLDIG"); //$NON-NLS-1$

    public static final Arbeidskategori UDEFINERT = new Arbeidskategori("-"); //$NON-NLS-1$


    Arbeidskategori() {
        // Hibernate trenger en
    }

    private Arbeidskategori(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
