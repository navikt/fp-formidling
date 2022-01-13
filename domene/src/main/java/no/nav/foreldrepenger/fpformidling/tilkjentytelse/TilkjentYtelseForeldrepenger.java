package no.nav.foreldrepenger.fpformidling.tilkjentytelse;

import java.util.ArrayList;
import java.util.List;

public class TilkjentYtelseForeldrepenger {
    private List<TilkjentYtelsePeriode> perioder;

    private TilkjentYtelseForeldrepenger(Builder builder) {
        perioder = builder.perioder;
    }

    public static Builder ny() {
        return new Builder();
    }

    public List<TilkjentYtelsePeriode> getPerioder() {
        return perioder;
    }

    public static final class Builder {
        private List<TilkjentYtelsePeriode> perioder = new ArrayList<>();

        private Builder() {
        }

        public Builder leggTilPerioder(List<TilkjentYtelsePeriode> perioder) {
            this.perioder.addAll(perioder);
            return this;
        }

        public TilkjentYtelseForeldrepenger build() {
            return new TilkjentYtelseForeldrepenger(this);
        }
    }
}
