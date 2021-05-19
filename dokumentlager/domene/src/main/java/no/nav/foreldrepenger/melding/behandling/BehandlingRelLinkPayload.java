package no.nav.foreldrepenger.melding.behandling;

import java.util.UUID;

// TODO Burde denne klasse være helt generisk?
public record BehandlingRelLinkPayload(Long saksnummer, UUID behandlingUuid) {

}
