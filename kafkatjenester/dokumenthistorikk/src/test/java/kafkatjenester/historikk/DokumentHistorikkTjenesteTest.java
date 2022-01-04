package kafkatjenester.historikk;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.fpformidling.historikk.HistorikkAktør;
import no.nav.foreldrepenger.fpformidling.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.fpformidling.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.foreldrepenger.fpformidling.kafkatjenester.historikk.DokumentHistorikkinnslagProducer;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

@ExtendWith(MockitoExtension.class)
public class DokumentHistorikkTjenesteTest {

    private DokumentHistorikkTjeneste historikkTjeneste;
    @Spy
    private DokumentHistorikkinnslagProducer historikkMeldingProducer;

    @BeforeEach
    public void setup() {
        historikkTjeneste = new DokumentHistorikkTjeneste(historikkMeldingProducer);
        lenient().doNothing().when(historikkMeldingProducer).sendJson(Mockito.any());
    }

    @Test
    public void publiserHistorikk() {

        DokumentHistorikkinnslag historikk = DokumentHistorikkinnslag.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medHistorikkUuid(UUID.randomUUID())
                .medHendelseId(1L)
                .medDokumentMalType(DokumentMalType.INGEN_ENDRING)
                .medJournalpostId(new JournalpostId("123"))
                .medHistorikkAktør(HistorikkAktør.SAKSBEHANDLER)
                .medDokumentId("123")
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();
        historikkTjeneste.publiserHistorikk(historikk);
        verify(historikkMeldingProducer, times(1)).sendJson(Mockito.anyString());
    }
}
