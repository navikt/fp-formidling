package no.nav.foreldrepenger.melding.behandling.beregningsgrunnlag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Inntektskategori")
@DiscriminatorValue(Inntektskategori.DISCRIMINATOR)
public class Inntektskategori extends Kodeliste {

    public static final String DISCRIMINATOR = "INNTEKTSKATEGORI";

    public static final Inntektskategori ARBEIDSTAKER = new Inntektskategori("ARBEIDSTAKER"); //$NON-NLS-1$
    public static final Inntektskategori FRILANSER = new Inntektskategori("FRILANSER"); //$NON-NLS-1$
    public static final Inntektskategori SELVSTENDIG_NÆRINGSDRIVENDE = new Inntektskategori("SELVSTENDIG_NÆRINGSDRIVENDE"); //$NON-NLS-1$
    public static final Inntektskategori DAGPENGER = new Inntektskategori("DAGPENGER"); //$NON-NLS-1$
    public static final Inntektskategori ARBEIDSAVKLARINGSPENGER = new Inntektskategori("ARBEIDSAVKLARINGSPENGER"); //$NON-NLS-1$
    public static final Inntektskategori SJØMANN = new Inntektskategori("SJØMANN"); //$NON-NLS-1$
    public static final Inntektskategori DAGMAMMA = new Inntektskategori("DAGMAMMA"); //$NON-NLS-1$
    public static final Inntektskategori JORDBRUKER = new Inntektskategori("JORDBRUKER"); //$NON-NLS-1$
    public static final Inntektskategori FISKER = new Inntektskategori("FISKER"); //$NON-NLS-1$
    public static final Inntektskategori ARBEIDSTAKER_UTEN_FERIEPENGER = new Inntektskategori("ARBEIDSTAKER_UTEN_FERIEPENGER"); //$NON-NLS-1$

    public static final Inntektskategori UDEFINERT = new Inntektskategori("-"); //$NON-NLS-1$

    Inntektskategori() {
        // Hibernate trenger en
    }

    private Inntektskategori(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
