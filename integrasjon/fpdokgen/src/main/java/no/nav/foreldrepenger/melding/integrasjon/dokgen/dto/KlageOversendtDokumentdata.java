package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageOversendtDokumentdata extends Dokumentdata {
    private boolean gjelderTilbakekreving;
    private String mottattDato;

    public boolean getGjelderTilbakekreving() {
        return gjelderTilbakekreving;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (KlageOversendtDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(gjelderTilbakekreving, that.gjelderTilbakekreving)
                && Objects.equals(mottattDato, that.mottattDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, gjelderTilbakekreving, mottattDato);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private KlageOversendtDokumentdata kladd;

        private Builder() {
            this.kladd = new KlageOversendtDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medGjelderTilbakekreving(boolean gjelderTilbakekreving) {
            this.kladd.gjelderTilbakekreving = gjelderTilbakekreving;
            return this;
        }

        public Builder medMottattDato(String mottattDato) {
            this.kladd.mottattDato = mottattDato;
            return this;
        }

        public KlageOversendtDokumentdata build() {
            return this.kladd;
        }
    }
}