package no.nav.foreldrepenger.melding.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.testutilities.db.RepositoryRule;

public class KodeverkRepositoryImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public RepositoryRule repoRule = new UnittestRepositoryRule();
    private KodeverkRepositoryImpl repo = new KodeverkRepositoryImpl(repoRule.getEntityManager());

    @Test
    public void skal_hente_kodeliste_verdi_basert_på_type_og_kode() {
        assertThat(repo.finn(Landkoder.class, "NOR")).isEqualTo(Landkoder.NOR);
    }

    @Test
    public void skal_kaste_feil_dersom_kodeliste_verdi_ikke_finnes() {
        expectedException.expect(TekniskException.class);
        repo.finn(Landkoder.class, "ANDEBY");
    }


}
