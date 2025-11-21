package no.nav.foreldrepenger.fpformidling.domene.uttak.svp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum PeriodeIkkeOppfyltÅrsak implements Kodeverdi {

    INGEN("-", null),
    BRUKER_ER_DØD("8304", "14-4"),
    BARN_ER_DØDT("8305", "14-4"),
    BRUKER_ER_IKKE_MEDLEM("8306", "14-4"),
    SØKT_FOR_SENT("8308", "14-4"),
    PERIODEN_ER_IKKE_FØR_FØDSEL("8309", "14-4"),
    PERIODEN_MÅ_SLUTTE_SENEST_TRE_UKER_FØR_TERMIN("8310", "14-4"),
    PERIODE_SAMTIDIG_SOM_FERIE("8311", "14-4"),
    PERIODEN_ER_ETTER_ET_OPPHOLD_I_UTTAK("8313", "14-4"),
    BEGYNT_ANNEN_SAK("8314", "14-4"),
    SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT("8315", "14-4"),
    OPPTJENINGSVILKÅRET_IKKE_OPPFYLT("8316", "14-4"),
    PERIODEN_ER_SAMTIDIG_SOM_SYKEPENGER("8317", "14-4");

    private static final Map<String, PeriodeIkkeOppfyltÅrsak> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @JsonValue
    private String kode;

    @JsonIgnore
    private String lovHjemmel;

    PeriodeIkkeOppfyltÅrsak(String kode, String lovHjemmel) {
        this.kode = kode;
        this.lovHjemmel = lovHjemmel;
    }

    public static PeriodeIkkeOppfyltÅrsak fra(String kode) {
        return Arrays.stream(PeriodeIkkeOppfyltÅrsak.values()).filter(å -> å.kode.equals(kode)).findFirst().orElseThrow();
    }

    @Override
    public String getKode() {
        return kode;
    }

    public Optional<String> getLovHjemmelData() {
        return Optional.ofNullable(lovHjemmel);
    }

    public static Set<PeriodeIkkeOppfyltÅrsak> opphørsAvslagÅrsaker() {
        return new HashSet<>(
            Arrays.asList(BRUKER_ER_DØD, BARN_ER_DØDT, BRUKER_ER_IKKE_MEDLEM, PERIODEN_ER_IKKE_FØR_FØDSEL, BEGYNT_ANNEN_SAK, SØKT_FOR_SENT,
                SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT, OPPTJENINGSVILKÅRET_IKKE_OPPFYLT));
    }

    public static Set<PeriodeIkkeOppfyltÅrsak> opphørSvpInngangsvilkårMedUttak() {
        return new HashSet<>(Arrays.asList(SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT, OPPTJENINGSVILKÅRET_IKKE_OPPFYLT));
    }

}

