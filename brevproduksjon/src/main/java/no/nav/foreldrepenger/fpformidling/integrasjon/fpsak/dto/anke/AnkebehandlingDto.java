package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.anke;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class AnkebehandlingDto {

    @JsonProperty("ankeVurderingResultat")
    private AnkeVurderingResultatDto ankeVurderingResultat;


    public AnkebehandlingDto() {
        // trengs for deserialisering av JSON
    }

    public AnkeVurderingResultatDto getAnkeVurderingResultat() {
        return ankeVurderingResultat;
    }
}
