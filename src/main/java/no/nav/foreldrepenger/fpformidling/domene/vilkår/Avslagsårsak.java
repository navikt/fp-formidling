package no.nav.foreldrepenger.fpformidling.domene.vilkår;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.domene.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum Avslagsårsak implements Kodeverdi, ÅrsakMedLovReferanse {

    SØKT_FOR_TIDLIG("1001", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_1\", \"lovreferanse\": \"14-5\"}]}]}"),
    SØKER_ER_MEDMOR("1002", null),
    SØKER_ER_FAR("1003", null),
    BARN_OVER_15_ÅR("1004", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_16_1\", \"lovreferanse\": \"14-5\"}]}]}"),
    EKTEFELLES_SAMBOERS_BARN("1005", null),
    MANN_ADOPTERER_IKKE_ALENE("1006", null),
    SØKT_FOR_SENT("1007", null),
    SØKER_ER_IKKE_BARNETS_FAR_O("1008", null),
    MOR_IKKE_DØD("1009", null),
    MOR_IKKE_DØD_VED_FØDSEL_OMSORG("1010", null),
    ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR("1011",
        "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK1\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK4\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"§ 14-17 3. ledd\"}]}, {\"FP\": [{\"kategori\": \"FP_VK1\", \"lovreferanse\": \"§ 14-5 1. ledd\"}, {\"kategori\": \"FP_VK11\", \"lovreferanse\": \"§ 14-5 1. ledd\"}, {\"kategori\": \"FP_VK16\", \"lovreferanse\": \"§ 14-5 2. ledd\"}]}]}"),
    FAR_HAR_IKKE_OMSORG_FOR_BARNET("1012", null),
    BARN_IKKE_UNDER_15_ÅR("1013", Constants.FP_VK_8_LOVREFERANSE_14_5),
    SØKER_HAR_IKKE_FORELDREANSVAR("1014", Constants.FP_VK_8_LOVREFERANSE_14_5),
    SØKER_HAR_HATT_VANLIG_SAMVÆR_MED_BARNET("1015", Constants.FP_VK_8_LOVREFERANSE_14_5),
    SØKER_ER_IKKE_BARNETS_FAR_F("1016", null),
    OMSORGSOVERTAKELSE_ETTER_56_UKER("1017", null),
    IKKE_FORELDREANSVAR_ALENE_ETTER_BARNELOVA("1018", null),
    MANGLENDE_DOKUMENTASJON("1019", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_34\", \"lovreferanse\": \"21-3\"}]}]}"),
    SØKER_ER_IKKE_MEDLEM("1020", Constants.FP_VK_2_LOVREFERANSE_14_2),
    SØKER_ER_UTVANDRET("1021", Constants.FP_VK_2_LOVREFERANSE_14_2),
    SØKER_HAR_IKKE_LOVLIG_OPPHOLD("1023", Constants.FP_VK_2_LOVREFERANSE_14_2),
    SØKER_HAR_IKKE_OPPHOLDSRETT("1024", Constants.FP_VK_2_LOVREFERANSE_14_2),
    SØKER_ER_IKKE_BOSATT("1025", Constants.FP_VK_2_LOVREFERANSE_14_2),
    SØKER_INNFLYTTET_FOR_SENT("1052", "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK_2_F\", \"lovreferanse\": \"14-17 5. ledd\"}]}]}"),
    FØDSELSDATO_IKKE_OPPGITT_ELLER_REGISTRERT("1026",
        "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_1\", \"lovreferanse\": \"14-5\"}]}]}"),
    INGEN_BARN_DOKUMENTERT_PÅ_FAR_MEDMOR("1027", Constants.FP_VK_11_LOVREFERANSE_14_5),
    MOR_FYLLER_IKKE_VILKÅRET_FOR_SYKDOM("1028", Constants.FP_VK_11_LOVREFERANSE_14_5),
    BRUKER_ER_IKKE_REGISTRERT_SOM_FAR_MEDMOR_TIL_BARNET("1029", Constants.FP_VK_11_LOVREFERANSE_14_5),
    ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR("1031", "{\"fagsakYtelseType\": {\"FP\": {\"lovreferanse\": \"14-5\"}}}"),
    FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR("1032",
        "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK1\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK4\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"§ 14-17 3. ledd\"}]}, {\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}"),
    ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR("1033",
        "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK4\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK33\", \"lovreferanse\": \"14-17\"}]}, {\"FP\": {\"lovreferanse\": \"14-5\"}}]}"),
    FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR("1034",
        "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK4\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK33\", \"lovreferanse\": \"14-17\"}]}, {\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}"),
    IKKE_TILSTREKKELIG_OPPTJENING("1035", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_23\", \"lovreferanse\": \"14-6\"}]}]}"),
    FOR_LAVT_BEREGNINGSGRUNNLAG("1041", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_41\", \"lovreferanse\": \"14-7\"}]}]}"),
    STEBARNSADOPSJON_IKKE_FLERE_DAGER_IGJEN("1051", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_16\", \"lovreferanse\": \"14-5\"}]}]}"),
    SØKER_IKKE_GRAVID_KVINNE("1060", Constants.SVP_VK_1_LOVREFERANSE_14_4_1_LEDD),
    SØKER_ER_IKKE_I_ARBEID("1061", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 3. ledd\"}]}]}"),
    SØKER_SKULLE_IKKE_SØKT_SVP("1062", Constants.SVP_VK_1_LOVREFERANSE_14_4_1_LEDD),
    ARBEIDSTAKER_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER("1063", Constants.SVP_VK_1_LOVREFERANSE_14_4_1_LEDD),
    ARBEIDSTAKER_KAN_OMPLASSERES("1064", Constants.SVP_VK_1_LOVREFERANSE_14_4_1_LEDD),
    SN_FL_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER("1065",
        "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 2. ledd\"}]}]}"),
    SN_FL_HAR_MULIGHET_TIL_Å_TILRETTELEGGE_SITT_VIRKE("1066",
        "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 2. ledd\"}]}]}"),
    INGEN_BEREGNINGSREGLER_TILGJENGELIG_I_LØSNINGEN("1099", null),
    UDEFINERT("-", null),

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

    @JsonIgnore
    private String lovReferanse;

    @JsonValue
    private String kode;

    Avslagsårsak(String kode, String lovReferanse) {
        this.kode = kode;
        this.lovReferanse = lovReferanse;
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getLovHjemmelData() {
        return lovReferanse;
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

    private static class Constants {
        private static final String FP_VK_8_LOVREFERANSE_14_5 = "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}";
        private static final String FP_VK_2_LOVREFERANSE_14_2 = "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_2\", \"lovreferanse\": \"14-2\"}]}]}";
        private static final String FP_VK_11_LOVREFERANSE_14_5 = "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_11\", \"lovreferanse\": \"14-5\"}]}]}";
        private static final String SVP_VK_1_LOVREFERANSE_14_4_1_LEDD = "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 1. ledd\"}]}]}";
    }
}
