package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;

public class Verge {

    private boolean brevTilSøker;
    private boolean brevTilVerge;
    private String fnr;

    public Verge(VergeDto dto) {
        brevTilSøker = dto.getSokerErKontaktPerson();
        brevTilVerge = dto.getVergeErKontaktPerson();
        fnr = dto.getFnr();
    }

    public String getFnr() {
        return fnr;
    }

    public boolean isBrevTilSøker() {
        return brevTilSøker;
    }

    public boolean isBrevTilVerge() {
        return brevTilVerge;
    }

    public boolean brevTilBegge() {
        return brevTilVerge && brevTilSøker;
    }
}
