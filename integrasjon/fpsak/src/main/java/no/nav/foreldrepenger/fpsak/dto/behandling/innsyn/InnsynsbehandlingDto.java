package no.nav.foreldrepenger.fpsak.dto.behandling.innsyn;


import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class InnsynsbehandlingDto {

    private KodeDto innsynResultatType;
    private List<InnsynDokumentDto> dokumenter = new ArrayList<>();

    public KodeDto getInnsynResultatType() {
        return innsynResultatType;
    }

    public void setInnsynResultatType(KodeDto innsynResultatType) {
        this.innsynResultatType = innsynResultatType;
    }

    public void setDokumenter(List<InnsynDokumentDto> dokumenter) {
        this.dokumenter = dokumenter;
    }

    public List<InnsynDokumentDto> getDokumenter() {
        return dokumenter;
    }
}
