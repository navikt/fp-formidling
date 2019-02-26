package no.nav.foreldrepenger.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsakDto {
    private Kode behandlingArsakType;
    private Boolean manueltOpprettet;

    public Kode getBehandlingArsakType() {
        return behandlingArsakType;
    }

    public void setBehandlingArsakType(Kode behandlingArsakType) {
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
