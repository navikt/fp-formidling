package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.domene.klage.KlageVurdering;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultatDto {

    @JsonProperty("klageVurdering")
    private KlageVurdering klageVurdering;
    @JsonProperty("fritekstTilBrev")
    private String fritekstTilBrev;

    public KlageVurdering getKlageVurdering() {
        return klageVurdering;
    }

    public void setKlageVurdering(KlageVurdering klageVurdering) {
        this.klageVurdering = klageVurdering;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }


}
