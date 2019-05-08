package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.time.LocalDate;
import java.util.List;


public class Inntektsmelding {

    private String arbeidsgiver;
    private String arbeidsgiverOrgnr;
    private LocalDate arbeidsgiverStartdato;
    private List<UtsettelsePeriode> utsettelsePerioder;
    private LocalDate innsendingstidspunkt;

    public Inntektsmelding(String arbeidsgiver, String arbeidsgiverOrgnr, LocalDate arbeidsgiverStartdato, List<UtsettelsePeriode> utsettelsePerioder, LocalDate innsendingstidspunkt) {
        this.arbeidsgiver = arbeidsgiver;
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
        this.arbeidsgiverStartdato = arbeidsgiverStartdato;
        this.utsettelsePerioder = utsettelsePerioder;
        this.innsendingstidspunkt = innsendingstidspunkt;
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

    public LocalDate getInnsendingstidspunkt() {
        return innsendingstidspunkt;
    }
}
