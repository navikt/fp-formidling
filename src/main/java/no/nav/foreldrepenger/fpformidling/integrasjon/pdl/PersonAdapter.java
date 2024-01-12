package no.nav.foreldrepenger.fpformidling.integrasjon.pdl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.typer.AktørId;
import no.nav.vedtak.felles.integrasjon.person.PdlException;

@ApplicationScoped
public class PersonAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(PersonAdapter.class);

    private PersondataTjeneste persondataTjeneste;

    public PersonAdapter() {
        // for CDI proxy
    }

    @Inject
    public PersonAdapter(PersondataTjeneste persondataTjeneste) {
        this.persondataTjeneste = persondataTjeneste;
    }

    public Optional<Personinfo> hentBrukerForAktør(FagsakYtelseType ytelseType, AktørId aktørId) {
        try {
            var funnetFnr = persondataTjeneste.hentPersonIdentForAktørId(aktørId);
            return funnetFnr.map(pi -> persondataTjeneste.hentPersoninfo(ytelseType, aktørId, pi));
        } catch (PdlException pdlException) {
            LOG.error("Fikk feil ved kall til PDL. Detaljer: type={}, cause={}, policy={}", pdlException.getDetails().type(),
                pdlException.getDetails().cause(), pdlException.getDetails().policy());
            throw pdlException;
        }
    }
}
