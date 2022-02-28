package no.nav.foreldrepenger.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsakDto {
    private BehandlingÅrsakType behandlingArsakType;
    private Boolean manueltOpprettet;

    public BehandlingÅrsakType getBehandlingArsakType() {
        return behandlingArsakType;
    }

    public void setBehandlingArsakType(BehandlingÅrsakType behandlingArsakType) {
        this.behandlingArsakType = behandlingArsakType;
    }

    public Boolean getManueltOpprettet() {
        return manueltOpprettet;
    }

    public void setManueltOpprettet(Boolean manueltOpprettet) {
        this.manueltOpprettet = manueltOpprettet;
    }

    @Override
    public String toString() {
        return "BehandlingÅrsakDto{" +
                "behandlingArsakType=" + behandlingArsakType +
                ", manueltOpprettet=" + manueltOpprettet +
                '}';
    }
}
