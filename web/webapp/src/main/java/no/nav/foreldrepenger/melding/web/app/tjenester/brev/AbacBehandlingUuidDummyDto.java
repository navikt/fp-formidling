package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import no.nav.foreldrepenger.kontrakter.formidling.v1.BehandlingUuidDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class AbacBehandlingUuidDummyDto extends BehandlingUuidDto implements AbacDto {
    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
