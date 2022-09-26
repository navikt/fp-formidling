package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.klage.KlageAvvistÅrsak;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class KlageFormkravResultatDto {

    @JsonProperty("paklagdBehandlingType")
    private BehandlingType paklagdBehandlingType;
    @JsonProperty("avvistArsaker")
    private List<KlageAvvistÅrsak> avvistArsaker;

    public List<KlageAvvistÅrsak> getAvvistArsaker() {
        return avvistArsaker != null ? avvistArsaker : List.of();
    }

    public void setAvvistArsaker(List<KlageAvvistÅrsak> avvistArsaker) {
        this.avvistArsaker = avvistArsaker;
    }

    public BehandlingType getPaklagdBehandlingType() {
        return paklagdBehandlingType;
    }

    public void setPaklagdBehandlingType(BehandlingType paklagdBehandlingType) {
        this.paklagdBehandlingType = paklagdBehandlingType;
    }


}
