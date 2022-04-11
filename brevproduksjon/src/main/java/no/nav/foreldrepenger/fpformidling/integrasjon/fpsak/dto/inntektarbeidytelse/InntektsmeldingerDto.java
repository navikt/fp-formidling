package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.inntektarbeidytelse;

import java.util.List;

public record InntektsmeldingerDto(List<InntektsmeldingDto> inntektsmeldinger) {

    public List<InntektsmeldingDto> getInntektsmeldinger() {
        return inntektsmeldinger != null ? inntektsmeldinger : List.of();
    }

}
