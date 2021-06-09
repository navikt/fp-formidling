package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AnnenAktivitet {
    private String aktivitetStatus;
    private boolean gradering;
    private BigDecimal utbetalingsgrad;
    private BigDecimal prosentArbeid;

    public boolean isGradering() {
        return gradering;
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (AnnenAktivitet) object;
        return Objects.equals(aktivitetStatus, that.aktivitetStatus)
                && Objects.equals(gradering, that.gradering)
                && Objects.equals(utbetalingsgrad, that.utbetalingsgrad)
                && Objects.equals(prosentArbeid, that.prosentArbeid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetStatus, gradering, utbetalingsgrad, prosentArbeid);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private AnnenAktivitet kladd;

        private Builder() {
            this.kladd = new AnnenAktivitet();
        }

        public Builder medAktivitetStatus(String aktivitetStatus) {
            this.kladd.aktivitetStatus = aktivitetStatus;
            return this;
        }

        public Builder medGradering(boolean gradering) {
            this.kladd.gradering = gradering;
            return this;
        }

        public Builder medUtbetalingsgrad(BigDecimal utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad.setScale(1, RoundingMode.HALF_UP);
            return this;
        }

        public Builder medProsentArbeid(BigDecimal prosentArbeid) {
            this.kladd.prosentArbeid = prosentArbeid.setScale(1, RoundingMode.HALF_UP);
            return this;
        }

        public AnnenAktivitet build() {
            return this.kladd;
        }
    }
}
