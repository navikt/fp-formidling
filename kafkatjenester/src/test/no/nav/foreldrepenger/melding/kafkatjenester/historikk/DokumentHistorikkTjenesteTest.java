package no.nav.foreldrepenger.melding.kafkatjenester.historikk;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import ch.qos.logback.classic.Level;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHistorikkDto;
import no.nav.foreldrepenger.melding.historikk.HistorikkAktør;
import no.nav.foreldrepenger.melding.sikkerhet.LogSniffer;

public class DokumentHistorikkTjenesteTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Rule
    public LogSniffer logSniffer = new LogSniffer(Level.ALL);

    private DokumentHistorikkTjeneste historikkTjeneste;
    @Mock
    private DokumentHistorikkMeldingProducer historikkMeldingProducer;

    @Before
    public void setup() {
        historikkTjeneste = new DokumentHistorikkTjeneste(historikkMeldingProducer);
    }

    @Test
    public void publiserHistorikk() {
        DokumentHistorikkDto dto = new DokumentHistorikkDto();
        dto.setBehandlingId(1l);
        dto.setHistorikkAktør(HistorikkAktør.ARBEIDSGIVER.getKode());
        historikkTjeneste.publiserHistorikk(dto);
        logSniffer.assertHasInfoMessage("Publisert historikk");
    }
}