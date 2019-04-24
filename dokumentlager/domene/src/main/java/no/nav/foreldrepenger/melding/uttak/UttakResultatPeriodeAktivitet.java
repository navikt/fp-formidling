package no.nav.foreldrepenger.melding.uttak;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UttakResultatPeriodeAktivitet {
    private String trekkonto;
    private int trekkdager;
    private String tidsperiode;
    private BigDecimal utbetalingsprosent;
    private boolean graderingInnvilget;
    private BigDecimal arbeidsprosent;
    private UttakResultatPeriode uttakResultatPeriode;
    private UttakAktivitet uttakAktivitet;

    private UttakResultatPeriodeAktivitet(Builder builder) {
        trekkonto = builder.trekkonto;
        trekkdager = builder.trekkdager;
        tidsperiode = builder.tidsperiode;
        utbetalingsprosent = builder.utbetalingsprosent;
        graderingInnvilget = builder.graderingInnvilget;
        arbeidsprosent = builder.arbeidsprosent;
        uttakResultatPeriode = builder.uttakResultatPeriode;
        uttakAktivitet = builder.uttakAktivitet;
    }

    public static Builder ny() {
        return new Builder();
    }

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
        return utbetalingsprosent;
    }

    public boolean getGraderingInnvilget() {
        return graderingInnvilget;
    }

    public BigDecimal getArbeidsprosent() {
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


    public static final class Builder {
        private String trekkonto;
        private int trekkdager;
        private String tidsperiode;
        private BigDecimal utbetalingsprosent;
        private boolean graderingInnvilget;
        private BigDecimal arbeidsprosent;
        private UttakResultatPeriode uttakResultatPeriode;
        private UttakAktivitet uttakAktivitet;

        private Builder() {
        }

        public Builder medTrekkonto(String trekkonto) {
            this.trekkonto = trekkonto;
            return this;
        }

        public Builder medTrekkdager(int trekkdager) {
            this.trekkdager = trekkdager;
            return this;
        }

        public Builder medTidsperiode(String tidsperiode) {
            this.tidsperiode = tidsperiode;
            return this;
        }

        public Builder medUtbetalingsprosent(BigDecimal utbetalingsprosent) {
            this.utbetalingsprosent = utbetalingsprosent;
            return this;
        }

        public Builder medGraderingInnvilget(boolean graderingInnvilget) {
            this.graderingInnvilget = graderingInnvilget;
            return this;
        }

        public Builder medArbeidsprosent(BigDecimal arbeidsprosent) {
            this.arbeidsprosent = arbeidsprosent;
            return this;
        }

        public Builder medUttakResultatPeriode(UttakResultatPeriode uttakResultatPeriode) {
            this.uttakResultatPeriode = uttakResultatPeriode;
            return this;
        }

        public Builder medUttakAktivitet(UttakAktivitet uttakAktivitet) {
            this.uttakAktivitet = uttakAktivitet;
            return this;
        }

        public UttakResultatPeriodeAktivitet build() {
            return new UttakResultatPeriodeAktivitet(this);
        }
    }
}
