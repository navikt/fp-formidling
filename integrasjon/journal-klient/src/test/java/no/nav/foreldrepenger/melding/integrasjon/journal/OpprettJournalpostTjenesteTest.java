package no.nav.foreldrepenger.melding.integrasjon.journal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.DokumentOpprettResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

public class OpprettJournalpostTjenesteTest {
    private static final String MOTTAKER_ID = "1234";
    private static final String JOURNALPOST_ID = "234567";
    private static final String MOTTAKER_NAVN = "SOEKERS_NAVN";
    private static final String FRITEKST = "FRITEKST";
    private static final byte[] GEN_BREV = "Dette er et lite testbrev med lite innhold".getBytes();
    private static final String FNR = "99999999899";

    private OpprettJournalpostTjeneste opprettJournalpost; // objektet vi tester

    // Mocks
    private JournalpostRestKlient journalpostRestKlient = mock(JournalpostRestKlient.class);

    @Before
    public void setup()  {
        opprettJournalpost = new OpprettJournalpostTjeneste(journalpostRestKlient);

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, true, List.of(new DokumentOpprettResponse("1111")));
        when(journalpostRestKlient.opprettJournalpost(any(OpprettJournalpostRequest.class), eq(true))).thenReturn(response);
    }

    @Test
    public void skal_journalføre_generert_brev() {
        DokumentFelles dokumentFelles = getDokumentFelles();
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder()
                .medTittel("Innvilget Engangsstønad")
                .build();

        Saksnummer saksnummer = new Saksnummer("123456789");

        OpprettJournalpostResponse responseMocked = opprettJournalpost.journalførUtsendelse(GEN_BREV, DokumentMalType.UDEFINERT, dokumentFelles, dokumentHendelse, saksnummer, true);

        assertThat(responseMocked.erFerdigstilt()).isTrue();
        assertThat(!responseMocked.getJournalpostId().isEmpty());
    }

    private DokumentFelles getDokumentFelles() {
        DokumentFelles dokumentFelles = Mockito.mock(DokumentFelles.class);
        when(dokumentFelles.getMottakerNavn()).thenReturn(MOTTAKER_NAVN);
        when(dokumentFelles.getSakspartId()).thenReturn(FNR);
        when(dokumentFelles.getMottakerId()).thenReturn(MOTTAKER_ID);
        return dokumentFelles;
    }

    private DokumentHendelse.Builder lagStandardHendelseBuilder() {
        return DokumentHendelse.builder()
                .medBestillingUuid(UUID.randomUUID())
                .medBehandlingUuid(UUID.randomUUID())
                .medFritekst(FRITEKST)
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD);
    }
}