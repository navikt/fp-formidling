package no.nav.foreldrepenger.fpsak.dto.anke;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.anke.AnkeVurderingOmgjør;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class AnkeVurderingResultatDto {

    // Er skrelt ned til minimum. Se fpsak for fullt innholt. Ta inn mer ved behov.

    @JsonProperty("fritekstTilBrev")
    private String fritekstTilBrev;
    @JsonProperty("ankeVurderingOmgjoer")
    private AnkeVurderingOmgjør ankeVurderingOmgjoer;
    @JsonProperty("paAnketBehandlingUuid")
    private UUID paAnketBehandlingUuid;


    public AnkeVurderingResultatDto() {
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public AnkeVurderingOmgjør getAnkeVurderingOmgjoer() {
        return ankeVurderingOmgjoer;
    }

    public UUID getPaAnketBehandlingUuid() {
        return paAnketBehandlingUuid;
    }

}
