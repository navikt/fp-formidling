package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "Begrunnelse")
@DiscriminatorValue(Begrunnelse.DISCRIMINATOR)
public class Begrunnelse extends Kodeliste {

    public static final String DISCRIMINATOR = "BEGRUNNELSE";

    public static final Begrunnelse LOVLIG_FRAVAER = new Begrunnelse("LOVF");
    public static final Begrunnelse FRAVAER_UTEN_GYLDIG_GRUNN = new Begrunnelse("FUAG");
    public static final Begrunnelse ARBEID_OPPHOERT = new Begrunnelse("ARBO");
    public static final Begrunnelse BESKJED_GITT_FOR_SENT = new Begrunnelse("BGFS");
    public static final Begrunnelse MANGLER_OPPTJENING = new Begrunnelse("MOPT");
    public static final Begrunnelse IKKE_LOENN = new Begrunnelse("IKLO");
    public static final Begrunnelse BETVILER_ARBEIDSUFOERHET = new Begrunnelse("BAUE");

    public static final Begrunnelse UDEFINERT = new Begrunnelse("-"); //$NON-NLS-1$


    public Begrunnelse() {
        // For Hibernate
    }

    public Begrunnelse(String kode) {
        super(kode, DISCRIMINATOR);
    }
}
