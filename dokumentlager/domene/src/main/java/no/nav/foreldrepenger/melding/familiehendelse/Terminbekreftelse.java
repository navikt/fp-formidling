package no.nav.foreldrepenger.melding.familiehendelse;

import java.time.LocalDate;

public class Terminbekreftelse {
    private LocalDate termindato;
    private LocalDate utstedtdato;

    public LocalDate getTermindato() {
        return termindato;
    }

    public LocalDate getUtstedtdato() {
        return utstedtdato;
    }
}
