package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

public record Rettigheter(Rettighetstype opprinnelig,  //søknad eller forrige vedtak
                          Rettighetstype gjeldende,
                          EøsUttak eøsUttak) {

    public enum Rettighetstype {
        ALENEOMSORG,
        BEGGE_RETT,
        BEGGE_RETT_EØS,
        BARE_SØKER_RETT,
        BARE_FAR_RETT_MOR_UFØR
    }

    public record EøsUttak(String fom, String tom, int forbruktFellesperiode, int fellesperiodeINorge) {
    }
}
