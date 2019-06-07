package no.nav.foreldrepenger.melding.uttak;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UttakResultatPerioder {
    private List<UttakResultatPeriode> perioder;
    private List<UttakResultatPeriode> perioderAnnenPart;
    private boolean aleneomsorg;
    private boolean annenForelderHarRett;

    private UttakResultatPerioder(Builder builder) {
        perioder = builder.perioder;
        perioderAnnenPart = builder.perioderAnnenPart;
        aleneomsorg = builder.aleneomsorg;
        annenForelderHarRett = builder.annenForelderHarRett;
    }

    public static Builder ny() {
        return new Builder();
    }

    public List<UttakResultatPeriode> getPerioder() {
        return perioder.stream().sorted(Comparator.comparing(UttakResultatPeriode::getFomDato)).collect(Collectors.toList());
    }

    public List<UttakResultatPeriode> getPerioderAnnenPart() {
        return perioderAnnenPart;
    }

    public boolean isAleneomsorg() {
        return aleneomsorg;
    }

    public boolean isAnnenForelderHarRett() {
        return annenForelderHarRett;
    }

    public static final class Builder {
        private List<UttakResultatPeriode> perioder = new ArrayList<>();
        private List<UttakResultatPeriode> perioderAnnenPart = new ArrayList<>();
        private boolean aleneomsorg;
        private boolean annenForelderHarRett;

        private Builder() {
        }

        public Builder medPerioder(List<UttakResultatPeriode> perioder) {
            this.perioder = perioder;
            return this;
        }

        public Builder medPerioderAnnenPart(List<UttakResultatPeriode> perioderAnnenPart) {
            this.perioderAnnenPart = perioderAnnenPart;
            return this;
        }

        public Builder medAleneomsorg(boolean aleneomsorg) {
            this.aleneomsorg = aleneomsorg;
            return this;
        }

        public Builder medAnnenForelderHarRett(boolean annenForelderHarRett) {
            this.annenForelderHarRett = annenForelderHarRett;
            return this;
        }

        public UttakResultatPerioder build() {
            return new UttakResultatPerioder(this);
        }
    }
}
