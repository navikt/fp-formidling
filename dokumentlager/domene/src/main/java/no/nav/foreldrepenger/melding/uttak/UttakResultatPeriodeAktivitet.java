package no.nav.foreldrepenger.melding.uttak;

import java.math.BigDecimal;

public class UttakResultatPeriodeAktivitet {
    private String trekkonto;
    private String trekkdager;
    private String tidsperiode;
    private String utbetalingsprosent;
    private String graderingInnvilget;
    private String arbeidsprosent;
    private Object UttakResultatPeriode;

    public String getTrekkonto() {
        return trekkonto;
    }

    public String getTrekkdager() {
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

    public Object getUttakResultatPeriode() {
        return UttakResultatPeriode;
    }
}
