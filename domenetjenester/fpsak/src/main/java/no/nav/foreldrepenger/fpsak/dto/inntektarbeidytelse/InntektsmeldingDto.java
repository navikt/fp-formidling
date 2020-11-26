package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InntektsmeldingDto {
    private String arbeidsgiverReferanse;
    private LocalDateTime innsendingstidspunkt;

    private List<UtsettelsePeriodeDto> utsettelsePerioder = new ArrayList<>();

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

    public List<UtsettelsePeriodeDto> getUtsettelsePerioder() {
        return utsettelsePerioder;
    }

    public void setUtsettelsePerioder(List<UtsettelsePeriodeDto> utsettelsePerioder) {
        this.utsettelsePerioder = utsettelsePerioder;
    }
}
