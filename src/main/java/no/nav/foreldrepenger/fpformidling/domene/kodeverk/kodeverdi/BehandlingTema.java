package no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BehandlingTema implements Kodeverdi, KodeverdiMedOffisiellKode {

    ENGANGSSTØNAD("ENGST", "ab0327"),
    ENGANGSSTØNAD_FØDSEL("ENGST_FODS", "ab0050"),
    ENGANGSSTØNAD_ADOPSJON("ENGST_ADOP", "ab0027"),

    FORELDREPENGER("FORP", "ab0326"),
    FORELDREPENGER_FØDSEL("FORP_FODS", "ab0047"),
    FORELDREPENGER_ADOPSJON("FORP_ADOP", "ab0072"),

    SVANGERSKAPSPENGER("SVP", "ab0126"),

    OMS("OMS", "ab0271"),
    OMS_OMSORG("OMS_OMSORG", "ab0149"),
    OMS_OPP("OMS_OPP", "ab0141"),
    OMS_PLEIE_BARN("OMS_PLEIE_BARN", "ab0069"),
    OMS_PLEIE_BARN_NY("OMS_PLEIE_BARN_NY", "ab0320"),
    OMS_PLEIE_INSTU("OMS_PLEIE_INSTU", "ab0153"),

    UDEFINERT("-", null),
    ;

    @JsonValue
    private String kode;

    private String offisiellKode;


    BehandlingTema(String kode, String offisiellKode) {
        this.kode = kode;
        this.offisiellKode = offisiellKode;
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getOffisiellKode() {
        return offisiellKode;
    }

}
