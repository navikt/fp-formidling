package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "BeregningsgrunnlagHjemmel")
@DiscriminatorValue(Hjemmel.DISCRIMINATOR)
public class Hjemmel extends Kodeliste {

    public static final String DISCRIMINATOR = "BG_HJEMMEL";

    public static final Hjemmel F_14_7 = new Hjemmel("F_14_7"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_30 = new Hjemmel("F_14_7_8_30"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_28_8_30 = new Hjemmel("F_14_7_8_28_8_30"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_35 = new Hjemmel("F_14_7_8_35"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_38 = new Hjemmel("F_14_7_8_38"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_40 = new Hjemmel("F_14_7_8_40"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_41 = new Hjemmel("F_14_7_8_41"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_42 = new Hjemmel("F_14_7_8_42"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_43 = new Hjemmel("F_14_7_8_43"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_47 = new Hjemmel("F_14_7_8_47"); //$NON-NLS-1$
    public static final Hjemmel F_14_7_8_49 = new Hjemmel("F_14_7_8_49"); //$NON-NLS-1$
    public static final Hjemmel UDEFINERT = new Hjemmel("-"); //$NON-NLS-1$

    public Hjemmel() {
        // for hibernate
    }

    private Hjemmel(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
