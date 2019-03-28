package no.nav.foreldrepenger.melding.behandling;

import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;

public class Innsyn {
    private String innsynResultatType; //Kodeliste.InnsynResultatType

    public Innsyn(InnsynsbehandlingDto dto) {
        this.innsynResultatType = dto.getInnsynResultatType().kode;
    }

    public String getInnsynResultatType() {
        return innsynResultatType;
    }
}
