package no.nav.foreldrepenger.fpformidling.domene.uttak.svp;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fpsak;

public enum ArbeidsforholdIkkeOppfyltÅrsak  {

    INGEN(Fpsak.STANDARDKODE_UDEFINERT),
    HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO("8301"),
    UTTAK_KUN_PÅ_HELG("8302"),
    ARBEIDSGIVER_KAN_TILRETTELEGGE("8303"),
    ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN("8312"),
    ;

    private final String kode;

    ArbeidsforholdIkkeOppfyltÅrsak(String kode) {
        this.kode = kode;
    }

    // Legg på JsonValue hvis du vil bruke denne mot fpsak / fpdokgen
    public String getKode() {
        return kode;
    }

}
