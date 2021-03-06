package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Næring {
    private boolean gradering;
    private Prosent utbetalingsgrad;
    private Prosent prosentArbeid;
    @JsonIgnore
    private int sistLignedeÅr;

    public static Builder ny() {
        return new Builder();
    }

    public boolean isGradering() {
        return gradering;
    }

    public Prosent getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public int getSistLignedeÅr() {
        return sistLignedeÅr;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Næring) object;
        return Objects.equals(gradering, that.gradering)
                && Objects.equals(utbetalingsgrad, that.utbetalingsgrad)
                && Objects.equals(prosentArbeid, that.prosentArbeid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gradering, utbetalingsgrad, prosentArbeid);
    }

    public static class Builder {
        private Næring kladd;

        private Builder() {
            this.kladd = new Næring();
        }

        public Builder medGradering(boolean gradering) {
            this.kladd.gradering = gradering;
            return this;
        }

        public Builder medUtbetalingsgrad(Prosent utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Builder medProsentArbeid(Prosent prosentArbeid) {
            this.kladd.prosentArbeid = prosentArbeid;
            return this;
        }

        public Builder medSistLignedeÅr(int sistLignedeÅr) {
            this.kladd.sistLignedeÅr = sistLignedeÅr;
            return this;
        }

        public Næring build() {
            return this.kladd;
        }
    }

}
