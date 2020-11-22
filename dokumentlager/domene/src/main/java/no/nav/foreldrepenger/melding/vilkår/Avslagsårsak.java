package no.nav.foreldrepenger.melding.vilkår;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;

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
    ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR("1011", "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK1\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK4\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"§ 14-17 3. ledd\"}]}, {\"FP\": [{\"kategori\": \"FP_VK1\", \"lovreferanse\": \"§ 14-5 1. ledd\"}, {\"kategori\": \"FP_VK11\", \"lovreferanse\": \"§ 14-5 1. ledd\"}, {\"kategori\": \"FP_VK16\", \"lovreferanse\": \"§ 14-5 2. ledd\"}]}]}"),
    FAR_HAR_IKKE_OMSORG_FOR_BARNET("1012", null),
    BARN_IKKE_UNDER_15_ÅR("1013", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}"),
    SØKER_HAR_IKKE_FORELDREANSVAR("1014", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}"),
    SØKER_HAR_HATT_VANLIG_SAMVÆR_MED_BARNET("1015", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}"),
    SØKER_ER_IKKE_BARNETS_FAR_F("1016", null),
    OMSORGSOVERTAKELSE_ETTER_56_UKER("1017", null),
    IKKE_FORELDREANSVAR_ALENE_ETTER_BARNELOVA("1018", null),
    MANGLENDE_DOKUMENTASJON("1019", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_34\", \"lovreferanse\": \"21-3\"}]}]}"),
    SØKER_ER_IKKE_MEDLEM("1020", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_2\", \"lovreferanse\": \"14-2\"}]}]}"),
    SØKER_ER_UTVANDRET("1021", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_2\", \"lovreferanse\": \"14-2\"}]}]}"),
    SØKER_HAR_IKKE_LOVLIG_OPPHOLD("1023", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_2\", \"lovreferanse\": \"14-2\"}]}]}"),
    SØKER_HAR_IKKE_OPPHOLDSRETT("1024", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_2\", \"lovreferanse\": \"14-2\"}]}]}"),
    SØKER_ER_IKKE_BOSATT("1025", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_2\", \"lovreferanse\": \"14-2\"}]}]}"),
    FØDSELSDATO_IKKE_OPPGITT_ELLER_REGISTRERT("1026", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_1\", \"lovreferanse\": \"14-5\"}]}]}"),
    INGEN_BARN_DOKUMENTERT_PÅ_FAR_MEDMOR("1027", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_11\", \"lovreferanse\": \"14-5\"}]}]}"),
    MOR_FYLLER_IKKE_VILKÅRET_FOR_SYKDOM("1028", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_11\", \"lovreferanse\": \"14-5\"}]}]}"),
    BRUKER_ER_IKKE_REGISTRERT_SOM_FAR_MEDMOR_TIL_BARNET("1029", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_11\", \"lovreferanse\": \"14-5\"}]}]}"),
    ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR("1031", "{\"fagsakYtelseType\": {\"FP\": {\"lovreferanse\": \"14-5\"}}}"),
    FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR("1032", "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK1\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK4\", \"lovreferanse\": \"§ 14-17 1. ledd\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"§ 14-17 3. ledd\"}]}, {\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}"),
    ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR("1033", "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK4\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK33\", \"lovreferanse\": \"14-17\"}]}, {\"FP\": {\"lovreferanse\": \"14-5\"}}]}"),
    FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR("1034", "{\"fagsakYtelseType\": [{\"ES\": [{\"kategori\": \"FP_VK4\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK5\", \"lovreferanse\": \"14-17\"}, {\"kategori\": \"FP_VK33\", \"lovreferanse\": \"14-17\"}]}, {\"FP\": [{\"kategori\": \"FP_VK_8\", \"lovreferanse\": \"14-5\"}]}]}"),
    IKKE_TILSTREKKELIG_OPPTJENING("1035", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_23\", \"lovreferanse\": \"14-6\"}]}]}"),
    FOR_LAVT_BEREGNINGSGRUNNLAG("1041", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_41\", \"lovreferanse\": \"14-7\"}]}]}"),
    STEBARNSADOPSJON_IKKE_FLERE_DAGER_IGJEN("1051", "{\"fagsakYtelseType\": [{\"FP\": [{\"kategori\": \"FP_VK_16\", \"lovreferanse\": \"14-5\"}]}]}"),
    SØKER_IKKE_GRAVID_KVINNE("1060", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 1. ledd\"}]}]}"),
    SØKER_ER_IKKE_I_ARBEID("1061", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 3. ledd\"}]}]}"),
    SØKER_SKULLE_IKKE_SØKT_SVP("1062", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 1. ledd\"}]}]}"),
    ARBEIDSTAKER_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER("1063", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 1. ledd\"}]}]}"),
    ARBEIDSTAKER_KAN_OMPLASSERES("1064", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 1. ledd\"}]}]}"),
    SN_FL_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER("1065", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 2. ledd\"}]}]}"),
    SN_FL_HAR_MULIGHET_TIL_Å_TILRETTELEGGE_SITT_VIRKE("1066", "{\"fagsakYtelseType\": [{\"SVP\": [{\"kategori\": \"SVP_VK_1\", \"lovreferanse\": \"14-4 2. ledd\"}]}]}"),
    INGEN_BEREGNINGSREGLER_TILGJENGELIG_I_LØSNINGEN("1099", null),
    UDEFINERT("-", null),

    ;

    private static final Map<String, Avslagsårsak> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    public static final String KODEVERK = "AVSLAGSARSAK";

    public static final Set<Avslagsårsak> ALLEREDE_UTBETALT_ENGANGSSTØNAD = Set.of(ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR,
            ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR,
            ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR);

    public static final Set<Avslagsårsak> ALLEREDE_UTBETALT_FORELDREPENGER = Set.of(FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR,
            FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR);

    public static final Set<Avslagsårsak> IKKE_ALENEOMSORG = Set.of(SØKER_ER_MEDMOR, SØKER_ER_FAR);

    public static final Set<Avslagsårsak> BARN_IKKE_RIKTIG_ALDER = Set.of(BARN_OVER_15_ÅR, BARN_IKKE_UNDER_15_ÅR);

    public static final Set<Avslagsårsak> IKKE_BARNETS_FAR = Set.of(SØKER_ER_IKKE_BARNETS_FAR_F, SØKER_ER_IKKE_BARNETS_FAR_O);

    // TODO endre fra raw json
    @JsonIgnore
    private String lovReferanse;

    private String kode;

    Avslagsårsak(String kode, String lovReferanse) {
        this.kode = kode;
        this.lovReferanse = lovReferanse;
    }

    @JsonCreator
    public static Avslagsårsak fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Avslagsårsak: " + kode);
        }
        return ad;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
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
}
