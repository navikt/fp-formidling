package no.nav.foreldrepenger.melding.uttak.svp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;

public class SvpUttakResultatPerioder implements Comparable<SvpUttakResultatPerioder> {
    private long utbetalingsgrad;
    private Optional<String> arbeidsgiverNavn;
    private Optional<ArbeidsforholdIkkeOppfyltÅrsak> arbeidsforholdIkkeOppfyltÅrsak;

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn.orElse("arbeidsgiver");
    }

    private long dagsats;

    private AktivitetStatus aktivitetStatus;

    private PeriodeResultatType periodeResultatType;

    private PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak;

    private DatoIntervall tidsperiode;

    private List<SvpUttakResultatPeriode> perioder;

    private SvpUttakResultatPerioder(Builder builder) {
        utbetalingsgrad = builder.utbetalingsgrad;
        arbeidsgiverNavn = builder.arbeidsgiverNavn;
        arbeidsforholdIkkeOppfyltÅrsak = builder.arbeidsforholdIkkeOppfyltÅrsak;
        aktivitetStatus = builder.aktivitetStatus;
        periodeResultatType = builder.periodeResultatType;
        periodeIkkeOppfyltÅrsak = builder.periodeIkkeOppfyltÅrsak;
        tidsperiode = builder.tidsperiode;
        perioder = builder.perioder;
        dagsats = builder.dagsats;
    }

    public DatoIntervall getTidsperiode() {
        return tidsperiode;
    }

    public LocalDate getFom() {
        return tidsperiode.getFomDato();
    }

    public LocalDate getTom() {
        return tidsperiode.getTomDato();
    }

    public long getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public PeriodeIkkeOppfyltÅrsak getPeriodeIkkeOppfyltAarsak() {
        return periodeIkkeOppfyltÅrsak;
    }

    public boolean isInnvilget() {
        return Objects.equals(getPeriodeResultatType(), PeriodeResultatType.INNVILGET);
    }

    public List<SvpUttakResultatPeriode> getPerioder() {
        return perioder.stream().sorted().collect(Collectors.toList());
    }

    public static Builder ny() {
        return new Builder();
    }

    @Override
    public int compareTo(SvpUttakResultatPerioder o) {
        int compareFom = this.getFom().compareTo(o.getFom());
        int compareTom = this.getTom().compareTo(o.getTom());
        return compareFom != 0 ? compareFom : compareTom;
    }


    public static final class Builder {
        private long utbetalingsgrad;
        private long dagsats;
        private Optional<String> arbeidsgiverNavn = Optional.empty();
        private Optional<ArbeidsforholdIkkeOppfyltÅrsak> arbeidsforholdIkkeOppfyltÅrsak = Optional.empty();
        private AktivitetStatus aktivitetStatus;
        private PeriodeResultatType periodeResultatType = PeriodeResultatType.IKKE_FASTSATT;
        private PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak = PeriodeIkkeOppfyltÅrsak.INGEN;
        private DatoIntervall tidsperiode;
        private List<SvpUttakResultatPeriode> perioder = new ArrayList<>();

        public Builder() {
        }

        public Builder medUtbetalingsgrad(BigDecimal utbetalingsgrad) {
            this.utbetalingsgrad = utbetalingsgrad.longValue();
            return this;
        }

        public Builder medDagsats(long dagsats) {
            this.dagsats = dagsats;
            return this;
        }

        public Builder medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
            this.arbeidsforholdIkkeOppfyltÅrsak = Optional.ofNullable(arbeidsforholdIkkeOppfyltÅrsak);
            return this;
        }

        public Builder medAktivitetStatus(AktivitetStatus aktivitetStatus) {
            this.aktivitetStatus = aktivitetStatus;
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

        public Builder medPerioder(List<SvpUttakResultatPeriode> perioder) {
            this.perioder.addAll(perioder);
            return this;
        }

        public Builder medPeriode(SvpUttakResultatPeriode periode) {
            this.perioder.add(periode);
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.arbeidsgiverNavn = Optional.ofNullable(arbeidsgiverNavn);
            return this;
        }

        public SvpUttakResultatPerioder build() {
            return new SvpUttakResultatPerioder(this);
        }
    }
}
