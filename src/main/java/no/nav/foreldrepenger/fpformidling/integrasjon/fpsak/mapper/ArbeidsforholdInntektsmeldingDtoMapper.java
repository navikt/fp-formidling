package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.ArbeidsforholdInntektsmelding;
import no.nav.foreldrepenger.kontrakter.fpsak.inntektsmeldinger.ArbeidsforholdInntektsmeldingerDto;

public class ArbeidsforholdInntektsmeldingDtoMapper {
    private ArbeidsforholdInntektsmeldingDtoMapper() {
    }

    public static List<ArbeidsforholdInntektsmelding> mapArbeidsforholdInntektsmeldingFraDto(ArbeidsforholdInntektsmeldingerDto dto, UnaryOperator<String> hentNavn) {
        return dto.arbeidsforholdInntektsmelding().stream().map(a -> mapArbeidsforholdInntektsmelding(a, hentNavn)).toList();
    }

    private static ArbeidsforholdInntektsmelding mapArbeidsforholdInntektsmelding(ArbeidsforholdInntektsmeldingerDto.ArbeidsforholdInntektsmeldingDto dto, UnaryOperator<String> hentNavn) {
        return new ArbeidsforholdInntektsmelding(dto.arbeidsgiverIdent(), hentNavn.apply(dto.arbeidsgiverIdent()), dto.stillingsprosent(),
            dto.erInntektsmeldingMottatt());
    }
}
