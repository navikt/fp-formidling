package no.nav.foreldrepenger;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.pdl.AktørTjeneste;
import no.nav.foreldrepenger.tps.TpsAdapter;

@ApplicationScoped
public class PersonTjeneste {

    private TpsAdapter tpsAdapter;
    private AktørTjeneste aktørTjeneste;

    public PersonTjeneste() {
        // for CDI proxy
    }

    @Inject
    public PersonTjeneste(TpsAdapter tpsAdapter, AktørTjeneste aktørTjeneste) {
        this.tpsAdapter = tpsAdapter;
        this.aktørTjeneste = aktørTjeneste;
    }

    public Optional<Personinfo> hentBrukerForAktør(AktørId aktørId) {
        Optional<PersonIdent> funnetFnr = tpsAdapter.hentIdentForAktørId(aktørId);
        aktørTjeneste.hentPersonIdentForAktørId(aktørId, funnetFnr);
        var pinfo = funnetFnr.map(fnr -> tpsAdapter.hentKjerneinformasjon(fnr, aktørId));
        funnetFnr.ifPresent(pi -> aktørTjeneste.hentPersoninfo(aktørId,pi, pinfo.orElse(null)));
        return pinfo;
    }

    public Optional<Adresseinfo> hentAdresseinformasjon(AktørId aktørId) {
        Optional<PersonIdent> funnetFnr = tpsAdapter.hentIdentForAktørId(aktørId);
        aktørTjeneste.hentPersonIdentForAktørId(aktørId, funnetFnr);
        return funnetFnr.map(pi -> tpsAdapter.hentAdresseinformasjon(pi));
    }

}
