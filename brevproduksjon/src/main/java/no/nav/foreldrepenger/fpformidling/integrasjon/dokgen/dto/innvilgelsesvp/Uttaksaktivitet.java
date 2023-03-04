package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Uttaksaktivitet {
    private String aktivitetsbeskrivelse;
    private List<Uttaksperiode> uttaksperioder;

    public String getAktivitetsbeskrivelse() {
        return aktivitetsbeskrivelse;
    }

    public List<Uttaksperiode> getUttaksperioder() {
        return uttaksperioder;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (Uttaksaktivitet) object;
        return Objects.equals(aktivitetsbeskrivelse, that.aktivitetsbeskrivelse) && Objects.equals(uttaksperioder, that.uttaksperioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetsbeskrivelse, uttaksperioder);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private Uttaksaktivitet kladd;

        private Builder() {
            this.kladd = new Uttaksaktivitet();
        }

        public Builder medAktivitetsbeskrivelse(String aktivitetsbeskrivelse) {
            this.kladd.aktivitetsbeskrivelse = aktivitetsbeskrivelse;
            return this;
        }

        public Builder medUttaksperioder(List<Uttaksperiode> uttaksperioder) {
            this.kladd.uttaksperioder = uttaksperioder;
            return this;
        }

        public Uttaksaktivitet build() {
            return this.kladd;
        }
    }
}
