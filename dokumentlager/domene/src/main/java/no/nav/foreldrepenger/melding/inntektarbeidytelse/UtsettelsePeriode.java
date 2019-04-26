package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.ytelsefordeling.UtsettelseÅrsak;

public class UtsettelsePeriode {

    private LocalDate fom;
    private LocalDate tom;
    private UtsettelseÅrsak utsettelseÅrsak;

    public UtsettelsePeriode(LocalDate fom, LocalDate tom, UtsettelseÅrsak utsettelseÅrsak) {
        this.fom = fom;
        this.tom = tom;
        this.utsettelseÅrsak = utsettelseÅrsak;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public UtsettelseÅrsak getUtsettelseÅrsak() {
        return utsettelseÅrsak;
    }
}
