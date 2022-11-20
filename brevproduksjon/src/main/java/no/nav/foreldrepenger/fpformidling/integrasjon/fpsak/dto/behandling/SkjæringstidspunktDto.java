package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling;

import java.time.LocalDate;

public record SkjæringstidspunktDto(LocalDate dato, boolean kreverSammenhengendeUttak, boolean utenMinsterett) {
}
