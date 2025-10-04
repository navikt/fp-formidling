package no.nav.foreldrepenger.fpformidling.domene.vilkår;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum Avslagsårsak implements Kodeverdi {

    SØKT_FOR_TIDLIG("1001"),
    SØKER_ER_MEDMOR("1002"),
    SØKER_ER_FAR("1003"),
    BARN_OVER_15_ÅR("1004"),
    EKTEFELLES_SAMBOERS_BARN("1005"),
    MANN_ADOPTERER_IKKE_ALENE("1006"),
    SØKT_FOR_SENT("1007"),
    SØKER_ER_IKKE_BARNETS_FAR_O("1008"),
    MOR_IKKE_DØD("1009"),
    MOR_IKKE_DØD_VED_FØDSEL_OMSORG("1010"),
    ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR("1011"),
    FAR_HAR_IKKE_OMSORG_FOR_BARNET("1012"),
    BARN_IKKE_UNDER_15_ÅR("1013"),
    SØKER_HAR_IKKE_FORELDREANSVAR("1014"),
    SØKER_HAR_HATT_VANLIG_SAMVÆR_MED_BARNET("1015"),
    SØKER_ER_IKKE_BARNETS_FAR_F("1016"),
    OMSORGSOVERTAKELSE_ETTER_56_UKER("1017"),
    IKKE_FORELDREANSVAR_ALENE_ETTER_BARNELOVA("1018"),
    MANGLENDE_DOKUMENTASJON("1019"),
    SØKER_ER_IKKE_MEDLEM("1020"),
    SØKER_ER_UTVANDRET("1021"),
    SØKER_HAR_IKKE_LOVLIG_OPPHOLD("1023"),
    SØKER_HAR_IKKE_OPPHOLDSRETT("1024"),
    SØKER_ER_IKKE_BOSATT("1025"),
    SØKER_INNFLYTTET_FOR_SENT("1052"),
    FØDSELSDATO_IKKE_OPPGITT_ELLER_REGISTRERT("1026"),
    INGEN_BARN_DOKUMENTERT_PÅ_FAR_MEDMOR("1027"),
    MOR_FYLLER_IKKE_VILKÅRET_FOR_SYKDOM("1028"),
    BRUKER_ER_IKKE_REGISTRERT_SOM_FAR_MEDMOR_TIL_BARNET("1029"),
    ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR("1031"),
    FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR("1032"),
    ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR("1033"),
    FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR("1034"),
    IKKE_TILSTREKKELIG_OPPTJENING("1035"),
    FOR_LAVT_BEREGNINGSGRUNNLAG("1041"),
    STEBARNSADOPSJON_IKKE_FLERE_DAGER_IGJEN("1051"),
    SØKER_IKKE_GRAVID_KVINNE("1060"),
    SØKER_ER_IKKE_I_ARBEID("1061"),
    SØKER_SKULLE_IKKE_SØKT_SVP("1062"),
    ARBEIDSTAKER_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER("1063"),
    ARBEIDSTAKER_KAN_OMPLASSERES("1064"),
    SN_FL_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER("1065"),
    SN_FL_HAR_MULIGHET_TIL_Å_TILRETTELEGGE_SITT_VIRKE("1066"),
    INGEN_BEREGNINGSREGLER_TILGJENGELIG_I_LØSNINGEN("1099"),
    UDEFINERT("-"),

    ;

    public static final Set<Avslagsårsak> ALLEREDE_UTBETALT_ENGANGSSTØNAD = Collections.unmodifiableSet(
        new LinkedHashSet<>(Arrays.asList(ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR, ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR)));

    public static final Set<Avslagsårsak> ALLEREDE_UTBETALT_FORELDREPENGER = Collections.unmodifiableSet(
        new LinkedHashSet<>(Arrays.asList(FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR, FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR)));

    public static final Set<Avslagsårsak> IKKE_ALENEOMSORG = Collections.unmodifiableSet(
        new LinkedHashSet<>(Arrays.asList(SØKER_ER_MEDMOR, SØKER_ER_FAR, MOR_IKKE_DØD, MANN_ADOPTERER_IKKE_ALENE)));

    public static final Set<Avslagsårsak> BARN_IKKE_RIKTIG_ALDER = Collections.unmodifiableSet(
        new LinkedHashSet<>(Arrays.asList(BARN_OVER_15_ÅR, BARN_IKKE_UNDER_15_ÅR)));

    public static final Set<Avslagsårsak> IKKE_BARNETS_FAR = Collections.unmodifiableSet(
        new LinkedHashSet<>(Arrays.asList(SØKER_ER_IKKE_BARNETS_FAR_F, SØKER_ER_IKKE_BARNETS_FAR_O)));

    @JsonValue
    private final String kode;

    Avslagsårsak(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

    public static boolean erAlleredeUtbetaltEngangsstønad(Avslagsårsak avslagsårsak) {
        return ALLEREDE_UTBETALT_ENGANGSSTØNAD.contains(avslagsårsak);
    }

    public static boolean erAlleredeUtbetaltForeldrepenger(Avslagsårsak avslagsårsak) {
        return ALLEREDE_UTBETALT_FORELDREPENGER.contains(avslagsårsak);
    }

    public static boolean farHarIkkeAleneomsorg(Avslagsårsak avslagsårsak) {
        return IKKE_ALENEOMSORG.contains(avslagsårsak);
    }

    public static boolean barnIkkeRiktigAlder(Avslagsårsak avslagsårsak) {
        return BARN_IKKE_RIKTIG_ALDER.contains(avslagsårsak);
    }

    public static boolean ikkeBarnetsFar(Avslagsårsak avslagsårsak) {
        return IKKE_BARNETS_FAR.contains(avslagsårsak);
    }
}
