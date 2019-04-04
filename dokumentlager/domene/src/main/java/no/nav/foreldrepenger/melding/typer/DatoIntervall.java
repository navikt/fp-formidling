package no.nav.foreldrepenger.melding.typer;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.time.LocalDate;

public class DatoIntervall {
    private LocalDate fomDato;
    private LocalDate tomDato;

    public LocalDate getFomDato() {
        return fomDato;
    }

    public LocalDate getTomDato() {
        return tomDato;
    }


    public DatoIntervall(LocalDate fomDato, LocalDate tomDato) {
        if (fomDato == null) {
            throw new IllegalArgumentException("Fra og med dato må være satt.");
        }
        if (tomDato == null) {
            throw new IllegalArgumentException("Til og med dato må være satt.");
        }
        if (tomDato.isBefore(fomDato)) {
            throw new IllegalArgumentException("Til og med dato før fra og med dato.");
        }
        this.fomDato = fomDato;
        this.tomDato = tomDato;
    }

    public static DatoIntervall fraOgMedTilOgMed(LocalDate fomDato, LocalDate tomDato) {
        return new DatoIntervall(fomDato, tomDato);
    }

    public static DatoIntervall fraOgMed(LocalDate fomDato) {
        return new DatoIntervall(fomDato, TIDENES_ENDE);
    }

}
