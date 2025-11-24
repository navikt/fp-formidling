package no.nav.foreldrepenger.fpformidling.domene.uttak.svp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fpsak;

public enum PeriodeIkkeOppfyltÅrsak {

    INGEN(Fpsak.STANDARDKODE_UDEFINERT),
    BRUKER_ER_DØD("8304"),
    BARN_ER_DØDT("8305"),
    BRUKER_ER_IKKE_MEDLEM("8306"),
    SØKT_FOR_SENT("8308"),
    PERIODEN_ER_IKKE_FØR_FØDSEL("8309"),
    PERIODEN_MÅ_SLUTTE_SENEST_TRE_UKER_FØR_TERMIN("8310"),
    PERIODE_SAMTIDIG_SOM_FERIE("8311"),
    PERIODEN_ER_ETTER_ET_OPPHOLD_I_UTTAK("8313"),
    BEGYNT_ANNEN_SAK("8314"),
    SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT("8315"),
    OPPTJENINGSVILKÅRET_IKKE_OPPFYLT("8316"),
    PERIODEN_ER_SAMTIDIG_SOM_SYKEPENGER("8317");

    private final String kode;

    PeriodeIkkeOppfyltÅrsak(String kode) {
        this.kode = kode;
    }

    public static PeriodeIkkeOppfyltÅrsak fra(String kode) {
        return Arrays.stream(PeriodeIkkeOppfyltÅrsak.values()).filter(å -> å.kode.equals(kode)).findFirst().orElseThrow();
    }

    // Legg på JsonValue hvis du vil bruke denne mot fpsak / fpdokgen
    public String getKode() {
        return kode;
    }

    public Optional<String> getLovHjemmelData() {
        return this.equals(INGEN) ? Optional.empty() : Optional.of("14-4");
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

