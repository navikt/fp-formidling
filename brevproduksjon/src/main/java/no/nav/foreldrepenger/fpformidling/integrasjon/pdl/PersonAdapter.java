package no.nav.foreldrepenger.fpformidling.integrasjon.pdl;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.vedtak.felles.integrasjon.pdl.PdlException;

@ApplicationScoped
public class PersonAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonAdapter.class);

    private PersondataTjeneste persondataTjeneste;

    public PersonAdapter() {
        // for CDI proxy
    }

    @Inject
    public PersonAdapter(PersondataTjeneste persondataTjeneste) {
        this.persondataTjeneste = persondataTjeneste;
    }

    public Optional<Personinfo> hentBrukerForAktør(AktørId aktørId) {
        try {
            var funnetFnr = persondataTjeneste.hentPersonIdentForAktørId(aktørId);
            return funnetFnr.map(pi -> persondataTjeneste.hentPersoninfo(aktørId,pi));
        } catch (PdlException pdlException) {
            LOGGER.error("Fikk feil ved kall til PDL. Detaljer: type={}, cause={}, policy={}", pdlException.getDetails().type(), pdlException.getDetails().cause(), pdlException.getDetails().policy());
            throw pdlException;
        }
    }
}
