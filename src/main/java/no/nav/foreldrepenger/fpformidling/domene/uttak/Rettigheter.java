package no.nav.foreldrepenger.fpformidling.domene.uttak;

import java.time.LocalDate;

public record Rettigheter(Rettighetstype opprinnelig,  //søknad eller forrige vedtak
                          Rettighetstype gjeldende,
                          EøsUttak eøsUttak) {

    public enum Rettighetstype {
        ALENEOMSORG,
        BEGGE_RETT,
        BEGGE_RETT_EØS,
        BARE_MOR_RETT,
        BARE_FAR_RETT,
        BARE_FAR_RETT_MOR_UFØR
    }

    public record EøsUttak(LocalDate fom, LocalDate tom, int forbruktFellesperiode, int fellesperiodeINorge) {
    }
}
