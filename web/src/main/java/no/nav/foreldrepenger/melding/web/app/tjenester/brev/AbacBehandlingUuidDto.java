package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import no.nav.foreldrepenger.kontrakter.formidling.v1.BehandlingUuidDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public class AbacBehandlingUuidDto extends BehandlingUuidDto implements AbacDto {

    public AbacBehandlingUuidDto(String behandlingUuid) {
        super(behandlingUuid);
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTil(StandardAbacAttributtType.BEHANDLING_UUID, getBehandlingUuid().toString());
    }
}
