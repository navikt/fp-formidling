package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IngenEndringDokumentdata extends Dokumentdata {

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (IngenEndringDokumentdata) object;
        return Objects.equals(felles, that.felles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private IngenEndringDokumentdata kladd;

        private Builder() {
            this.kladd = new IngenEndringDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public IngenEndringDokumentdata build() {
            return this.kladd;
        }
    }
}
