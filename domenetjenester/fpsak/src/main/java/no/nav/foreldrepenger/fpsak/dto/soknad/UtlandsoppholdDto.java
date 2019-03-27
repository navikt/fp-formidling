package no.nav.foreldrepenger.fpsak.dto.soknad;

import java.time.LocalDate;

public class UtlandsoppholdDto {
    private String landNavn;
    private LocalDate fom;
    private LocalDate tom;

    public UtlandsoppholdDto() {
        // trengs for deserialisering av JSON
    }

    private UtlandsoppholdDto(String landNavn, LocalDate fom, LocalDate tom) {
        this.landNavn = landNavn;
        this.fom = fom;
        this.tom = tom;
    }

    public String getLandNavn() {
        return landNavn;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }
}
