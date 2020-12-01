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
        Optional<PersonIdent> funnetFnr = aktørTjeneste.hentPersonIdentForAktørId(aktørId);
        var pinfo = funnetFnr.map(fnr -> tpsAdapter.hentKjerneinformasjon(fnr, aktørId));
        funnetFnr.ifPresent(pi -> aktørTjeneste.hentPersoninfo(aktørId,pi, pinfo.orElse(null)));
        return pinfo;
    }

    public Optional<Adresseinfo> hentAdresseinformasjon(AktørId aktørId) {
        Optional<PersonIdent> funnetFnr = aktørTjeneste.hentPersonIdentForAktørId(aktørId);
        var adresse = funnetFnr.map(pi -> tpsAdapter.hentAdresseinformasjon(pi));
        funnetFnr.ifPresent(pi -> adresse.ifPresent(adr -> aktørTjeneste.hentAdresseinformasjon(aktørId, pi, adr)));
        return adresse;
    }

}
