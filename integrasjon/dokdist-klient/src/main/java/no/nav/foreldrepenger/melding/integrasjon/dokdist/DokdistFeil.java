package no.nav.foreldrepenger.melding.integrasjon.dokdist;

import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface DokdistFeil extends DeklarerteFeil {
    DokdistFeil FACTORY = FeilFactory.create(DokdistFeil.class);

    @TekniskFeil(feilkode = "FPFORMIDLING-647352", feilmelding = "Fikk tomt svar ved kall til dokdist for %s.", logLevel = LogLevel.ERROR)
    Feil tomtSvarFraDokdist(JournalpostId journalpost);

    @TekniskFeil(feilkode = "FPFORMIDLING-647353", feilmelding = "Fikk feil ved kall til dokdist for %s.", logLevel = LogLevel.ERROR)
    Feil feilVedKallTilDokdist(JournalpostId journalpost, Exception cause);
}
