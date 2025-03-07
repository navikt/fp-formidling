package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

import java.util.Objects;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FritekstbrevHtmlDokumentdata extends Dokumentdata {
    private String html;

    public String getHtml() {
        return html;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (FritekstbrevHtmlDokumentdata) object;
        return Objects.equals(felles, that.felles) && Objects.equals(html, that.html);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, html);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private FritekstbrevHtmlDokumentdata kladd;

        private Builder() {
            this.kladd = new FritekstbrevHtmlDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medHtml(String html) {
            this.kladd.html = html;
            return this;
        }

        public FritekstbrevHtmlDokumentdata build() {
            return this.kladd;
        }
    }
}
