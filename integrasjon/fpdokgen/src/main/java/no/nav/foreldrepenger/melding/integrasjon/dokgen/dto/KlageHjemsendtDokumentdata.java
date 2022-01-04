package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageHjemsendtDokumentdata extends Dokumentdata {
    private boolean gjelderTilbakekreving;
    private boolean opphevet;
    private String ettersendelsesfrist;

    public boolean getGjelderTilbakekreving() {
        return gjelderTilbakekreving;
    }

    public boolean getOpphevet() {
        return opphevet;
    }

    public String getEttersendelsesfrist() {
        return ettersendelsesfrist;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (KlageHjemsendtDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(gjelderTilbakekreving, that.gjelderTilbakekreving)
                && Objects.equals(opphevet, that.opphevet)
                && Objects.equals(ettersendelsesfrist, that.ettersendelsesfrist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, gjelderTilbakekreving, opphevet, ettersendelsesfrist);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private KlageHjemsendtDokumentdata kladd;

        private Builder() {
            this.kladd = new KlageHjemsendtDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medGjelderTilbakekreving(boolean gjelderTilbakekreving) {
            this.kladd.gjelderTilbakekreving = gjelderTilbakekreving;
            return this;
        }

        public Builder medOpphevet(boolean opphevet) {
            this.kladd.opphevet = opphevet;
            return this;
        }

        public Builder medEttersendelsesfrist(String ettersendelsesfrist) {
            this.kladd.ettersendelsesfrist = ettersendelsesfrist;
            return this;
        }

        public KlageHjemsendtDokumentdata build() {
            return this.kladd;
        }
    }
}
