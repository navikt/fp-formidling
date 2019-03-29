package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektsmeldingDto;

public class Inntektsmelding {

    private String arbeidsgiver;
    private String arbeidsgiverOrgnr;
    private LocalDate arbeidsgiverStartdato;
    private List<UtsettelsePeriode> utsettelsePerioder = new ArrayList<>();

    public Inntektsmelding(InntektsmeldingDto dto) {
        this.arbeidsgiver = dto.getArbeidsgiver();
        this.arbeidsgiverOrgnr = dto.getArbeidsgiverOrgnr();
        this.arbeidsgiverStartdato = dto.getArbeidsgiverStartdato();
        dto.getUtsettelsePerioder().forEach(up -> utsettelsePerioder.add(new UtsettelsePeriode(up)));
    }

    public String getArbeidsgiver() {
        return arbeidsgiver;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public LocalDate getArbeidsgiverStartdato() {
        return arbeidsgiverStartdato;
    }

    public List<UtsettelsePeriode> getUtsettelsePerioder() {
        return utsettelsePerioder;
    }
}
