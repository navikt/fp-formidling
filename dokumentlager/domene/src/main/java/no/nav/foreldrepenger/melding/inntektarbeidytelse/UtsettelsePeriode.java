package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.UtsettelsePeriodeDto;

public class UtsettelsePeriode {

    private LocalDate fom;
    private LocalDate tom;
    private String utsettelseÅrsak;

    public UtsettelsePeriode(UtsettelsePeriodeDto dto) {
        this.fom = dto.getFom();
        this.tom = dto.getTom();
        this.utsettelseÅrsak = dto.getUtsettelseArsak().kode;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public String getUtsettelseÅrsak() {
        return utsettelseÅrsak;
    }
}
