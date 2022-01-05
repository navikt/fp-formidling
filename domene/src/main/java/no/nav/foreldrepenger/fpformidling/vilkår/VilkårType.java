package no.nav.foreldrepenger.fpformidling.vilkår;

import java.util.HashSet;
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

import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@SuppressWarnings("java:S1192")
@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum VilkårType implements Kodeverdi {

    FØDSELSVILKÅRET_MOR(VilkårTypeKoder.FP_VK_1,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 14-17, 1. ledd", FagsakYtelseType.FORELDREPENGER, "§ 14-5, 1. ledd"),
        Avslagsårsak.SØKT_FOR_TIDLIG,
        Avslagsårsak.SØKER_ER_MEDMOR,
        Avslagsårsak.SØKER_ER_FAR,
        Avslagsårsak.FØDSELSDATO_IKKE_OPPGITT_ELLER_REGISTRERT,
        Avslagsårsak.ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR,
        Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR),
    FØDSELSVILKÅRET_FAR_MEDMOR(VilkårTypeKoder.FP_VK_11,
        Map.of(FagsakYtelseType.FORELDREPENGER, "§ 14-5, 1. ledd"),
        Avslagsårsak.INGEN_BARN_DOKUMENTERT_PÅ_FAR_MEDMOR,
        Avslagsårsak.MOR_FYLLER_IKKE_VILKÅRET_FOR_SYKDOM,
        Avslagsårsak.BRUKER_ER_IKKE_REGISTRERT_SOM_FAR_MEDMOR_TIL_BARNET),
    ADOPSJONSVILKARET_FORELDREPENGER(VilkårTypeKoder.FP_VK_16,
        Map.of(FagsakYtelseType.FORELDREPENGER, "§ 14-5, første ledd eller tredje ledd"),
        Avslagsårsak.BARN_OVER_15_ÅR,
        Avslagsårsak.STEBARNSADOPSJON_IKKE_FLERE_DAGER_IGJEN),
    MEDLEMSKAPSVILKÅRET(VilkårTypeKoder.FP_VK_2,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 14-2", FagsakYtelseType.FORELDREPENGER, "§ 14-2", FagsakYtelseType.SVANGERSKAPSPENGER, "§ 14-2"),
        Avslagsårsak.SØKER_ER_IKKE_MEDLEM,
        Avslagsårsak.SØKER_ER_UTVANDRET,
        Avslagsårsak.SØKER_HAR_IKKE_LOVLIG_OPPHOLD,
        Avslagsårsak.SØKER_HAR_IKKE_OPPHOLDSRETT,
        Avslagsårsak.SØKER_ER_IKKE_BOSATT),
    MEDLEMSKAPSVILKÅRET_LØPENDE(VilkårTypeKoder.FP_VK_2_L,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 14-2", FagsakYtelseType.FORELDREPENGER, "§ 14-2", FagsakYtelseType.SVANGERSKAPSPENGER, "§ 14-2"),
        Avslagsårsak.SØKER_ER_IKKE_MEDLEM,
        Avslagsårsak.SØKER_ER_UTVANDRET,
        Avslagsårsak.SØKER_HAR_IKKE_LOVLIG_OPPHOLD,
        Avslagsårsak.SØKER_HAR_IKKE_OPPHOLDSRETT,
        Avslagsårsak.SØKER_ER_IKKE_BOSATT),
    SØKNADSFRISTVILKÅRET(VilkårTypeKoder.FP_VK_3,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 22-13, 2. ledd"),
        Avslagsårsak.SØKT_FOR_SENT),
    ADOPSJONSVILKÅRET_ENGANGSSTØNAD(VilkårTypeKoder.FP_VK_4,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 14-17, 1. ledd"),
        Avslagsårsak.BARN_OVER_15_ÅR,
        Avslagsårsak.EKTEFELLES_SAMBOERS_BARN,
        Avslagsårsak.MANN_ADOPTERER_IKKE_ALENE,
        Avslagsårsak.ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR,
        Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR,
        Avslagsårsak.ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR,
        Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR),
    OMSORGSVILKÅRET(VilkårTypeKoder.FP_VK_5,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 14-17, 3. ledd"),
        Avslagsårsak.SØKER_ER_IKKE_BARNETS_FAR_O,
        Avslagsårsak.MOR_IKKE_DØD,
        Avslagsårsak.MOR_IKKE_DØD_VED_FØDSEL_OMSORG,
        Avslagsårsak.ENGANGSSTØNAD_ALLEREDE_UTBETALT_TIL_MOR,
        Avslagsårsak.FAR_HAR_IKKE_OMSORG_FOR_BARNET,
        Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR,
        Avslagsårsak.ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR,
        Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR),
    FORELDREANSVARSVILKÅRET_2_LEDD(VilkårTypeKoder.FP_VK_8,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 14-17, 2. ledd", FagsakYtelseType.FORELDREPENGER, "§ 14-5, 2. ledd"),
        Avslagsårsak.BARN_IKKE_UNDER_15_ÅR,
        Avslagsårsak.SØKER_HAR_IKKE_FORELDREANSVAR,
        Avslagsårsak.SØKER_HAR_HATT_VANLIG_SAMVÆR_MED_BARNET,
        Avslagsårsak.ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR,
        Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR),
    FORELDREANSVARSVILKÅRET_4_LEDD(VilkårTypeKoder.FP_VK_33,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§ 14-17, 4. ledd"),
        Avslagsårsak.SØKER_ER_IKKE_BARNETS_FAR_F,
        Avslagsårsak.OMSORGSOVERTAKELSE_ETTER_56_UKER,
        Avslagsårsak.IKKE_FORELDREANSVAR_ALENE_ETTER_BARNELOVA,
        Avslagsårsak.ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR,
        Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR),
    SØKERSOPPLYSNINGSPLIKT(VilkårTypeKoder.FP_VK_34,
        Map.of(FagsakYtelseType.ENGANGSTØNAD, "§§ 21-3", FagsakYtelseType.FORELDREPENGER, "§§ 21-3", FagsakYtelseType.SVANGERSKAPSPENGER, "§ 21-3"),
        Avslagsårsak.MANGLENDE_DOKUMENTASJON),
    OPPTJENINGSPERIODEVILKÅR(VilkårTypeKoder.FP_VK_21,
        Map.of(FagsakYtelseType.FORELDREPENGER, "§ 14-6 og 14-10", FagsakYtelseType.SVANGERSKAPSPENGER, "§ 14-4"),
        Avslagsårsak.IKKE_TILSTREKKELIG_OPPTJENING),
    OPPTJENINGSVILKÅRET(VilkårTypeKoder.FP_VK_23,
        Map.of(FagsakYtelseType.FORELDREPENGER, "§ 14-6", FagsakYtelseType.SVANGERSKAPSPENGER, "§ 14-4"),
        Avslagsårsak.IKKE_TILSTREKKELIG_OPPTJENING),
    BEREGNINGSGRUNNLAGVILKÅR(VilkårTypeKoder.FP_VK_41,
        Map.of(FagsakYtelseType.FORELDREPENGER, "§ 14-7", FagsakYtelseType.SVANGERSKAPSPENGER, "§ 14-4"),
        Avslagsårsak.FOR_LAVT_BEREGNINGSGRUNNLAG),
    SVANGERSKAPSPENGERVILKÅR(VilkårTypeKoder.SVP_VK_1,
        Map.of(FagsakYtelseType.SVANGERSKAPSPENGER, "§ 14-4"),
        Avslagsårsak.SØKER_IKKE_GRAVID_KVINNE,
        Avslagsårsak.SØKER_ER_IKKE_I_ARBEID,
        Avslagsårsak.SØKER_SKULLE_IKKE_SØKT_SVP,
        Avslagsårsak.ARBEIDSTAKER_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER,
        Avslagsårsak.ARBEIDSTAKER_KAN_OMPLASSERES,
        Avslagsårsak.SN_FL_HAR_IKKE_DOKUMENTERT_RISIKOFAKTORER,
        Avslagsårsak.SN_FL_HAR_MULIGHET_TIL_Å_TILRETTELEGGE_SITT_VIRKE),

    /**
     * Brukes i stedet for null der det er optional.
     */
    UDEFINERT("-", Map.of()),

    ;

    private static final Map<String, VilkårType> KODER = new LinkedHashMap<>();
    private static final Map<VilkårType, Set<Avslagsårsak>> INDEKS_VILKÅR_AVSLAGSÅRSAKER = new LinkedHashMap<>(); // NOSONAR
    private static final Map<Avslagsårsak, Set<VilkårType>> INDEKS_AVSLAGSÅRSAK_VILKÅR = new LinkedHashMap<>(); // NOSONAR
    public static final String KODEVERK = "VILKAR_TYPE";

    @JsonIgnore
    private Map<FagsakYtelseType, String> lovReferanser;

    @JsonIgnore
    private Set<Avslagsårsak> avslagsårsaker;

    private String kode;

    VilkårType(String kode,
               Map<FagsakYtelseType, String> lovReferanser,
               Avslagsårsak... avslagsårsaker) {
        this.kode = kode;
        this.lovReferanser = lovReferanser;
        this.avslagsårsaker = Set.of(avslagsårsaker);

    }

    public String getLovReferanse(FagsakYtelseType fagsakYtelseType) {
        return lovReferanser.get(fagsakYtelseType);
    }

    @Override
    public String toString() {
        return super.toString() + lovReferanser;
    }


    @JsonCreator
    public static VilkårType fraKode(@JsonProperty(value = "kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent VilkårType: " + kode);
        }
        return ad;
    }


    public Set<Avslagsårsak> getAvslagsårsaker() {
        return avslagsårsaker;
    }

    public static Set<VilkårType> getVilkårTyper(Avslagsårsak avslagsårsak) {
        return INDEKS_AVSLAGSÅRSAK_VILKÅR.get(avslagsårsak);
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }

            INDEKS_VILKÅR_AVSLAGSÅRSAKER.put(v, v.avslagsårsaker);
            v.avslagsårsaker.forEach(a -> INDEKS_AVSLAGSÅRSAK_VILKÅR.computeIfAbsent(a, k-> new HashSet<>(4)).add(v));
        }
    }

}
