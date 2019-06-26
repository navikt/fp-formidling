package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import no.nav.foreldrepenger.kontrakter.formidling.v1.TekstFraSaksbehandlerDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class AbacTekstFraSaksbehandlerDtoDummy extends TekstFraSaksbehandlerDto implements AbacDto {
    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
