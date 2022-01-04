package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AnkeOmgjortDokumentdata extends Dokumentdata {
    private String vedtaksdato;
    private boolean gunst;

    public String getVedtaksdato() {
        return vedtaksdato;
    }

    public boolean getGunst() {
        return gunst;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (AnkeOmgjortDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(vedtaksdato, that.vedtaksdato)
                && Objects.equals(gunst, that.gunst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, vedtaksdato, gunst);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private AnkeOmgjortDokumentdata kladd;

        private Builder() {
            this.kladd = new AnkeOmgjortDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medVedtaksdato(String vedtaksdato) {
            this.kladd.vedtaksdato = vedtaksdato;
            return this;
        }

        public Builder medGunst(boolean gunst) {
            this.kladd.gunst = gunst;
            return this;
        }

        public AnkeOmgjortDokumentdata build() {
            return this.kladd;
        }
    }
}
