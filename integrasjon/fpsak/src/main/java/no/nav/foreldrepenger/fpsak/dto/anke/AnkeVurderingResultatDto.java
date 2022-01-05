package no.nav.foreldrepenger.fpsak.dto.anke;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class AnkeVurderingResultatDto {

    // Er skrelt ned til minimum. Se fpsak for fullt innholt. Ta inn mer ved behov.
    @JsonProperty("ankeVurdering")
    private KodeDto ankeVurdering;
    @JsonProperty("fritekstTilBrev")
    private String fritekstTilBrev;
    @JsonProperty("ankeVurderingOmgjoer")
    private KodeDto ankeVurderingOmgjoer;
    @JsonProperty("påAnketKlageBehandlingUuid")
    private UUID påAnketKlageBehandlingUuid;


    public AnkeVurderingResultatDto() {
    }

    public KodeDto getAnkeVurdering() {
        return ankeVurdering;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public KodeDto getAnkeVurderingOmgjoer() {
        return ankeVurderingOmgjoer;
    }

    public UUID getPåAnketKlageBehandlingUuid() {
        return påAnketKlageBehandlingUuid;
    }

}
