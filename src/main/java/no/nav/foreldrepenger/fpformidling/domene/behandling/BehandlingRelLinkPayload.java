package no.nav.foreldrepenger.fpformidling.domene.behandling;

import java.util.UUID;

public record BehandlingRelLinkPayload(Long saksnummer, UUID behandlingUuid) {

}
