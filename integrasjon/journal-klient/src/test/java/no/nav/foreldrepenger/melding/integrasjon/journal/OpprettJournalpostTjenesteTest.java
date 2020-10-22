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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.DokumentOpprettResponse;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostRequest;
import no.nav.foreldrepenger.melding.integrasjon.journal.dto.OpprettJournalpostResponse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingTema;
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

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, "", true, List.of(new DokumentOpprettResponse("1111")));
        when(journalpostRestKlient.opprettJournalpost(any(OpprettJournalpostRequest.class), eq(true))).thenReturn(response);
    }

    @Test
    public void skal_journalføre_generert_INNVES_brev() {
        // Arrange
        ArgumentCaptor<OpprettJournalpostRequest> requestCaptor = ArgumentCaptor.forClass(OpprettJournalpostRequest.class);

        DokumentFelles dokumentFelles = getDokumentFelles();
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder()
                .medTittel("Innvilget Engangsstønad")
                .build();

        Saksnummer saksnummer = new Saksnummer("123456789");

        // Act
        OpprettJournalpostResponse responseMocked = opprettJournalpost.journalførUtsendelse(GEN_BREV, DokumentMalType.INNVILGELSE_ENGANGSSTØNAD, dokumentFelles, dokumentHendelse, saksnummer, true);

        // Assert
        Mockito.verify(journalpostRestKlient).opprettJournalpost(requestCaptor.capture(), eq(true));

        OpprettJournalpostRequest genRequest = requestCaptor.getValue();
        assertThat(genRequest.getTema()).isEqualTo("FOR");
        assertThat(genRequest.getBehandlingstema()).isEqualTo(BehandlingTema.ENGANGSSTØNAD.getOffisiellKode());
        assertThat(genRequest.getSak().getSakstype()).isEqualTo("ARKIVSAK");
        assertThat(genRequest.getSak().getArkivsaksnummer()).isEqualTo(saksnummer.getVerdi());
        assertThat(genRequest.getSak().getArkivsaksystem()).isEqualTo("GSAK");
        assertThat(genRequest.getAvsenderMottaker().getId()).isEqualTo(MOTTAKER_ID);
        assertThat(genRequest.getAvsenderMottaker().getNavn()).isEqualTo(MOTTAKER_NAVN);
        assertThat(genRequest.getJournalfoerendeEnhet()).isEqualTo("9999");
        assertThat(genRequest.getBruker().getId()).isEqualTo(FNR);
        assertThat(genRequest.getEksternReferanseId()).isEqualTo(null);
        assertThat(genRequest.getDokumenter().get(0).getBrevkode()).isEqualTo(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD.getKode());
        byte[] brev = genRequest.getDokumenter().get(0).getDokumentvarianter().get(0).getFysiskDokument();
        assertThat(brev).contains(GEN_BREV);
        assertThat(genRequest.getDokumenter().get(0).getDokumentvarianter().get(0).getVariantformat()).isEqualTo("ARKIV");
        assertThat(genRequest.getDokumenter().get(0).getDokumentvarianter().get(0).getFiltype()).isEqualTo("PDFA");
        assertThat(genRequest.getKanal()).isEqualTo(null);
        assertThat(genRequest.getTittel()).isEqualTo("Innvilget Engangsstønad");

        assertThat(responseMocked.erFerdigstilt()).isTrue();
        assertThat(responseMocked.getJournalpostId()).isEqualTo(JOURNALPOST_ID);
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