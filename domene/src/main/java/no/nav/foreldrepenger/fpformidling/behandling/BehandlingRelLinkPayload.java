package no.nav.foreldrepenger.fpformidling.behandling;

import java.util.UUID;

// TODO Burde denne klasse være helt generisk?
public record BehandlingRelLinkPayload(Long saksnummer, UUID behandlingUuid) {

}
