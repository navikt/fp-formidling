package no.nav.foreldrepenger.melding.vilkår;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "Avslagsårsak")
@DiscriminatorValue(Avslagsårsak.DISCRIMINATOR)
public class Avslagsårsak extends Kodeliste implements ÅrsakMedLovReferanse {

    public static final String DISCRIMINATOR = "AVSLAGSARSAK"; //$NON-NLS-1$

    public static final Avslagsårsak SØKT_FOR_TIDLIG = new Avslagsårsak("1001"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_ER_MEDMOR = new Avslagsårsak("1002"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_ER_FAR = new Avslagsårsak("1003"); //$NON-NLS-1$
    public static final Avslagsårsak BARN_OVER_15_ÅR = new Avslagsårsak("1004"); //$NON-NLS-1$
    public static final Avslagsårsak EKTEFELLES_SAMBOERS_BARN = new Avslagsårsak("1005"); //$NON-NLS-1$
    public static final Avslagsårsak MANN_ADOPTERER_IKKE_ALENE = new Avslagsårsak("1006"); //$NON-NLS-1$
    public static final Avslagsårsak SØKT_FOR_SENT = new Avslagsårsak("1007"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_ER_IKKE_BARNETS_FAR_O = new Avslagsårsak("1008"); //$NON-NLS-1$
    public static final Avslagsårsak MOR_IKKE_DØD = new Avslagsårsak("1009"); //$NON-NLS-1$
    public static final Avslagsårsak MOR_IKKE_DØD_VED_FØDSEL_OMSORG = new Avslagsårsak("1010"); //$NON-NLS-1$
    public static final Avslagsårsak ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR = new Avslagsårsak("1011"); //$NON-NLS-1$
    public static final Avslagsårsak FAR_HAR_IKKE_OMSORG_FOR_BARNET = new Avslagsårsak("1012"); //$NON-NLS-1$
    public static final Avslagsårsak BARN_IKKE_UNDER_15_ÅR = new Avslagsårsak("1013"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_HAR_IKKE_FORELDREANSVAR = new Avslagsårsak("1014"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_HAR_HATT_VANLIG_SAMVÆR_MED_BARNET = new Avslagsårsak("1015"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_ER_IKKE_BARNETS_FAR_F = new Avslagsårsak("1016"); //$NON-NLS-1$
    public static final Avslagsårsak OMSORGSOVERTAKELSE_ETTER_56_UKER = new Avslagsårsak("1017"); //$NON-NLS-1$
    public static final Avslagsårsak IKKE_FORELDREANSVAR_ALENE_ETTER_BARNELOVA = new Avslagsårsak("1018"); //$NON-NLS-1$
    public static final Avslagsårsak MANGLENDE_DOKUMENTASJON = new Avslagsårsak("1019"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_ER_IKKE_MEDLEM = new Avslagsårsak("1020"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_ER_UTVANDRET = new Avslagsårsak("1021"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_HAR_IKKE_LOVLIG_OPPHOLD = new Avslagsårsak("1023"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_HAR_IKKE_OPPHOLDSRETT = new Avslagsårsak("1024"); //$NON-NLS-1$
    public static final Avslagsårsak SØKER_ER_IKKE_BOSATT = new Avslagsårsak("1025"); //$NON-NLS-1$
    public static final Avslagsårsak FØDSELSDATO_IKKE_OPPGITT_ELLER_REGISTRERT = new Avslagsårsak("1026"); //$NON-NLS-1$
    public static final Avslagsårsak INGEN_BARN_DOKUMENTERT_PÅ_FAR_MEDMOR = new Avslagsårsak("1027"); //$NON-NLS-1$
    public static final Avslagsårsak MOR_FYLLER_IKKE_VILKÅRET_FOR_SYKDOM = new Avslagsårsak("1028"); //$NON-NLS-1$
    public static final Avslagsårsak BRUKER_ER_IKKE_REGISTRERT_SOM_FAR_MEDMOR_TIL_BARNET = new Avslagsårsak("1029"); //$NON-NLS-1$
    public static final Avslagsårsak ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR = new Avslagsårsak("1031"); //$NON-NLS-1$
    public static final Avslagsårsak FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR = new Avslagsårsak("1032"); //$NON-NLS-1$
    public static final Avslagsårsak ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR = new Avslagsårsak("1033"); //$NON-NLS-1$
    public static final Avslagsårsak FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR = new Avslagsårsak("1034"); //$NON-NLS-1$
    public static final Avslagsårsak IKKE_TILSTREKKELIG_OPPTJENING = new Avslagsårsak("1035"); //$NON-NLS-1$
    public static final Avslagsårsak FOR_LAVT_BEREGNINGSGRUNNLAG = new Avslagsårsak("1041"); //$NON-NLS-1$
    public static final Avslagsårsak STEBARNSADOPSJON_IKKE_FLERE_DAGER_IGJEN = new Avslagsårsak("1051"); //$NON-NLS-1$

    public static final Avslagsårsak INGEN_BEREGNINGSREGLER_TILGJENGELIG_I_LØSNINGEN = new Avslagsårsak("1099"); //$NON-NLS-1$

    public static final Avslagsårsak UDEFINERT = new Avslagsårsak("-"); //$NON-NLS-1$

    private static final Set<Avslagsårsak> ALLEREDE_UTBETALT_ENGANGSSTØNAD_ÅRSAKER = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList(ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR, ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR,
                    ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR)));


    @Transient
    private String lovReferanse;

    Avslagsårsak() {
        // Hibernate trenger den
    }


    @Override
    public String getLovHjemmelData() {
        return getEkstraData();
    }

    private Avslagsårsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public boolean erAlleredeUtbetaltEngangsstønad() {
        return ALLEREDE_UTBETALT_ENGANGSSTØNAD_ÅRSAKER.contains(this);
    }
}
