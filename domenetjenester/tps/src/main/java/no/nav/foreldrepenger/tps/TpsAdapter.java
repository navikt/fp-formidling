package no.nav.foreldrepenger.tps;

import java.util.Optional;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;

public interface TpsAdapter {
    Optional<PersonIdent> hentIdentForAktørId(AktørId aktørId);

    Personinfo hentKjerneinformasjon(PersonIdent personIdent, AktørId aktørId);

    Adresseinfo hentAdresseinformasjon(PersonIdent personIdent);

    Optional<AktørId> hentAktørIdForPersonIdent(PersonIdent personIdent);
}
