package no.nav.foreldrepenger.melding.uttak;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UttakResultatPeriodeAktivitet {
    private String trekkonto;
    private int trekkdager;
    private String tidsperiode;
    private String utbetalingsprosent;
    private String graderingInnvilget;
    private String arbeidsprosent;
    private UttakResultatPeriode uttakResultatPeriode;
    private UttakAktivitet uttakAktivitet;

    public String getTrekkonto() {
        return trekkonto;
    }

    public int getTrekkdager() {
        return trekkdager;
    }

    public String getTidsperiode() {
        return tidsperiode;
    }

    public BigDecimal getUtbetalingsprosent() {
        return new BigDecimal(utbetalingsprosent);
    }

    public String getGraderingInnvilget() {
        return graderingInnvilget;
    }

    public String getArbeidsprosent() {
        return arbeidsprosent;
    }

    public UttakArbeidType getUttakArbeidType() {
        return uttakAktivitet.getUttakArbeidType();
    }

    public String getArbeidsgiverIdentifikator() {
        return uttakAktivitet.getArbeidsgiver().isPresent() ? uttakAktivitet.getArbeidsgiver().get().getIdentifikator() : null;
    }

    public String getArbeidsforholdId() {
        return uttakAktivitet.getArbeidsforholdRef().isPresent() ? uttakAktivitet.getArbeidsforholdRef().get().getReferanse() : null;
    }

    public LocalDate getFom() {
        return this.uttakResultatPeriode.getFom();
    }

    public LocalDate getTom() {
        return this.uttakResultatPeriode.getTom();
    }
}
