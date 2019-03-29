package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.util.ArrayList;
import java.util.List;

public class RelaterteYtelserDto {

    private String relatertYtelseType;
    private List<TilgrensendeYtelserDto> tilgrensendeYtelserListe = new ArrayList<>();

    public String getRelatertYtelseType() {
        return relatertYtelseType;
    }

    public List<TilgrensendeYtelserDto> getTilgrensendeYtelserListe() {
        return tilgrensendeYtelserListe;
    }
}
