package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingRelLinkPayloadDto {
    private Long saksnummer;
    private UUID behandlingUuid;

    public Long getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(Long saksnummer) {
        this.saksnummer = saksnummer;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public void setBehandlingUuid(UUID behandlingUuid) {
        this.behandlingUuid = behandlingUuid;
    }

    @Override
    public String toString() {
        return "BehandlingRelLinkPayloadDto{" +
                "saksnummer=" + saksnummer +
                ", behandlingUuid='" + behandlingUuid + '\'' +
                '}';
    }
}
