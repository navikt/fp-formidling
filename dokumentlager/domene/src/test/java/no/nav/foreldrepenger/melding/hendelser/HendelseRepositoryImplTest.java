package no.nav.foreldrepenger.melding.hendelser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;

public class HendelseRepositoryImplTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private HendelseRepository hendelseRepository;

    @Before
    public void setup() {
        hendelseRepository = new HendelseRepositoryImpl(repositoryRule.getEntityManager());
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
