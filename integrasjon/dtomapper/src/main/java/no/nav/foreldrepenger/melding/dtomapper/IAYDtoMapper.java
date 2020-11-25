package no.nav.foreldrepenger.melding.dtomapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektsmeldingDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.UtsettelsePeriodeDto;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.UtsettelsePeriode;
import no.nav.foreldrepenger.melding.ytelsefordeling.UtsettelseÅrsak;

public class IAYDtoMapper {

    public static InntektArbeidYtelse mapIAYFraDto(InntektArbeidYtelseDto dto, UnaryOperator<String> hentNavn) {
        List<Inntektsmelding> inntektsmeldinger = new ArrayList<>();
        dto.getInntektsmeldinger().stream().map(i -> mapInntektsmeldingFraDto(i, hentNavn)).forEach(inntektsmeldinger::add);
        return InntektArbeidYtelse.ny()
                .medInntektsmeldinger(inntektsmeldinger)
                .build();
    }

    public static Inntektsmelding mapInntektsmeldingFraDto(InntektsmeldingDto dto, UnaryOperator<String> hentNavn) {
        List<UtsettelsePeriode> utsettelsePerioder = new ArrayList<>();
        dto.getUtsettelsePerioder().stream().map(IAYDtoMapper::mapUtsettelsesPeriodeFraDto).forEach(utsettelsePerioder::add);
        LocalDate innsendingstidspunkt = dto.getInnsendingstidspunkt() != null ? dto.getInnsendingstidspunkt().toLocalDate() : LocalDate.now(); //TODO burde ikke være nødvendig
        return new Inntektsmelding(hentNavn.apply(dto.getArbeidsgiverReferanse()), dto.getArbeidsgiverReferanse(), utsettelsePerioder, innsendingstidspunkt);
    }

    public static UtsettelsePeriode mapUtsettelsesPeriodeFraDto(UtsettelsePeriodeDto dto) {
        return new UtsettelsePeriode(dto.getFom(), dto.getTom(), UtsettelseÅrsak.fraKode(dto.getUtsettelseArsak().getKode()));
    }
}
