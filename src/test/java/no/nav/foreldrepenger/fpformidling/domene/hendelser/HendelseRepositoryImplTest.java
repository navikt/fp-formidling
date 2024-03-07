package no.nav.foreldrepenger.fpformidling.domene.hendelser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.fpformidling.database.JpaExtension;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;

@ExtendWith(JpaExtension.class)
class HendelseRepositoryImplTest {

    private HendelseRepository hendelseRepository;

    @BeforeEach
    void setup(EntityManager entityManager) {
        hendelseRepository = new HendelseRepository(entityManager);
    }

    @Test
    void skalLagreOgHenteOppIgjen() {
        var behandlingUuid = UUID.randomUUID();
        var bestiilingUuid = UUID.randomUUID();
        var dokumentHendelse = DokumentHendelse.builder()
            .medBehandlingUuid(behandlingUuid)
            .medBestillingUuid(bestiilingUuid)
            .medDokumentMal(DokumentMal.FORELDREPENGER_ANNULLERT)
            .build();
        hendelseRepository.lagre(dokumentHendelse);

        var hendelse = hendelseRepository.hentDokumentHendelseMedId(dokumentHendelse.getId());

        assertThat(hendelse).isNotNull();

        assertThat(hendelseRepository.finnesHendelseMedUuidAllerede(bestiilingUuid)).isTrue();

    }
}
