package no.nav.foreldrepenger.fpformidling.uttak.svp;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.typer.Dato;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;

public class SvpUttakResultatPeriode implements Comparable<SvpUttakResultatPeriode> {

    private BigDecimal utbetalingsgrad;
    private PeriodeResultatType periodeResultatType;
    private PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak;
    private DatoIntervall tidsperiode;

    private long aktivitetDagsats;
    private Optional<String> arbeidsgiverNavn;

    private SvpUttakResultatPeriode(Builder builder) {
        utbetalingsgrad = builder.utbetalingsgrad;
        aktivitetDagsats = builder.aktivitetDagsats;
        arbeidsgiverNavn = builder.arbeidsgiverNavn;
        periodeResultatType = builder.periodeResultatType;
        periodeIkkeOppfyltÅrsak = builder.periodeIkkeOppfyltÅrsak;
        tidsperiode = builder.tidsperiode;
    }

    public long getAktivitetDagsats() {
        return aktivitetDagsats;
    }

    public int getAarsakskode() {
        if (PeriodeIkkeOppfyltÅrsak.INGEN.getKode().equals(getPeriodeIkkeOppfyltÅrsak().getKode())) {
            return 0;
        }
        return Integer.parseInt(getPeriodeIkkeOppfyltÅrsak().getKode());
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn.orElse("");
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
        return Objects.equals(getUtbetalingsgrad(), that.getUtbetalingsgrad()) && getAktivitetDagsats() == that.getAktivitetDagsats() && Objects.equals(
            getTidsperiode(), that.getTidsperiode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUtbetalingsgrad(), getAktivitetDagsats(), getTidsperiode());
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

        private long aktivitetDagsats;
        private Optional<String> arbeidsgiverNavn = Optional.empty();

        private Builder() {
            // Skjul default constructor
        }

        private Builder(SvpUttakResultatPeriode copy) {
            this.utbetalingsgrad = copy.getUtbetalingsgrad();
            this.aktivitetDagsats = copy.getAktivitetDagsats();
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

        public Builder medAktivitetDagsats(long aktivitetDagsats) {
            this.aktivitetDagsats = aktivitetDagsats;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.arbeidsgiverNavn = Optional.ofNullable(arbeidsgiverNavn);
            return this;
        }

        public SvpUttakResultatPeriode build() {
            return new SvpUttakResultatPeriode(this);
        }

    }
}
