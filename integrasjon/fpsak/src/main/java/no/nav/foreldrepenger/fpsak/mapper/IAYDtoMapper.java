package no.nav.foreldrepenger.fpsak.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektsmeldingDto;

public class IAYDtoMapper {

    public static InntektArbeidYtelse mapIAYFraDto(InntektArbeidYtelseDto dto, UnaryOperator<String> hentNavn) {
        List<Inntektsmelding> inntektsmeldinger = new ArrayList<>();
        dto.getInntektsmeldinger().stream().map(i -> mapInntektsmeldingFraDto(i, hentNavn)).forEach(inntektsmeldinger::add);
        return InntektArbeidYtelse.ny()
                .medInntektsmeldinger(inntektsmeldinger)
                .build();
    }

    public static Inntektsmelding mapInntektsmeldingFraDto(InntektsmeldingDto dto, UnaryOperator<String> hentNavn) {
        LocalDate innsendingstidspunkt = dto.getInnsendingstidspunkt() != null ? dto.getInnsendingstidspunkt().toLocalDate() : LocalDate.now(); //TODO burde ikke være nødvendig
        return new Inntektsmelding(hentNavn.apply(dto.getArbeidsgiverReferanse()), dto.getArbeidsgiverReferanse(), innsendingstidspunkt);
    }
}