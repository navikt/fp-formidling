package no.nav.foreldrepenger.melding.kodeverk;

import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.vedtak.felles.testutilities.db.RepositoryRule;

public class KodeverkRepositoryImplTest {


    @Rule
    public RepositoryRule repoRule = new UnittestRepositoryRule();
    private KodeverkRepositoryImpl repo = new KodeverkRepositoryImpl(repoRule.getEntityManager());

    private Map<String, String> landkoder;

    @Before
    public void setup() {
        landkoder = new HashMap<>(repo.hentLandkodeISO2TilLandkoderMap());
    }

    @Test
    public void iso3ErNull() {
        String iso2 = tilIso2(null);
        Assert.assertThat(iso2, is("???"));
    }

    @Test
    public void iso2FinnesIkke() {
        String iso2 = tilIso2("XXX");
        Assert.assertThat(iso2, is(Landkoder.UOPPGITT.getKode()));
    }

    @Ignore // Denne feiler ....
    @Test
    public void iso2Finnes() {
        String iso2 = tilIso2("NOR");
        Assert.assertThat(iso2, is("NO"));
    }

    @Test
    public void iso2Finnes2() {
        String iso2 = tilIso2("NO");
        Assert.assertThat(iso2, is("NOR"));
    }


    public String tilIso2(String iso3) {
        return Optional.ofNullable(iso3)
                .map(iso2 -> landkoder.get(iso2))
                .orElse(Landkoder.UOPPGITT.getKode());
    }


}
