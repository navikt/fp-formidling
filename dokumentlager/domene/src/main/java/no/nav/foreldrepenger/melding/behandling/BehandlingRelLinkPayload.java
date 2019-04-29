package no.nav.foreldrepenger.melding.behandling;

// TODO Burde denne klasse være helt generisk?
public class BehandlingRelLinkPayload {

    private Long saksnummer;
    private Long behandlingId;
    private String behandlingUuid;

    public BehandlingRelLinkPayload(Long saksnummer, Long behandlingId, String behandlingUuid) {
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
        this.behandlingUuid = behandlingUuid;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public String getBehandlingUuid() {
        return behandlingUuid;
    }

    @Override
    public String toString() {
        return "BehandlingRelLinkPayload{" +
                "saksnummer=" + saksnummer +
                ", behandlingId=" + behandlingId +
                ", behandlingUuid='" + behandlingUuid + '\'' +
                '}';
    }
}
