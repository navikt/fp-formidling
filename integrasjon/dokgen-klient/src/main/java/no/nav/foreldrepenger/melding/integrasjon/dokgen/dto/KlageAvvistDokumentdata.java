package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageAvvistDokumentdata extends Dokumentdata {
    private boolean gjelderTilbakekreving;
    private String lovhjemler;
    private int klagefristUker;
    private int antallGrunner;
    private Set<String> avvistGrunner;

    public boolean getGjelderTilbakekreving() {
        return gjelderTilbakekreving;
    }

    public String getLovhjemler() {
        return lovhjemler;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public int getAntallGrunner() {
        return antallGrunner;
    }

    public Set<String> getAvvistGrunner() {
        return avvistGrunner;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (KlageAvvistDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(gjelderTilbakekreving, that.gjelderTilbakekreving)
                && Objects.equals(lovhjemler, that.lovhjemler)
                && Objects.equals(klagefristUker, that.klagefristUker)
                && Objects.equals(antallGrunner, that.antallGrunner)
                && Objects.equals(avvistGrunner, that.avvistGrunner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, gjelderTilbakekreving, lovhjemler, klagefristUker, antallGrunner, avvistGrunner);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private KlageAvvistDokumentdata kladd;

        private Builder() {
            this.kladd = new KlageAvvistDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medGjelderTilbakekreving(boolean gjelderTilbakekreving) {
            this.kladd.gjelderTilbakekreving = gjelderTilbakekreving;
            return this;
        }

        public Builder medLovhjemler(String lovhjemler) {
            this.kladd.lovhjemler = lovhjemler;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medAntallGrunner(int antallGrunner) {
            this.kladd.antallGrunner = antallGrunner;
            return this;
        }

        public Builder medAvvistGrunner(Set<String> avvistGrunner) {
            this.kladd.avvistGrunner = avvistGrunner;
            return this;
        }

        public KlageAvvistDokumentdata build() {
            return this.kladd;
        }
    }
}
