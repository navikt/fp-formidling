package no.nav.foreldrepenger.fpformidling.domene.hendelser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.fpformidling.database.JpaExtension;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi.DokumentMalType;

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
            .medYtelseType(FagsakYtelseType.FORELDREPENGER)
            .medDokumentMalType(DokumentMalType.FORELDREPENGER_ANNULLERT)
            .build();
        hendelseRepository.lagre(dokumentHendelse);

        var hendelse = hendelseRepository.hentDokumentHendelseMedId(dokumentHendelse.getId());

        assertThat(hendelse).isNotNull();

        assertThat(hendelseRepository.erDokumentHendelseMottatt(behandlingUuid, DokumentMalType.FORELDREPENGER_ANNULLERT)).isTrue();
        assertThat(hendelseRepository.erDokumentHendelseMottatt(behandlingUuid, DokumentMalType.FORELDREPENGER_AVSLAG)).isFalse();
        assertThat(hendelseRepository.finnesHendelseMedUuidAllerede(bestiilingUuid)).isTrue();

    }
}
