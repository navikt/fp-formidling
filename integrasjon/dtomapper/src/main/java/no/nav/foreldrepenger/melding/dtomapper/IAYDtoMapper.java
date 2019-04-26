package no.nav.foreldrepenger.melding.dtomapper;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektsmeldingDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.UtsettelsePeriodeDto;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.UtsettelsePeriode;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.ytelsefordeling.UtsettelseÅrsak;

@ApplicationScoped
public class IAYDtoMapper {

    private KodeverkRepository kodeverkRepository;

    @Inject
    public IAYDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public IAYDtoMapper() {
        //CDI
    }

    public InntektArbeidYtelse mapIAYFraDto(InntektArbeidYtelseDto dto) {
        List<Inntektsmelding> inntektsmeldinger = new ArrayList<>();
        dto.getInntektsmeldinger().stream().map(this::mapInntektsmeldingFraDto).forEach(inntektsmeldinger::add);
        return InntektArbeidYtelse.ny()
                .medInntektsmeldinger(inntektsmeldinger)
                .build();
    }

    public Inntektsmelding mapInntektsmeldingFraDto(InntektsmeldingDto dto) {
        List<UtsettelsePeriode> utsettelsePerioder = new ArrayList<>();
        dto.getUtsettelsePerioder().stream().map(this::mapUtsettelsesPeriodeFraDto).forEach(utsettelsePerioder::add);
        return new Inntektsmelding(dto.getArbeidsgiver(), dto.getArbeidsgiverOrgnr(), dto.getArbeidsgiverStartdato(), utsettelsePerioder);
    }

    public UtsettelsePeriode mapUtsettelsesPeriodeFraDto(UtsettelsePeriodeDto dto) {
        return new UtsettelsePeriode(dto.getFom(), dto.getTom(), kodeverkRepository.finn(UtsettelseÅrsak.class, dto.getUtsettelseArsak().getKode()));
    }
}
