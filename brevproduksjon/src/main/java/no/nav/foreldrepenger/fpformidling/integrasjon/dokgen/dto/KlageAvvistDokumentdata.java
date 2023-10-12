package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import static no.nav.foreldrepenger.fpformidling.klage.KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageAvvistDokumentdata extends Dokumentdata {
    private boolean gjelderTilbakekreving;
    private String lovhjemler;
    private int klagefristUker;
    private Set<String> avvistGrunner;
    private boolean påklagdVedtak;

    public boolean getGjelderTilbakekreving() {
        return gjelderTilbakekreving;
    }

    public String getLovhjemler() {
        return lovhjemler;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public Set<String> getAvvistGrunner() {
        return avvistGrunner;
    }

    public boolean isPåklagdVedtak() {
        return påklagdVedtak;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (KlageAvvistDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(gjelderTilbakekreving, that.gjelderTilbakekreving) && Objects.equals(lovhjemler,
            that.lovhjemler) && Objects.equals(klagefristUker, that.klagefristUker) && Objects.equals(avvistGrunner, that.avvistGrunner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, gjelderTilbakekreving, lovhjemler, klagefristUker, avvistGrunner);
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

        public Builder medAvvistGrunner(Set<String> avvistGrunner) {
            this.kladd.avvistGrunner = avvistGrunner;
            return this;
        }

        public KlageAvvistDokumentdata build() {
            this.kladd.påklagdVedtak = this.kladd.getAvvistGrunner() != null && this.kladd.getAvvistGrunner().stream().noneMatch(g -> Objects.equals(IKKE_PAKLAGD_VEDTAK.getKode(), g));
            return this.kladd;
        }
    }
}
