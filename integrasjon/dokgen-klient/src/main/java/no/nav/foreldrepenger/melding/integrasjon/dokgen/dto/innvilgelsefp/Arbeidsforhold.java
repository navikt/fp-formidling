package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Arbeidsforhold {
    private String arbeidsgiverNavn;
    private boolean gradering;
    private BigDecimal prosentArbeid;
    private BigDecimal stillingsprosent;
    private BigDecimal utbetalingsgrad;
    private NaturalytelseEndringType naturalytelseEndringType;
    private String naturalytelseEndringDato;
    private long naturalytelseNyDagsats;

    public boolean isGradering() {
        return gradering;
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public NaturalytelseEndringType getNaturalytelseEndringType() {
        return naturalytelseEndringType;
    }

    public String getNaturalytelseEndringDato() {
        return naturalytelseEndringDato;
    }

    public long getNaturalytelseNyDagsats() {
        return naturalytelseNyDagsats;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Arbeidsforhold) object;
        return Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn)
                && Objects.equals(gradering, that.gradering)
                && Objects.equals(prosentArbeid, that.prosentArbeid)
                && Objects.equals(stillingsprosent, that.stillingsprosent)
                && Objects.equals(utbetalingsgrad, that.utbetalingsgrad)
                && Objects.equals(naturalytelseEndringType, that.naturalytelseEndringType)
                && Objects.equals(naturalytelseEndringDato, that.naturalytelseEndringDato)
                && Objects.equals(naturalytelseNyDagsats, that.naturalytelseNyDagsats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverNavn, gradering, prosentArbeid, stillingsprosent, utbetalingsgrad,
                naturalytelseEndringType, naturalytelseEndringDato, naturalytelseNyDagsats);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private Arbeidsforhold kladd;

        private Builder() {
            this.kladd = new Arbeidsforhold();
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medGradering(boolean gradering) {
            this.kladd.gradering = gradering;
            return this;
        }

        public Builder medProsentArbeid(BigDecimal prosentArbeid) {
            this.kladd.prosentArbeid = prosentArbeid.setScale(1, RoundingMode.HALF_UP);
            return this;
        }

        public Builder medStillingsprosent(BigDecimal stillingsprosent) {
            this.kladd.stillingsprosent = stillingsprosent.setScale(1, RoundingMode.HALF_UP);
            return this;
        }

        public Builder medUtbetalingsgrad(BigDecimal utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad.setScale(1, RoundingMode.HALF_UP);
            return this;
        }

        public Builder medNaturalytelseEndringType(NaturalytelseEndringType naturalytelseEndringType) {
            this.kladd.naturalytelseEndringType = naturalytelseEndringType;
            return this;
        }

        public Builder medNaturalytelseEndringDato(String naturalytelseEndringDato) {
            this.kladd.naturalytelseEndringDato = naturalytelseEndringDato;
            return this;
        }

        public Builder medNaturalytelseNyDagsats(long naturalytelseNyDagsats) {
            this.kladd.naturalytelseNyDagsats = naturalytelseNyDagsats;
            return this;
        }

        public Arbeidsforhold build() {
            return this.kladd;
        }
    }
}
