package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FritekstbrevDokumentdata extends Dokumentdata {
    private String overskrift;
    private FritekstDto brødtekst;

    public String getOverskrift() {
        return overskrift;
    }

    public FritekstDto getBrødtekst() {
        return brødtekst;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (FritekstbrevDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(overskrift, that.overskrift)
                && Objects.equals(brødtekst, that.brødtekst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, overskrift, brødtekst);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private FritekstbrevDokumentdata kladd;

        private Builder() {
            this.kladd = new FritekstbrevDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medOverskrift(String overskrift) {
            this.kladd.overskrift = overskrift;
            return this;
        }

        public Builder medBrødtekst(FritekstDto brødtekst) {
            this.kladd.brødtekst = brødtekst;
            return this;
        }

        public FritekstbrevDokumentdata build() {
            return this.kladd;
        }
    }
}
