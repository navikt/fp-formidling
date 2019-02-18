package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "VariantFormat")
@DiscriminatorValue(VariantFormat.DISCRIMINATOR)
public class VariantFormat extends Kodeliste {

    public static final String DISCRIMINATOR = "VARIANT_FORMAT";

    /**
     * Konstanter for å skrive ned kodeverdi. For å hente ut andre data konfigurert, må disse leses fra databasen (eks.
     * for å hente offisiell kode for et Nav kodeverk).
     */
    public static final VariantFormat PRODUKSJON = new VariantFormat("PROD"); //$NON-NLS-1$
    public static final VariantFormat ARKIV = new VariantFormat("ARKIV"); //$NON-NLS-1$
    public static final VariantFormat SKANNING_META = new VariantFormat("SKANM"); //$NON-NLS-1$
    public static final VariantFormat BREVBESTILLING = new VariantFormat("BREVB"); //$NON-NLS-1$
    public static final VariantFormat ORIGINAL = new VariantFormat("ORIG"); //$NON-NLS-1$
    public static final VariantFormat FULLVERSJON = new VariantFormat("FULL"); //$NON-NLS-1$
    public static final VariantFormat SLADDET = new VariantFormat("SLADD"); //$NON-NLS-1$
    public static final VariantFormat PRODUKSJON_DLF = new VariantFormat("PRDLF"); //$NON-NLS-1$

    /**
     * Alle kodeverk må ha en verdi, det kan ikke være null i databasen. Denne koden gjør samme nytten.
     */
    public static final VariantFormat UDEFINERT = new VariantFormat("-"); //$NON-NLS-1$

    public VariantFormat() {
        // Hibernate trenger den
    }

    private VariantFormat(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
