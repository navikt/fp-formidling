package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.innsyn;


import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.behandling.innsyn.InnsynResultatType;

public class InnsynsbehandlingDto {

    private InnsynResultatType innsynResultatType;
    private List<InnsynDokumentDto> dokumenter = new ArrayList<>();

    public InnsynResultatType getInnsynResultatType() {
        return innsynResultatType;
    }

    public void setInnsynResultatType(InnsynResultatType innsynResultatType) {
        this.innsynResultatType = innsynResultatType;
    }

    public void setDokumenter(List<InnsynDokumentDto> dokumenter) {
        this.dokumenter = dokumenter;
    }

    public List<InnsynDokumentDto> getDokumenter() {
        return dokumenter;
    }
}
