package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.time.LocalDate;
import java.util.List;


public class Inntektsmelding {
    private String arbeidsgiverReferanse;
    private String arbeidsgiverNavn;
    private List<UtsettelsePeriode> utsettelsePerioder;
    private LocalDate innsendingstidspunkt;

    public Inntektsmelding(String arbeidsgiverNavn, String arbeidsgiverOrgnr, List<UtsettelsePeriode> utsettelsePerioder, LocalDate innsendingstidspunkt) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
        this.arbeidsgiverReferanse = arbeidsgiverOrgnr;
        this.utsettelsePerioder = utsettelsePerioder;
        this.innsendingstidspunkt = innsendingstidspunkt;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public List<UtsettelsePeriode> getUtsettelsePerioder() {
        return utsettelsePerioder;
    }

    public LocalDate getInnsendingstidspunkt() {
        return innsendingstidspunkt;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }
}
