package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Naturalytelse {
    public enum NaturalytelseStatus {
        TILKOMMER,
        BORTFALLER
    }

    private NaturalytelseStatus status;
    @JsonIgnore
    private LocalDate endringsdatoDate;
    private String endringsdato;
    private long nyDagsats;
    private String arbeidsgiverNavn;

    public NaturalytelseStatus getStatus() {
        return status;
    }

    public LocalDate getEndringsdatoDate() {
        return endringsdatoDate;
    }

    public String getEndringsdato() {
        return endringsdato;
    }

    public long getNyDagsats() {
        return nyDagsats;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Naturalytelse) object;
        return Objects.equals(status, that.status)
                && Objects.equals(endringsdato, that.endringsdato)
                && Objects.equals(nyDagsats, that.nyDagsats)
                && Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, endringsdato, nyDagsats, arbeidsgiverNavn);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private Naturalytelse kladd;

        private Builder() {
            this.kladd = new Naturalytelse();
        }

        public Builder medStatus(NaturalytelseStatus status) {
            this.kladd.status = status;
            return this;
        }

        public Builder medEndringsdato(LocalDate endringsdato, Språkkode språkkode) {
            this.kladd.endringsdato = formaterDato(endringsdato, språkkode);
            this.kladd.endringsdatoDate = endringsdato;

            return this;
        }

        public Builder medNyDagsats(long nyDagsats) {
            this.kladd.nyDagsats = nyDagsats;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Naturalytelse build() {
            return this.kladd;
        }
    }
}