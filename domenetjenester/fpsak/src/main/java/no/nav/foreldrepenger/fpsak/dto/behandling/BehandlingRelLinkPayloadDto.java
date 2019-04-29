package no.nav.foreldrepenger.fpsak.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingRelLinkPayloadDto {
    private Long saksnummer;
    private Long behandlingId;
    private String behandlingUuid;

    public Long getSaksnummer() {
        return saksnummer;
    }

    public void setSaksnummer(Long saksnummer) {
        this.saksnummer = saksnummer;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public String getBehandlingUuid() {
        return behandlingUuid;
    }

    @Override
    public String toString() {
        return "BehandlingRelLinkPayloadDto{" +
                "saksnummer=" + saksnummer +
                ", behandlingId=" + behandlingId +
                ", behandlingUuid='" + behandlingUuid + '\'' +
                '}';
    }
}
