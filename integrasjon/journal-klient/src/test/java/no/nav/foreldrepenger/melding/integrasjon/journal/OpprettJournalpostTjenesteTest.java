package no.nav.foreldrepenger.melding.integrasjon.journal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.DokumentOpprettRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

public class OpprettJournalpostTjenesteTest {
    private static final String SAK_ID = "456";
    private static final String AVSENDER_ID = "3000";
    private static final String BRUKER_ID = "1234";
    private static final String JOURNALPOST_ID = "234567";
    private static final String SOEKERS_NAVN = "SOEKERS_NAVN";
    private static final String FRITEKST = "FRITEKST";
    private static final LocalDateTime FORSENDELSE_MOTTATT = LocalDateTime.now();
    private static final byte[] GEN_BREV = "Dette er et lite testbrev med lite innhold".getBytes();

    private OpprettJournalpostTjeneste opprettJournalpost; // objektet vi tester

    // Mocks
    private JournalpostRestKlient journalpostRestKlient = mock(JournalpostRestKlient.class);

    @Before
    public void setup()  {
        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, true, Collections.emptyList());
        when(journalpostRestKlient.opprettJournalpost(any(OpprettJournalpostRequest.class), eq(true))).thenReturn(response);
        opprettJournalpost = new OpprettJournalpostTjeneste(journalpostRestKlient);
    }

    @Test
    public void skal_journalføre_generert_brev() {
        DokumentFelles dokumentFelles = getDokumentFelles();
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder()
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medTittel("Innvilget Engangsstønad")
                .build();

        Saksnummer saksnummer = new Saksnummer("123456789");

        DokumentOpprettRequest generertBrev = lagDokumentOpprettRequest(dokumentHendelse.getTittel(), "", "");
        OpprettJournalpostResponse response = opprettJournalpost.journalførUtsendelse(generertBrev, dokumentFelles, dokumentHendelse, saksnummer, true);
        //TODO(Anja): Fullføre testen - jeg har gjort det som skal til for at det kompilerer :-)
    }

    private DokumentOpprettRequest lagDokumentOpprettRequest(String tittel, String brevkode, String dokumentKategori) {
        return new DokumentOpprettRequest(tittel, brevkode, dokumentKategori, GEN_BREV);
    }

    private DokumentFelles getDokumentFelles() {
        DokumentFelles dokumentFelles = Mockito.mock(DokumentFelles.class);
        when(dokumentFelles.getSakspartNavn()).thenReturn(SOEKERS_NAVN);
        when(dokumentFelles.getSakspartPersonStatus()).thenReturn("ANNET");
        return dokumentFelles;
    }

    private DokumentHendelse.Builder lagStandardHendelseBuilder() {
        return DokumentHendelse.builder()
                .medBestillingUuid(UUID.randomUUID())
                .medBehandlingUuid(UUID.randomUUID())
                .medFritekst(FRITEKST)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }
}