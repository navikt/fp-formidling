package no.nav.foreldrepenger.fpformidling.tjenester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseEntitet;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseTjeneste;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseRepository;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum;

@ExtendWith(MockitoExtension.class)
class DokumentHendelseEntitetTjenesteTest {

    @Mock
    DokumentHendelseRepository dokumentHendelseRepository;

    @Test
    void valider_om_hendelse_finnes_allerede_true() {
        when(dokumentHendelseRepository.finnesHendelseMedUuidAllerede(any(UUID.class))).thenReturn(true);

        var tjeneste = new DokumentHendelseTjeneste(dokumentHendelseRepository);
        var resultat = tjeneste.validerUnikOgLagre(dokumentHendelseBuilder().build());

        verify(dokumentHendelseRepository, never()).lagre(any(DokumentHendelseEntitet.class));
        assertThat(resultat).isNotPresent();
    }

    @Test
    void valider_om_hendelse_finnes_allerede_false() {
        when(dokumentHendelseRepository.finnesHendelseMedUuidAllerede(any(UUID.class))).thenReturn(false);
        doNothing().when(dokumentHendelseRepository).lagre(any(DokumentHendelseEntitet.class));

        var tjeneste = new DokumentHendelseTjeneste(dokumentHendelseRepository);
        var resultat = tjeneste.validerUnikOgLagre(dokumentHendelseBuilder().build());

        verify(dokumentHendelseRepository, times(1)).lagre(any(DokumentHendelseEntitet.class));
        assertThat(resultat).isPresent();
    }

    private DokumentHendelseEntitet.Builder dokumentHendelseBuilder() {
        return DokumentHendelseEntitet.builder()
            .medBehandlingUuid(UUID.randomUUID())
            .medBestillingUuid(UUID.randomUUID())
            .medDokumentMal(DokumentMalEnum.FRITEKSTBREV);
    }
}
