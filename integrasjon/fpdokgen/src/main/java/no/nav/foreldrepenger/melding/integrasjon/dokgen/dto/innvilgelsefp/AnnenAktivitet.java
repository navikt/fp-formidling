package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Prosent;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AnnenAktivitet {
    private String aktivitetStatus;
    private boolean gradering;
    private Prosent utbetalingsgrad;
    private Prosent prosentArbeid;

    @JsonIgnore
    private int aktivitetDagsats;

    public boolean isGradering() {
        return gradering;
    }

    public Prosent getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    public int getAktivitetDagsats() {
        return aktivitetDagsats;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (AnnenAktivitet) object;
        return Objects.equals(aktivitetStatus, that.aktivitetStatus)
                && Objects.equals(gradering, that.gradering)
                && Objects.equals(utbetalingsgrad, that.utbetalingsgrad)
                && Objects.equals(prosentArbeid, that.prosentArbeid)
                && Objects.equals(aktivitetDagsats, that.aktivitetDagsats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetStatus, gradering, utbetalingsgrad, prosentArbeid, aktivitetDagsats);
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

        public Builder medUtbetalingsgrad(Prosent utbetalingsgrad) {
            this.kladd.utbetalingsgrad = utbetalingsgrad;
            return this;
        }

        public Builder medProsentArbeid(Prosent prosentArbeid) {
            this.kladd.prosentArbeid = prosentArbeid;
            return this;
        }

        public Builder medAktivitetDagsats(int aktivitetDagsats) {
            this.kladd.aktivitetDagsats = aktivitetDagsats;
            return this;
        }

        public AnnenAktivitet build() {
            return this.kladd;
        }
    }
}
