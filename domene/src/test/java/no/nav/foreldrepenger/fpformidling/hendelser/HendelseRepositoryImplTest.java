package no.nav.foreldrepenger.fpformidling.hendelser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.fpformidling.dbstoette.JpaExtension;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;

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
                .build();
        hendelseRepository.lagre(dokumentHendelse);

        List<DokumentHendelse> hendelseListe = hendelseRepository.hentDokumentHendelserForBehandling(behandlingUuid);

        assertThat(hendelseListe).hasSize(1);

        assertThat(hendelseRepository.hentDokumentHendelseMedId(hendelseListe.get(0).getId()))
                .isNotNull();
        assertThat(hendelseRepository.finnesHendelseMedUuidAllerede(bestiilingUuid)).isTrue();

    }
}
