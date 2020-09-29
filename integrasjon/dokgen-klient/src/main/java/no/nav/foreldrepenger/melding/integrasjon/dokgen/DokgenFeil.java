package no.nav.foreldrepenger.melding.integrasjon.dokgen;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface DokgenFeil extends DeklarerteFeil {
    DokgenFeil FACTORY = FeilFactory.create(DokgenFeil.class);

    @TekniskFeil(feilkode = "FPFORMIDLING-946543", feilmelding = "Fikk tomt svar ved kall til dokgen for mal %s og språkkode %s.", logLevel = LogLevel.ERROR)
    Feil tomtSvarFraDokgen(String maltype, String språkKode);

    @TekniskFeil(feilkode = "FPFORMIDLING-946544", feilmelding = "Fikk feil ved kall til dokgen for mal %s og språkkode %s.", logLevel = LogLevel.ERROR)
    Feil feilVedKallTilDokgen(String maltype, String språkKode, Exception cause);
}
