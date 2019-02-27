package no.nav.foreldrepenger.fpsak.dto.behandling;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BehandlingIdDto {
    @NotNull
    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long behandlingId;

    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long saksnummer;

    public BehandlingIdDto() {//For Jackson
    }

    public BehandlingIdDto(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(Long saksnummer) {
        this.saksnummer = saksnummer;
    }
}
