package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDateTime;

public class InntektsmeldingDto {
    private String arbeidsgiverReferanse;
    private LocalDateTime innsendingstidspunkt;

    InntektsmeldingDto() {
        // trengs for deserialisering av JSON
    }


    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public void setArbeidsgiverReferanse(String arbeidsgiverReferanse) {
        this.arbeidsgiverReferanse = arbeidsgiverReferanse;
    }

    public LocalDateTime getInnsendingstidspunkt() {
        return innsendingstidspunkt;
    }

    public void setInnsendingstidspunkt(LocalDateTime innsendingstidspunkt) {
        this.innsendingstidspunkt = innsendingstidspunkt;
    }
}
