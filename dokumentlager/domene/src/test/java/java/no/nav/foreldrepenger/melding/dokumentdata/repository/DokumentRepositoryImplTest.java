package no.nav.foreldrepenger.melding.dokumentdata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentHendelse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;

public class DokumentRepositoryImplTest {

    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private DokumentRepository dokumentRepository;
    private Long behandlingId = 123l;

    @Before
    public void setUp() {
        this.dokumentRepository = new DokumentRepositoryImpl(repositoryRule.getEntityManager());
    }


    @Test
    public void skalLagreOgHenteOppIgjen() {
        DokumentHendelse dokumentHendelse = DokumentHendelse
                .builder()
                .medBehandlingId(behandlingId)
                .build();
        dokumentRepository.lagre(dokumentHendelse);

        List<DokumentHendelse> hendelseListe = dokumentRepository.hentDokumentHendelserForBehandling(behandlingId);

        assertThat(hendelseListe).hasSize(1);

        assertThat(dokumentRepository.hentDokumentHendelseMedId(hendelseListe.get(0).getId()))
                .isNotNull();
    }

    @Test
    public void hentVillkårDokumentMal() {
        assertThat(dokumentRepository.hentDokumentMalType(DokumentMalType.AVSLAGSVEDTAK_DOK).getNavn())
                .isEqualToIgnoringCase("Avslagsbrev");
    }
}