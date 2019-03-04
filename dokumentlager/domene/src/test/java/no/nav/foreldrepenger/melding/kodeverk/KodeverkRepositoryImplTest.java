package no.nav.foreldrepenger.melding.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.melding.kodeverk.arkiv.ArkivFilType;
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
        assertThat(repo.finn(ArkivFilType.class, "PDF")).isEqualTo(ArkivFilType.PDF);
    }

    @Test
    public void skal_kaste_feil_dersom_kodeliste_verdi_ikke_finnes() {
        expectedException.expect(TekniskException.class);
        repo.finn(ArkivFilType.class, "IKKE_EKSISTERENDE_KODE");
    }

    @Test
    public void test_hent_offisiellverdi_flere_ganger() {
        DokumentTypeId d1 = repo.finnForKodeverkEiersKode(DokumentTypeId.class, "I000027");
        DokumentTypeId d2 = repo.finnForKodeverkEiersKode(DokumentTypeId.class, "I000027");
        assertThat(d1).isEqualTo(d2);
    }

    @Test
    public void test_hent_flere_offisielle_koder_samtidig_flere_ganger() {
        List<DokumentTypeId> dokumentTypeIds1 = repo.finnForKodeverkEiersKoder(DokumentTypeId.class, "I000003", "I000027");
        assertThat(dokumentTypeIds1).hasSize(2);
        List<DokumentTypeId> dokumentTypeIds2 = repo.finnForKodeverkEiersKoder(DokumentTypeId.class, "I000003", "I000027");
        assertThat(dokumentTypeIds2).hasSize(2);
    }

    @Test
    public void test_hent_flere_koder_samtidig_flere_ganger() {
        List<DokumentTypeId> dokumentTypeIds1 = repo.finnListe(DokumentTypeId.class, Arrays.asList("SØKNAD_ENGANGSSTØNAD_FØDSEL", "KLAGE_DOKUMENT"));
        assertThat(dokumentTypeIds1).hasSize(2);
        List<DokumentTypeId> dokumentTypeIds2 = repo.finnListe(DokumentTypeId.class, Arrays.asList("SØKNAD_ENGANGSSTØNAD_FØDSEL", "KLAGE_DOKUMENT"));
        assertThat(dokumentTypeIds2).hasSize(2);
    }

}
