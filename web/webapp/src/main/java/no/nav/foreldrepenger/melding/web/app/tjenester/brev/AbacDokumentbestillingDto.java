package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class AbacDokumentbestillingDto extends DokumentbestillingDto implements AbacDto {

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilBehandlingsUUID(getBehandlingUuid());
    }
}
