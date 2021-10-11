package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InnsynDokumentdata extends Dokumentdata {
    private String innsynResultat;
    private int klagefrist;

    public String getInnsynResultat() { return innsynResultat; }

    public int getKlagefrist() { return klagefrist; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (InnsynDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(innsynResultat, that.innsynResultat)
                && Objects.equals(klagefrist, that.klagefrist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, innsynResultat, klagefrist);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private InnsynDokumentdata kladd;

        private Builder() {
            this.kladd = new InnsynDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medInnsynResultat(String innsynResultat) {
            this.kladd.innsynResultat = innsynResultat;
            return this;
        }

        public Builder medKlagefrist(int klagefrist) {
            this.kladd.klagefrist = klagefrist;
            return this;
        }

        public InnsynDokumentdata build() {
            return this.kladd;
        }
    }
}
