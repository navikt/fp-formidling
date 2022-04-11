package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.inntektarbeidytelse.InntektsmeldingDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.inntektarbeidytelse.InntektsmeldingerDto;

public class InntektsmeldingDtoMapper {

    public static Inntektsmeldinger mapIAYFraDto(InntektsmeldingerDto dto, UnaryOperator<String> hentNavn) {
        var inntektsmeldinger = dto.getInntektsmeldinger().stream()
                .map(i -> mapInntektsmeldingFraDto(i, hentNavn))
                .toList();
        return new Inntektsmeldinger(inntektsmeldinger);
    }

    public static Inntektsmelding mapInntektsmeldingFraDto(InntektsmeldingDto dto, UnaryOperator<String> hentNavn) {
        LocalDate innsendingstidspunkt = dto.innsendingstidspunkt() != null ? dto.innsendingstidspunkt().toLocalDate() : LocalDate.now(); //TODO burde ikke være nødvendig
        return new Inntektsmelding(hentNavn.apply(dto.arbeidsgiverReferanse()), dto.arbeidsgiverReferanse(), innsendingstidspunkt);
    }
}
