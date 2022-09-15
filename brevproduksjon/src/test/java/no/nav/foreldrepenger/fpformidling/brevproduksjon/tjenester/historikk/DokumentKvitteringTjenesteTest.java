package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;

@ExtendWith(MockitoExtension.class)
public class DokumentKvitteringTjenesteTest {

    private DokumentKvitteringTjeneste historikkTjeneste;

    @Mock
    private Behandlinger behandlinger;

    @BeforeEach
    public void setup() {
        historikkTjeneste = new DokumentKvitteringTjeneste(behandlinger);
        lenient().doNothing().when(behandlinger).kvitterDokument(Mockito.any());
    }

    @Test
    public void publiserHistorikk() {
        var kvittering = new DokumentProdusertDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                DokumentMalType.INGEN_ENDRING.getKode(),
                "123",
                "123"
        );

        historikkTjeneste.sendKvittering(kvittering);
        verify(behandlinger, times(1)).kvitterDokument(Mockito.any());
    }
}
