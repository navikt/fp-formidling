package no.nav.foreldrepenger.melding.kodeverk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktType;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
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
        AksjonspunktType d1 = repo.finnForKodeverkEiersKode(AksjonspunktType.class, "Manuell");
        AksjonspunktType d2 = repo.finnForKodeverkEiersKode(AksjonspunktType.class, "Manuell");
        assertThat(d1).isEqualTo(d2);
    }

    @Test
    public void test_hent_flere_offisielle_koder_samtidig_flere_ganger() {
        List<Landkoder> landkoder1 = repo.finnForKodeverkEiersKoder(Landkoder.class, "349", "546");
        assertThat(landkoder1).hasSize(2);
        List<Landkoder> landkoder2 = repo.finnForKodeverkEiersKoder(Landkoder.class, "349", "546");
        assertThat(landkoder2).hasSize(2);
    }

    @Test
    public void test_hent_flere_koder_samtidig_flere_ganger() {
        List<AksjonspunktType> aksjonspunktTypes1 = repo.finnListe(AksjonspunktType.class, Arrays.asList("AUTO", "OVST"));
        assertThat(aksjonspunktTypes1).hasSize(2);
        List<AksjonspunktType> aksjonspunktTypes2 = repo.finnListe(AksjonspunktType.class, Arrays.asList("AUTO", "OVST"));
        assertThat(aksjonspunktTypes2).hasSize(2);
    }

}
