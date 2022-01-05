package no.nav.foreldrepenger.fpformidling.web.app.pdp.dto;

import java.util.Set;

import javax.validation.Valid;

import no.nav.foreldrepenger.fpformidling.typer.AktørId;

public class PipDto {

    @Valid
    private Set<AktørId> aktørIder;
    private String fagsakStatus;
    private String behandlingStatus;


    public Set<AktørId> getAktørIder() {
        return aktørIder;
    }

    public void setAktørIder(Set<AktørId> aktørIder) {
        this.aktørIder = aktørIder;
    }

    public String getFagsakStatus() {
        return fagsakStatus;
    }

    public void setFagsakStatus(String fagsakStatus) {
        this.fagsakStatus = fagsakStatus;
    }

    public String getBehandlingStatus() {
        return behandlingStatus;
    }

    public void setBehandlingStatus(String behandlingStatus) {
        this.behandlingStatus = behandlingStatus;
    }
}
