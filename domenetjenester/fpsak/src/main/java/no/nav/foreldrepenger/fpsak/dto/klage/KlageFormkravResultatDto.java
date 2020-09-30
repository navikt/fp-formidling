package no.nav.foreldrepenger.fpsak.dto.klage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class KlageFormkravResultatDto {

    @JsonProperty("paklagdBehandlingType")
    private KodeDto paklagdBehandlingType;
    @JsonProperty("avvistArsaker")
    private List<KodeDto> avvistArsaker;


    public KlageFormkravResultatDto() {
    }

    public List<KodeDto> getAvvistArsaker() {
        return avvistArsaker;
    }

    public void setAvvistArsaker(List<KodeDto> avvistArsaker) {
        this.avvistArsaker = avvistArsaker;
    }

    public KodeDto getPaklagdBehandlingType() {
        return paklagdBehandlingType;
    }

    public void setPaklagdBehandlingType(KodeDto paklagdBehandlingType) {
        this.paklagdBehandlingType = paklagdBehandlingType;
    }


}
