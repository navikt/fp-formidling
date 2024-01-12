package no.nav.foreldrepenger.fpformidling.domene.uttak.fp;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.domene.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;

public class UttakResultatPeriodeAktivitet {
    private final StønadskontoType trekkonto;
    private final BigDecimal trekkdager;
    private final BigDecimal utbetalingsprosent;
    private final boolean graderingInnvilget;
    private final BigDecimal arbeidsprosent;
    private UttakResultatPeriode uttakResultatPeriode;
    private final UttakAktivitet uttakAktivitet;

    private UttakResultatPeriodeAktivitet(Builder builder) {
        trekkonto = builder.trekkonto;
        trekkdager = builder.trekkdager;
        utbetalingsprosent = builder.utbetalingsprosent;
        graderingInnvilget = builder.graderingInnvilget;
        arbeidsprosent = builder.arbeidsprosent;
        uttakResultatPeriode = builder.uttakResultatPeriode;
        uttakAktivitet = builder.uttakAktivitet;
    }

    public void leggTilPeriode(UttakResultatPeriode periode) {
        if (this.uttakResultatPeriode == null) {
            this.uttakResultatPeriode = periode;
        }
    }

    public static Builder ny() {
        return new Builder();
    }

    public StønadskontoType getTrekkonto() {
        return trekkonto;
    }

    public BigDecimal getTrekkdager() {
        return trekkdager;
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
        return uttakAktivitet.getArbeidsgiver().map(Arbeidsgiver::arbeidsgiverReferanse).orElse(null);
    }

    public String getArbeidsforholdId() {
        return uttakAktivitet.getArbeidsforholdRef().map(ArbeidsforholdRef::getReferanse).orElse(null);
    }

    public LocalDate getFom() {
        return this.uttakResultatPeriode.getFom();
    }

    public LocalDate getTom() {
        return this.uttakResultatPeriode.getTom();
    }

    public static final class Builder {
        private StønadskontoType trekkonto;
        private BigDecimal trekkdager;
        private BigDecimal utbetalingsprosent;
        private boolean graderingInnvilget;
        private BigDecimal arbeidsprosent;
        private UttakResultatPeriode uttakResultatPeriode;
        private UttakAktivitet uttakAktivitet;

        private Builder() {
        }

        public Builder medTrekkonto(StønadskontoType trekkonto) {
            this.trekkonto = trekkonto;
            return this;
        }

        public Builder medTrekkdager(BigDecimal trekkdager) {
            this.trekkdager = trekkdager;
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
