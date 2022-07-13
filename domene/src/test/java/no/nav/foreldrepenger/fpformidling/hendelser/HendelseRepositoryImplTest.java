package no.nav.foreldrepenger.fpformidling.hendelser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.fpformidling.dbstoette.JpaExtension;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(JpaExtension.class)
public class HendelseRepositoryImplTest {

    private HendelseRepository hendelseRepository;

    @BeforeEach
    public void setup(EntityManager entityManager) {
        hendelseRepository = new HendelseRepository(entityManager);
    }

    @Test
    public void skalLagreOgHenteOppIgjen() {
        UUID behandlingUuid = UUID.randomUUID();
        UUID bestiilingUuid = UUID.randomUUID();
        DokumentHendelse dokumentHendelse = DokumentHendelse
                .builder()
                .medBehandlingUuid(behandlingUuid)
                .medBestillingUuid(bestiilingUuid)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medDokumentMalType(DokumentMalType.FORELDREPENGER_ANNULLERT)
                .build();
        hendelseRepository.lagre(dokumentHendelse);

        DokumentHendelse hendelse = hendelseRepository.hentDokumentHendelseMedId(dokumentHendelse.getId());

        assertThat(hendelse).isNotNull();

        assertThat(hendelseRepository.erDokumentHendelseMottatt(behandlingUuid, DokumentMalType.FORELDREPENGER_ANNULLERT))
                .isTrue();
        assertThat(hendelseRepository.erDokumentHendelseMottatt(behandlingUuid, DokumentMalType.FORELDREPENGER_AVSLAG))
                .isFalse();
        assertThat(hendelseRepository.finnesHendelseMedUuidAllerede(bestiilingUuid)).isTrue();

    }
}
