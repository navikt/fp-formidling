package no.nav.foreldrepenger.fpsak.dto.klage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.klage.KlageVurdering;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultatDto {

    @JsonProperty("klageVurdering")
    private KodeDto klageVurdering;
    @JsonProperty("fritekstTilBrev")
    private String fritekstTilBrev;

    public KlageVurderingResultatDto() {
    }

    public KodeDto getKlageVurdering() {
        return klageVurdering;
    }

    public void setKlageVurdering(KlageVurdering klageVurdering) {
        this.klageVurdering = new KodeDto(klageVurdering.getKodeverk(), klageVurdering.getKode());
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }


}
