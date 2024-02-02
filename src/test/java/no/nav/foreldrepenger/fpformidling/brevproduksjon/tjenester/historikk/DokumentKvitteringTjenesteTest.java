package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentKvitteringDto;

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
class DokumentKvitteringTjenesteTest {

    private DokumentKvitteringTjeneste historikkTjeneste;

    @Mock
    private Behandlinger behandlinger;

    @BeforeEach
    void setup() {
        historikkTjeneste = new DokumentKvitteringTjeneste(behandlinger);
        lenient().doNothing().when(behandlinger).kvitterDokument(Mockito.any());
    }

    @Test
    void publiserHistorikk() {
        var kvittering = new DokumentKvitteringDto(UUID.randomUUID(), UUID.randomUUID(), "123", "123");

        historikkTjeneste.sendKvittering(kvittering);
        verify(behandlinger, times(1)).kvitterDokument(Mockito.any());
    }
}
