package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.fagsak;

import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;

public record FagsakDto(String saksnummer, FagsakYtelseType fagsakYtelseType, RelasjonsRolleType relasjonsRolleType, String aktørId, Integer dekningsgrad) {
}
