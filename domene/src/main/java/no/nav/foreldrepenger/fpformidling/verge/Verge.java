package no.nav.foreldrepenger.fpformidling.verge;

import java.time.LocalDate;

public record Verge(String aktoerId, String organisasjonsnummer, String navn, LocalDate gyldigFom, LocalDate gyldigTom) {
}
