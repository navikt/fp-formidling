package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.fagsak;

import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;

public record FagsakDto(String saksnummer,
                        RelasjonsRolleType relasjonsRolleType,
                        String aktoerId,
                        Integer dekningsgrad) {
}
