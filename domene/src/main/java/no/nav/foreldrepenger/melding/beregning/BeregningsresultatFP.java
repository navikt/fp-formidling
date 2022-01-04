package no.nav.foreldrepenger.melding.beregning;

import java.util.ArrayList;
import java.util.List;

public class BeregningsresultatFP {
    private List<BeregningsresultatPeriode> beregningsresultatPerioder = new ArrayList<>();

    private BeregningsresultatFP(Builder builder) {
        beregningsresultatPerioder = builder.beregningsresultatPerioder;
    }

    public static Builder ny() {
        return new Builder();
    }

    public List<BeregningsresultatPeriode> getBeregningsresultatPerioder() {
        return beregningsresultatPerioder;
    }


    public static final class Builder {
        private List<BeregningsresultatPeriode> beregningsresultatPerioder = new ArrayList<>();

        private Builder() {
        }

        public Builder leggTilBeregningsresultatPerioder(List<BeregningsresultatPeriode> beregningsresultatPerioder) {
            this.beregningsresultatPerioder.addAll(beregningsresultatPerioder);
            return this;
        }

        public BeregningsresultatFP build() {
            return new BeregningsresultatFP(this);
        }
    }
}
