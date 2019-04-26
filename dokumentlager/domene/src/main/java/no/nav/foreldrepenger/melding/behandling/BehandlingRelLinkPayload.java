package no.nav.foreldrepenger.melding.behandling;

public class BehandlingRelLinkPayload {

    private Long saksnummer;
    private Long behandlingId;

    public BehandlingRelLinkPayload(Long saksnummer, Long behandlingId) {
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    @Override
    public String toString() {
        return "BehandlingRelLinkPayloadDto{" +
                "saksnummer=" + saksnummer +
                ", behandlingId=" + behandlingId +
                '}';
    }
}
