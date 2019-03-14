package no.nav.foreldrepenger.tps;

import java.util.Optional;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;

public interface TpsTjeneste {
    Optional<Personinfo> hentBrukerForAktør(AktørId aktørId);

    Adresseinfo hentAdresseinformasjon(PersonIdent personIdent);

    Optional<PersonIdent> hentFnr(AktørId aktørId);

    Optional<Personinfo> hentBrukerForFnr(PersonIdent fnr);

    Optional<AktørId> hentAktørForFnr(PersonIdent fnr);
}
