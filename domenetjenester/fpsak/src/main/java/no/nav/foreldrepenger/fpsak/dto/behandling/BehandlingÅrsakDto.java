package no.nav.foreldrepenger.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsakDto {
    private KodeDto behandlingArsakType;
    private Boolean manueltOpprettet;

    public KodeDto getBehandlingArsakType() {
        return behandlingArsakType;
    }

    public void setBehandlingArsakType(KodeDto behandlingArsakType) {
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
