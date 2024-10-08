package no.nav.foreldrepenger.fpformidling.domene.uttak.fp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;

public class UttakResultatPeriode {
    private final PeriodeResultatÅrsak periodeResultatÅrsak;
    private final DatoIntervall tidsperiode;
    private final PeriodeResultatÅrsak graderingAvslagÅrsak;
    private final PeriodeResultatType periodeResultatType;
    private final List<UttakResultatPeriodeAktivitet> aktiviteter;
    private final LocalDate tidligstMottattDato;
    private final boolean erUtbetalingRedusertTilMorsStillingsprosent;

    private UttakResultatPeriode(Builder builder) {
        periodeResultatÅrsak = builder.periodeResultatÅrsak;
        tidsperiode = builder.tidsperiode;
        graderingAvslagÅrsak = builder.graderingAvslagÅrsak;
        periodeResultatType = builder.periodeResultatType;
        aktiviteter = builder.aktiviteter;
        tidligstMottattDato = builder.tidligstMottattDato;
        erUtbetalingRedusertTilMorsStillingsprosent = builder.erUtbetalingRedusertTilMorsStillingsprosent;
    }

    public static Builder ny() {
        return new Builder();
    }

    public PeriodeResultatÅrsak getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak != null ? periodeResultatÅrsak : PeriodeResultatÅrsak.UKJENT;
    }

    public LocalDate getFom() {
        return tidsperiode.getFomDato();
    }

    public LocalDate getTom() {
        return tidsperiode.getTomDato();
    }

    public PeriodeResultatÅrsak getGraderingAvslagÅrsak() {
        return graderingAvslagÅrsak == null ? PeriodeResultatÅrsak.UKJENT : graderingAvslagÅrsak;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public List<UttakResultatPeriodeAktivitet> getAktiviteter() {
        return aktiviteter;
    }

    public boolean isInnvilget() {
        return Objects.equals(getPeriodeResultatType(), PeriodeResultatType.INNVILGET);
    }

    public boolean erGraderingInnvilget() {
        return aktiviteter.stream().anyMatch(UttakResultatPeriodeAktivitet::getGraderingInnvilget);
    }

    public LocalDate getTidligstMottattDato() {
        return tidligstMottattDato;
    }

    public boolean erUtbetalingRedusertTilMorsStillingsprosent() {
        return erUtbetalingRedusertTilMorsStillingsprosent;
    }

    public static final class Builder {
        private PeriodeResultatÅrsak periodeResultatÅrsak;
        private DatoIntervall tidsperiode;
        private PeriodeResultatÅrsak graderingAvslagÅrsak;
        private PeriodeResultatType periodeResultatType;
        private List<UttakResultatPeriodeAktivitet> aktiviteter = new ArrayList<>();
        private LocalDate tidligstMottattDato;
        private boolean erUtbetalingRedusertTilMorsStillingsprosent;

        private Builder() {
        }

        public Builder medPeriodeResultatÅrsak(PeriodeResultatÅrsak periodeResultatÅrsak) {
            this.periodeResultatÅrsak = periodeResultatÅrsak;
            return this;
        }

        public Builder medTidsperiode(DatoIntervall tidsperiode) {
            this.tidsperiode = tidsperiode;
            return this;
        }

        public Builder medGraderingAvslagÅrsak(PeriodeResultatÅrsak graderingAvslagÅrsak) {
            this.graderingAvslagÅrsak = graderingAvslagÅrsak;
            return this;
        }

        public Builder medPeriodeResultatType(PeriodeResultatType periodeResultatType) {
            this.periodeResultatType = periodeResultatType;
            return this;
        }

        public Builder medAktiviteter(List<UttakResultatPeriodeAktivitet> aktiviteter) {
            this.aktiviteter = aktiviteter;
            return this;
        }

        public Builder medTidligstMottattDato(LocalDate tidligstMottattDato) {
            this.tidligstMottattDato = tidligstMottattDato;
            return this;
        }

        public Builder medErUtbetalingRedusertTilMorsStillingsprosent(boolean erUtbetalingRedusertTilMorsStillingsprosent) {
            this.erUtbetalingRedusertTilMorsStillingsprosent = erUtbetalingRedusertTilMorsStillingsprosent;
            return this;
        }

        public UttakResultatPeriode build() {
            return new UttakResultatPeriode(this);
        }
    }
}
