package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsakDto {
    private BehandlingÅrsakType behandlingArsakType;

    public BehandlingÅrsakType getBehandlingArsakType() {
        return behandlingArsakType;
    }

    public void setBehandlingArsakType(BehandlingÅrsakType behandlingArsakType) {
        this.behandlingArsakType = behandlingArsakType;
    }

    @Override
    public String toString() {
        return "BehandlingÅrsakDto{" + "behandlingArsakType=" + behandlingArsakType + '}';
    }
}
