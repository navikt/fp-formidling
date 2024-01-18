package no.nav.foreldrepenger.fpformidling.domene.verge;

import java.time.LocalDate;

public record Verge(String aktoerId, String organisasjonsnummer, String navn, LocalDate gyldigFom, LocalDate gyldigTom) {
}
