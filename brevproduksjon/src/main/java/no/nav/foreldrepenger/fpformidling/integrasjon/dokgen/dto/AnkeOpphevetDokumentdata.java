package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AnkeOpphevetDokumentdata extends Dokumentdata {
    private boolean oppheve;

    public boolean getOppheve() {
        return oppheve;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (AnkeOpphevetDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(oppheve, that.oppheve);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, oppheve);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private AnkeOpphevetDokumentdata kladd;

        private Builder() {
            this.kladd = new AnkeOpphevetDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medOppheve(boolean oppheve) {
            this.kladd.oppheve = oppheve;
            return this;
        }

        public AnkeOpphevetDokumentdata build() {
            return this.kladd;
        }
    }
}
