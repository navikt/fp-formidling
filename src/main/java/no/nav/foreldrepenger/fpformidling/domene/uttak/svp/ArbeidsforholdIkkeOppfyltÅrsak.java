package no.nav.foreldrepenger.fpformidling.domene.uttak.svp;

public enum ArbeidsforholdIkkeOppfyltÅrsak  {

    INGEN("-"),
    HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO("8301"),
    UTTAK_KUN_PÅ_HELG("8302"),
    ARBEIDSGIVER_KAN_TILRETTELEGGE("8303"),
    ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN("8312"),
    ;

    private final String kode;

    ArbeidsforholdIkkeOppfyltÅrsak(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

}
