package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.formidling.v1.BehandlingUuidDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

// TODO(JEJ) TFP-1404: Enten ta BehandlingUuidDto ut av kontrakter hvis den bare brukes i fp-formidling, eller utvide den med tingene i denne comitten
public class AbacBehandlingUuidDto extends BehandlingUuidDto implements AbacDto {

    public static final String NAME = "uuid";
    public static final String DESC = "behandlingUUID";

    public AbacBehandlingUuidDto() {
    }

    public AbacBehandlingUuidDto(String behandlingUuid) {
        super(UUID.fromString(behandlingUuid));
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, getBehandlingUuid().toString());
    }
}
