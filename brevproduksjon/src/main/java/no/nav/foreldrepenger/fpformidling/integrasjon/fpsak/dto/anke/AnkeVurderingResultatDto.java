package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.anke;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.anke.AnkeVurdering;
import no.nav.foreldrepenger.fpformidling.anke.AnkeVurderingOmgjør;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class AnkeVurderingResultatDto {

    // Er skrelt ned til minimum. Se fpsak for fullt innholt. Ta inn mer ved behov.
    @JsonProperty("ankeVurdering")
    private AnkeVurdering ankeVurdering;
    @JsonProperty("fritekstTilBrev")
    private String fritekstTilBrev;
    @JsonProperty("ankeVurderingOmgjoer")
    private AnkeVurderingOmgjør ankeVurderingOmgjoer;
    @JsonProperty("påAnketKlageBehandlingUuid")
    private UUID påAnketKlageBehandlingUuid;

    public AnkeVurdering getAnkeVurdering() {
        return ankeVurdering;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public AnkeVurderingOmgjør getAnkeVurderingOmgjoer() {
        return ankeVurderingOmgjoer;
    }

    public UUID getPåAnketKlageBehandlingUuid() {
        return påAnketKlageBehandlingUuid;
    }

}
