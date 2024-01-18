package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingTema;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.domene.typer.Saksnummer;
import no.nav.vedtak.felles.integrasjon.dokarkiv.DokArkiv;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.AvsenderMottaker;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.OpprettJournalpostRequest;
import no.nav.vedtak.felles.integrasjon.dokarkiv.dto.OpprettJournalpostResponse;

@ExtendWith(MockitoExtension.class)
class OpprettJournalpostTjenesteTest {
    private static final String MOTTAKER_ID = "1234";
    private static final String JOURNALPOST_ID = "234567";
    private static final String MOTTAKER_NAVN = "SOEKERS_NAVN";
    private static final String FRITEKST = "FRITEKST";
    private static final byte[] GEN_BREV = "Dette er et lite testbrev med lite innhold".getBytes();
    private static final String FNR = "99999999899";

    private OpprettJournalpostTjeneste opprettJournalpost; // objektet vi tester

    @Mock
    private DokArkiv dokArkivKlient;

    @BeforeEach
    void setup() {
        opprettJournalpost = new OpprettJournalpostTjeneste(dokArkivKlient);

        var response = new OpprettJournalpostResponse(JOURNALPOST_ID, true, List.of(new OpprettJournalpostResponse.DokumentInfoResponse("1111")));
        when(dokArkivKlient.opprettJournalpost(any(OpprettJournalpostRequest.class), eq(true))).thenReturn(response);
    }

    @Test
    void skal_journalføre_generert_INNVES_brev() {
        // Arrange
        var requestCaptor = ArgumentCaptor.forClass(OpprettJournalpostRequest.class);

        var dokumentFelles = getDokumentFelles();
        var dokumentHendelse = lagStandardHendelseBuilder().medTittel("Innvilget Engangsstønad").build();

        var unikBestillingsId = dokumentHendelse.getBestillingUuid().toString() + "-" + 1;

        var saksnummer = new Saksnummer("153456789");

        // Act
        var responseMocked = opprettJournalpost.journalførUtsendelse(GEN_BREV, DokumentMalType.ENGANGSSTØNAD_INNVILGELSE, dokumentFelles,
            dokumentHendelse, saksnummer, true, null, unikBestillingsId, DokumentMalType.ENGANGSSTØNAD_INNVILGELSE);

        // Assert
        Mockito.verify(dokArkivKlient).opprettJournalpost(requestCaptor.capture(), eq(true));

        var genRequest = requestCaptor.getValue();
        assertThat(genRequest.tema()).isEqualTo("FOR");
        assertThat(genRequest.behandlingstema()).isEqualTo(BehandlingTema.ENGANGSSTØNAD.getOffisiellKode());
        assertThat(genRequest.sak().sakstype().name()).isEqualTo("FAGSAK");
        assertThat(genRequest.sak().fagsakId()).isEqualTo(saksnummer.getVerdi());
        assertThat(genRequest.sak().fagsaksystem()).isEqualTo("FS36");
        assertThat(genRequest.avsenderMottaker().id()).isEqualTo(MOTTAKER_ID);
        assertThat(genRequest.avsenderMottaker().navn()).isEqualTo(MOTTAKER_NAVN);
        assertThat(genRequest.avsenderMottaker().idType()).isEqualByComparingTo(AvsenderMottaker.AvsenderMottakerIdType.FNR);
        assertThat(genRequest.journalfoerendeEnhet()).isEqualTo("9999");
        assertThat(genRequest.bruker().id()).isEqualTo(FNR);
        assertThat(genRequest.eksternReferanseId()).isEqualTo(unikBestillingsId);
        assertThat(genRequest.dokumenter().get(0).brevkode()).isEqualTo(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE.getKode());
        var brev = genRequest.dokumenter().get(0).dokumentvarianter().get(0).fysiskDokument();
        assertThat(brev).contains(GEN_BREV);
        assertThat(genRequest.dokumenter().get(0).dokumentvarianter().get(0).variantformat().name()).isEqualTo("ARKIV");
        assertThat(genRequest.dokumenter().get(0).dokumentvarianter().get(0).filtype().name()).isEqualTo("PDFA");
        assertThat(genRequest.kanal()).isNull();
        assertThat(genRequest.tittel()).isEqualTo("Innvilget Engangsstønad");

        assertThat(responseMocked.journalpostferdigstilt()).isTrue();
        assertThat(responseMocked.journalpostId()).isEqualTo(JOURNALPOST_ID);
    }

    private DokumentFelles getDokumentFelles() {
        var dokumentFelles = Mockito.mock(DokumentFelles.class);
        when(dokumentFelles.getMottakerNavn()).thenReturn(MOTTAKER_NAVN);
        when(dokumentFelles.getSakspartId()).thenReturn(FNR);
        when(dokumentFelles.getMottakerId()).thenReturn(MOTTAKER_ID);
        when(dokumentFelles.getMottakerType()).thenReturn(DokumentFelles.MottakerType.PERSON);
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
