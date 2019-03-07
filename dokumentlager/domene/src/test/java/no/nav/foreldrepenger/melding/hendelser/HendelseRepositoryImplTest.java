package no.nav.foreldrepenger.melding.hendelser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;

public class HendelseRepositoryImplTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private HendelseRepository hendelseRepository;

    private Long behandlingId = 123l;

    @Before
    public void setup() {
        hendelseRepository = new HendelseRepositoryImpl(repositoryRule.getEntityManager());
    }

    @Test
    public void skalLagreOgHenteOppIgjen() {
        DokumentHendelse dokumentHendelse = DokumentHendelse
                .builder()
                .medBehandlingId(behandlingId)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
        hendelseRepository.lagre(dokumentHendelse);

        List<DokumentHendelse> hendelseListe = hendelseRepository.hentDokumentHendelserForBehandling(behandlingId);

        assertThat(hendelseListe).hasSize(1);

        assertThat(hendelseRepository.hentDokumentHendelseMedId(hendelseListe.get(0).getId()))
                .isNotNull();
    }
}