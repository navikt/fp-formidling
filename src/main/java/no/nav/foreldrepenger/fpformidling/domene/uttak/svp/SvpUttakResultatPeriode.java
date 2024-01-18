package no.nav.foreldrepenger.fpformidling.domene.uttak.svp;

import java.math.BigDecimal;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.typer.Dato;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;

public class SvpUttakResultatPeriode implements Comparable<SvpUttakResultatPeriode> {

    private final BigDecimal utbetalingsgrad;
    private final PeriodeResultatType periodeResultatType;
    private final PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak;
    private final DatoIntervall tidsperiode;
    private final String arbeidsgiverNavn;

    private SvpUttakResultatPeriode(Builder builder) {
        utbetalingsgrad = builder.utbetalingsgrad;
        arbeidsgiverNavn = builder.arbeidsgiverNavn;
        periodeResultatType = builder.periodeResultatType;
        periodeIkkeOppfyltÅrsak = builder.periodeIkkeOppfyltÅrsak;
        tidsperiode = builder.tidsperiode;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn == null ? "" : arbeidsgiverNavn;
    }

    public DatoIntervall getTidsperiode() {
        return tidsperiode;
    }

    public Dato getFom() {
        return tidsperiode.getFom();
    }

    public Dato getTom() {
        return tidsperiode.getTom();
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public PeriodeIkkeOppfyltÅrsak getPeriodeIkkeOppfyltÅrsak() {
        return periodeIkkeOppfyltÅrsak;
    }

    public boolean isInnvilget() {
        return Objects.equals(getPeriodeResultatType(), PeriodeResultatType.INNVILGET);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (SvpUttakResultatPeriode) o;
        return Objects.equals(getUtbetalingsgrad(), that.getUtbetalingsgrad()) && Objects.equals(getTidsperiode(), that.getTidsperiode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUtbetalingsgrad(), getTidsperiode());
    }

    @Override
    public int compareTo(SvpUttakResultatPeriode o) {
        return this.getTidsperiode().compareTo(o.getTidsperiode());
    }

    public static final class Builder {

        private BigDecimal utbetalingsgrad;
        private PeriodeResultatType periodeResultatType = PeriodeResultatType.IKKE_FASTSATT;
        private PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak = PeriodeIkkeOppfyltÅrsak.INGEN;
        private DatoIntervall tidsperiode;

        private String arbeidsgiverNavn;

        private Builder() {
            // Skjul default constructor
        }

        private Builder(SvpUttakResultatPeriode copy) {
            this.utbetalingsgrad = copy.getUtbetalingsgrad();
            this.arbeidsgiverNavn = copy.arbeidsgiverNavn;
            this.periodeResultatType = copy.getPeriodeResultatType();
            this.periodeIkkeOppfyltÅrsak = copy.getPeriodeIkkeOppfyltÅrsak();
            this.tidsperiode = copy.getTidsperiode();
        }

        public static Builder ny() {
            return new Builder();
        }

        public static Builder ny(SvpUttakResultatPeriode copy) {
            return new Builder(copy);
        }

        public Builder medUtbetalingsgrad(BigDecimal utbetalingsgrad) {
            this.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Builder medPeriodeResultatType(PeriodeResultatType periodeResultatType) {
            this.periodeResultatType = periodeResultatType;
            return this;
        }

        public Builder medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak) {
            this.periodeIkkeOppfyltÅrsak = periodeIkkeOppfyltÅrsak;
            return this;
        }

        public Builder medTidsperiode(DatoIntervall tidsperiode) {
            this.tidsperiode = tidsperiode;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public SvpUttakResultatPeriode build() {
            return new SvpUttakResultatPeriode(this);
        }

    }
}
