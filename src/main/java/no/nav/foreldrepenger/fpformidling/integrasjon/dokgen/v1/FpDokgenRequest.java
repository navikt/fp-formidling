package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.v1;

import jakarta.validation.constraints.NotNull;

public record FpDokgenRequest(@NotNull String malNavn, Språk språk, @NotNull CssStyling cssStyling, @NotNull String inputData) {

    public enum Språk {
        BOKMÅL,
        NYNORSK,
        ENGELSK,
    }

    public enum CssStyling {
        PDF,
        HTML,
        INNTEKTSMELDING_PDF
    }
}
