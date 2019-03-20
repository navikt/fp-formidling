package no.nav.foreldrepenger.fpsak.dto.klage;

import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class KlageFormkravResultatDto {
    private Long paKlagdBehandlingId;
    private String begrunnelse;
    private boolean erKlagerPart;
    private boolean erKlageKonkret;
    private boolean erKlagefirstOverholdt;
    private boolean erSignert;
    private List<KodeDto> avvistArsaker;


    public KlageFormkravResultatDto() {
    }

    public List<KodeDto> getAvvistArsaker() {
        return avvistArsaker;
    }

    public void setAvvistArsaker(List<KodeDto> avvistArsaker) {
        this.avvistArsaker = avvistArsaker;
    }

    public Long getPaKlagdBehandlingId() {
        return paKlagdBehandlingId;
    }

    public void setPaKlagdBehandlingId(Long paKlagdBehandlingId) {
        this.paKlagdBehandlingId = paKlagdBehandlingId;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public boolean isErKlagerPart() {
        return erKlagerPart;
    }

    public void setErKlagerPart(boolean erKlagerPart) {
        this.erKlagerPart = erKlagerPart;
    }

    public boolean isErKlageKonkret() {
        return erKlageKonkret;
    }

    public void setErKlageKonkret(boolean erKlageKonkret) {
        this.erKlageKonkret = erKlageKonkret;
    }

    public boolean isErKlagefirstOverholdt() {
        return erKlagefirstOverholdt;
    }

    public void setErKlagefirstOverholdt(boolean erKlagefirstOverholdt) {
        this.erKlagefirstOverholdt = erKlagefirstOverholdt;
    }

    public boolean isErSignert() {
        return erSignert;
    }

    public void setErSignert(boolean erSignert) {
        this.erSignert = erSignert;
    }
}
