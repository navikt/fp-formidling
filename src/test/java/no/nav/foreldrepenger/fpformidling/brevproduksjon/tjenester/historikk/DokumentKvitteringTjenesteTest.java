package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentKvitteringDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;

@ExtendWith(MockitoExtension.class)
class DokumentKvitteringTjenesteTest {

    private DokumentKvitteringTjeneste historikkTjeneste;

    @Mock
    private Behandlinger fpsakKlient;

    @Captor
    ArgumentCaptor<DokumentKvitteringDto> kvitteringCaptor;

    @BeforeEach
    void setup() {
        historikkTjeneste = new DokumentKvitteringTjeneste(fpsakKlient);
        lenient().doNothing().when(fpsakKlient).kvitterDokument(Mockito.any());
    }

    @Test
    void publiserHistorikk() {
        var forventetSaksnummer = "saksnummer";
        var forventetJp = "123";
        var forventetDokumentId = "123";
        historikkTjeneste.sendKvittering(UUID.randomUUID(), UUID.randomUUID(), forventetJp, forventetDokumentId, forventetSaksnummer);

        verify(fpsakKlient).kvitterDokument(kvitteringCaptor.capture());

        var kvittering = kvitteringCaptor.getValue();

        assertThat(kvittering.journalpostId()).isEqualTo(forventetJp);
        assertThat(kvittering.dokumentId()).isEqualTo(forventetDokumentId);
        assertThat(kvittering.saksnummer()).isNotNull();
        assertThat(kvittering.saksnummer().saksnummer()).isEqualTo(forventetSaksnummer);
    }
}
