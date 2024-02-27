package no.nav.foreldrepenger.fpformidling.domene.hendelser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseEntitet;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.fpformidling.database.JpaExtension;

@ExtendWith(JpaExtension.class)
class DokumentHendelseRepositoryImplTest {

    private DokumentHendelseRepository dokumentHendelseRepository;

    @BeforeEach
    void setup(EntityManager entityManager) {
        dokumentHendelseRepository = new DokumentHendelseRepository(entityManager);
    }

    @Test
    void skalLagreOgHenteOppIgjen() {
        var behandlingUuid = UUID.randomUUID();
        var bestiilingUuid = UUID.randomUUID();
        var dokumentHendelse = DokumentHendelseEntitet.builder()
            .medBehandlingUuid(behandlingUuid)
            .medBestillingUuid(bestiilingUuid)
            .medDokumentMal(no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum.FORELDREPENGER_ANNULLERT)
            .build();
        dokumentHendelseRepository.lagre(dokumentHendelse);

        var hendelse = dokumentHendelseRepository.hentDokumentHendelseMedId(dokumentHendelse.getId());

        assertThat(hendelse).isNotNull();

        assertThat(dokumentHendelseRepository.finnesHendelseMedUuidAllerede(bestiilingUuid)).isTrue();

    }
}
