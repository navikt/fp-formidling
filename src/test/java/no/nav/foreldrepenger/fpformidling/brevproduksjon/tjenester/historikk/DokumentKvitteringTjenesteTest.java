package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.historikk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentKvitteringDto;

@ExtendWith(MockitoExtension.class)
class DokumentKvitteringTjenesteTest {

    @Mock
    private Behandlinger fpsakKlient;

    @Captor
    private ArgumentCaptor<DokumentKvitteringDto> kvitteringCaptor;

    @Test
    void publiserHistorikk() {
        var historikkTjeneste = new DokumentKvitteringTjeneste(fpsakKlient);
        doNothing().when(fpsakKlient).kvitterDokument(Mockito.any());

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
