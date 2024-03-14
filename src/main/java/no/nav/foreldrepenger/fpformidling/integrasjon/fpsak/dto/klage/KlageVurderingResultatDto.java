package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.domene.klage.KlageVurdering;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultatDto {

    private KlageVurdering klageVurdering;
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

    public void setFritekstTilBrev(String fritekstTilBrev) {
        this.fritekstTilBrev = fritekstTilBrev;
    }
}
