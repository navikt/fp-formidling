package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Arbeidsforhold {
    private String arbeidsgiverNavn;
    private boolean gradering;
    private Prosent prosentArbeid;
    private Prosent stillingsprosent;
    private Prosent utbetalingsgrad;
    private NaturalytelseEndringType naturalytelseEndringType;
    private String naturalytelseEndringDato;
    private long naturalytelseNyDagsats;

    public boolean isGradering() {
        return gradering;
    }

    public Prosent getUtbetalingsgrad() {
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

        public Builder medProsentArbeid(Prosent prosentArbeid) {
            this.kladd.prosentArbeid = prosentArbeid;
            return this;
        }

        public Builder medStillingsprosent(Prosent stillingsprosent) {
            this.kladd.stillingsprosent = stillingsprosent;
            return this;
        }

        public Builder medUtbetalingsgrad(Prosent utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad;
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
