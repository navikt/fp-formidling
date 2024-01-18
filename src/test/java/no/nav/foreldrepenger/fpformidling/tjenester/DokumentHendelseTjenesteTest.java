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

import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.HendelseRepository;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelseTjeneste;

@ExtendWith(MockitoExtension.class)
class DokumentHendelseTjenesteTest {

    @Mock
    HendelseRepository hendelseRepository;

    @Test
    void valider_om_hendelse_finnes_allerede_true() {
        when(hendelseRepository.finnesHendelseMedUuidAllerede(any(UUID.class))).thenReturn(true);

        var tjeneste = new DokumentHendelseTjeneste(hendelseRepository);
        var resultat = tjeneste.validerUnikOgLagre(dokumentHendelseBuilder().build());

        verify(hendelseRepository, never()).lagre(any(DokumentHendelse.class));
        assertThat(resultat).isNotPresent();
    }

    @Test
    void valider_om_hendelse_finnes_allerede_false() {
        when(hendelseRepository.finnesHendelseMedUuidAllerede(any(UUID.class))).thenReturn(false);
        doNothing().when(hendelseRepository).lagre(any(DokumentHendelse.class));

        var tjeneste = new DokumentHendelseTjeneste(hendelseRepository);
        var resultat = tjeneste.validerUnikOgLagre(dokumentHendelseBuilder().build());

        verify(hendelseRepository, times(1)).lagre(any(DokumentHendelse.class));
        assertThat(resultat).isPresent();
    }

    private DokumentHendelse.Builder dokumentHendelseBuilder() {
        return DokumentHendelse.builder()
            .medBehandlingUuid(UUID.randomUUID())
            .medBestillingUuid(UUID.randomUUID())
            .medYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medDokumentMalType(DokumentMalType.FRITEKSTBREV);
    }
}
