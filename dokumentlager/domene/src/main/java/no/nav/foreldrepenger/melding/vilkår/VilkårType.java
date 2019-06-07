package no.nav.foreldrepenger.melding.vilkår;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "VilkarType")
@DiscriminatorValue(VilkårType.DISCRIMINATOR)
public class VilkårType extends Kodeliste {

    public static final String DISCRIMINATOR = "VILKAR_TYPE"; //$NON-NLS-1$

    /**
     * statisk koder, kun for konfigurasjon. Bruk VilkårType konstanter for API'er og skriving.
     */
    public static final String FP_VK_1 = "FP_VK_1"; //$NON-NLS-1$
    public static final String FP_VK_2 = "FP_VK_2"; //$NON-NLS-1$
    public static final String FP_VK_3 = "FP_VK_3"; //$NON-NLS-1$
    public static final String FP_VK_4 = "FP_VK_4"; //$NON-NLS-1$
    public static final String FP_VK_5 = "FP_VK_5"; //$NON-NLS-1$

    public static final String FP_VK_8 = "FP_VK_8"; //$NON-NLS-1$
    public static final String FP_VK_11 = "FP_VK_11"; //$NON-NLS-1$
    public static final String FP_VK_16 = "FP_VK_16"; //$NON-NLS-1$
    public static final String FP_VK_21 = "FP_VK_21"; //$NON-NLS-1$
    public static final String FP_VK_23 = "FP_VK_23"; //$NON-NLS-1$
    public static final String FP_VK_33 = "FP_VK_33"; //$NON-NLS-1$
    public static final String FP_VK_34 = "FP_VK_34"; //$NON-NLS-1$

    public static final String FP_VK_41 = "FP_VK_41"; //$NON-NLS-1$

    public static final String FP_VK_2_L = "FP_VK_2_L"; //$NON-NLS-1$

    public static final VilkårType FØDSELSVILKÅRET_MOR = new VilkårType(FP_VK_1);
    public static final VilkårType MEDLEMSKAPSVILKÅRET = new VilkårType(FP_VK_2);
    public static final VilkårType MEDLEMSKAPSVILKÅRET_LØPENDE = new VilkårType(FP_VK_2_L);
    public static final VilkårType SØKNADSFRISTVILKÅRET = new VilkårType(FP_VK_3);
    public static final VilkårType ADOPSJONSVILKÅRET_ENGANGSSTØNAD = new VilkårType(FP_VK_4);
    public static final VilkårType ADOPSJONSVILKARET_FORELDREPENGER = new VilkårType(FP_VK_16);
    public static final VilkårType OMSORGSVILKÅRET = new VilkårType(FP_VK_5);
    public static final VilkårType FORELDREANSVARSVILKÅRET_2_LEDD = new VilkårType(FP_VK_8);
    public static final VilkårType FØDSELSVILKÅRET_FAR_MEDMOR = new VilkårType(FP_VK_11);
    public static final VilkårType FORELDREANSVARSVILKÅRET_4_LEDD = new VilkårType(FP_VK_33);
    public static final VilkårType SØKERSOPPLYSNINGSPLIKT = new VilkårType(FP_VK_34);
    public static final VilkårType OPPTJENINGSPERIODEVILKÅR = new VilkårType(FP_VK_21);
    public static final VilkårType OPPTJENINGSVILKÅRET = new VilkårType(FP_VK_23);
    public static final VilkårType BEREGNINGSGRUNNLAGVILKÅR = new VilkårType(FP_VK_41);

    /**
     * Brukes i stedet for null der det er optional.
     */
    public static final VilkårType UDEFINERT = new VilkårType("-"); //$NON-NLS-1$

    @Transient
    private String lovReferanse;

    VilkårType() {
        // Hibernate trenger den
    }

    public VilkårType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public String getLovReferanse(FagsakYtelseType fagsakYtelseType) {
        if (lovReferanse == null) {
            if (fagsakYtelseType.gjelderEngangsstønad()) {
                lovReferanse = getJsonField("fagsakYtelseType", "ES", "lovreferanse"); //$NON-NLS-1$
            } else if (fagsakYtelseType.gjelderForeldrepenger()) {
                lovReferanse = getJsonField("fagsakYtelseType", "FP", "lovreferanse"); //$NON-NLS-1$
            }
        }
        return lovReferanse;
    }

    @Override
    public String toString() {
        return super.toString() + "<lovReferanse ES=" + getLovReferanse(FagsakYtelseType.ENGANGSTØNAD) + ">"
                + "<lovReferanse FP=" + getLovReferanse(FagsakYtelseType.FORELDREPENGER) + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
