package no.nav.foreldrepenger.tps;

import static no.nav.vedtak.feil.LogLevel.ERROR;
import static no.nav.vedtak.feil.LogLevel.WARN;

import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.ManglerTilgangFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface TpsFeilmeldinger extends DeklarerteFeil {

    TpsFeilmeldinger FACTORY = FeilFactory.create(TpsFeilmeldinger.class);

    @ManglerTilgangFeil(feilkode = "FPFORMIDLING-432142", feilmelding = "TPS ikke tilgjengelig (sikkerhetsbegrensning)", logLevel = ERROR)
    Feil tpsUtilgjengeligSikkerhetsbegrensning(HentPersonSikkerhetsbegrensning cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-715013", feilmelding = "Fant ikke person i TPS", logLevel = WARN)
    Feil fantIkkePerson(HentPersonPersonIkkeFunnet cause);
}
