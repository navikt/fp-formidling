package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageAvvistÅrsak;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KlageFormkravResultatDto {

    private BehandlingType paklagdBehandlingType;
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
