package no.nav.foreldrepenger;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.pdl.PersondataTjeneste;

@ApplicationScoped
public class PersonAdapter {

    private PersondataTjeneste aktørTjeneste;

    public PersonAdapter() {
        // for CDI proxy
    }

    @Inject
    public PersonAdapter(PersondataTjeneste aktørTjeneste) {
        this.aktørTjeneste = aktørTjeneste;
    }

    public Optional<Personinfo> hentBrukerForAktør(AktørId aktørId) {
        Optional<PersonIdent> funnetFnr = aktørTjeneste.hentPersonIdentForAktørId(aktørId);
        return funnetFnr.map(pi -> aktørTjeneste.hentPersoninfo(aktørId,pi));
    }

    public Optional<Adresseinfo> hentAdresseinformasjon(AktørId aktørId) {
        Optional<PersonIdent> funnetFnr = aktørTjeneste.hentPersonIdentForAktørId(aktørId);
        return funnetFnr.map(pi -> aktørTjeneste.hentAdresseinformasjon(aktørId, pi));
    }

}
