package no.nav.foreldrepenger.fpformidling.tilkjentytelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "TilkjentYtelseForeldrepenger{" + "perioder=" + perioder + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TilkjentYtelseForeldrepenger that = (TilkjentYtelseForeldrepenger) o;
        return Objects.equals(perioder, that.perioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perioder);
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
