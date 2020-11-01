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

import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.historikk.HistorikkinnslagType;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkTjeneste;
import no.nav.foreldrepenger.melding.kafkatjenester.historikk.DokumentHistorikkinnslagProducer;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.JournalpostId;

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
                .medDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK)
                .medJournalpostId(new JournalpostId("123"))
                .medHistorikkAktør(HistorikkAktør.SAKSBEHANDLER)
                .medDokumentId("123")
                .medHistorikkinnslagType(HistorikkinnslagType.BREV_SENT)
                .build();
        historikkTjeneste.publiserHistorikk(historikk);
        verify(historikkMeldingProducer, times(1)).sendJson(Mockito.anyString());
    }
}
