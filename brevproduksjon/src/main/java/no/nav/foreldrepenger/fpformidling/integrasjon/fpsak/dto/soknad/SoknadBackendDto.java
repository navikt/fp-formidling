package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.soknad;

import java.time.LocalDate;

public record SoknadBackendDto(LocalDate mottattDato, boolean oppgittAleneomsorg) {
}
