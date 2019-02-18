package no.nav.foreldrepenger.meldinger.kafkatjenester.jsondokumenthendelse;

public class JsonDokumentHendelse {
    private Long behandlingId;
    private String hendelse;

    public Long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public String getHendelse() {
        return hendelse;
    }

    public void setHendelse(String hendelse) {
        this.hendelse = hendelse;
    }
}
