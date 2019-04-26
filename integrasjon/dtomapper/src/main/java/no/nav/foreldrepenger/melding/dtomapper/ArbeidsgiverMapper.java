package no.nav.foreldrepenger.melding.dtomapper;

import no.nav.foreldrepenger.melding.inntektarbeidytelse.OrganisasjonsNummerValidator;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;

class ArbeidsgiverMapper {
    static Arbeidsgiver finnArbeidsgiver(String arbeidsgiverNavn, String arbeidsgiverIdentifikator) {
        Virksomhet virksomhet = null;
        AktørId aktørId = null;
        if (OrganisasjonsNummerValidator.erGyldig(arbeidsgiverIdentifikator)) {
            virksomhet = new Virksomhet(arbeidsgiverNavn, arbeidsgiverIdentifikator);
        } else {
            aktørId = new AktørId(arbeidsgiverIdentifikator);
        }
        return new Arbeidsgiver(arbeidsgiverNavn, virksomhet, aktørId);
    }
}
