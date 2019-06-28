package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

public class InnvilgelseSvangerskapspengerBrevMapperTest extends OppsettForGjengivelseAvManuellTest {

    @InjectMocks
    protected InnvilgelseSvangerskapspengerBrevMapper mapper;

    @Before
    public void setup() {
        mapper = new InnvilgelseSvangerskapspengerBrevMapper(brevParametere, domeneobjektProvider);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void scenario_med_to_arbeidsforhold() {
        setup("scenario");
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(testScenario.getString("forventet_brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(testScenario.getString("forventet_overskrift"));
    }

    @Ignore
    @Test
    public void scenario_AT_FL() {
        setup("scenario_AT_FN_2");
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(testScenario.getString("forventet_brødtekst"));
    }

    @Override
    String mappenHvorFilenMedLoggetTestdataLigger() {
        return "innvilgelsesvangerskapspenger";
    }

}
