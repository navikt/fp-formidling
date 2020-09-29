package no.nav.foreldrepenger.melding.integrasjoner.opprettJournalpost;

import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface JournalpostFeil extends DeklarerteFeil {
    JournalpostFeil FACTORY = FeilFactory.create(JournalpostFeil.class);

    @TekniskFeil(feilkode = "FPFORMIDLING-156530", feilmelding = "Feil ved oppretting av URI for opprettelse av ny journalpost til fagsak %s.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeOppretteUriForNyJournalpost(String fagsak, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-156531", feilmelding = "Feil ved oppretting av URI for tilknytning av vedlegg til %s: %s.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeOppretteUriForTilknytningAvVedlegg(JournalpostId journalpost, String request, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-156532", feilmelding = "Feil ved oppretting av URI for ferdigstilling av %s: %s.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeOppretteUriForFerdigstillingAvJournalpost(JournalpostId journalpost, String request, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-156533", feilmelding = "Journalføring av brev for behandling %s med mal %s feilet.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeOppretteJournalpost(String behandlingUuid, String dokumentmalType, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-156534", feilmelding = "Klarte ikke knytte %s til %s med dokumentId %s.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeKnytteVedleggTilJournalpost(JournalpostId fraJournalpost, JournalpostId tilJournalpost, String dokumentId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-156535", feilmelding = "Klarte ikke ferdigstille %s.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeFerdigstilleJournalpost(JournalpostId journalpost, Exception cause);
}
