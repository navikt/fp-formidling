package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageOmgjortDokumentdata extends Dokumentdata {
    private boolean gjelderTilbakekreving;
    private int klagefristUker;

    public boolean getGjelderTilbakekreving() {
        return gjelderTilbakekreving;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (KlageOmgjortDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(gjelderTilbakekreving, that.gjelderTilbakekreving)
                && Objects.equals(klagefristUker, that.klagefristUker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, gjelderTilbakekreving, klagefristUker);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private KlageOmgjortDokumentdata kladd;

        private Builder() {
            this.kladd = new KlageOmgjortDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medGjelderTilbakekreving(boolean gjelderTilbakekreving) {
            this.kladd.gjelderTilbakekreving = gjelderTilbakekreving;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public KlageOmgjortDokumentdata build() {
            return this.kladd;
        }
    }
}
