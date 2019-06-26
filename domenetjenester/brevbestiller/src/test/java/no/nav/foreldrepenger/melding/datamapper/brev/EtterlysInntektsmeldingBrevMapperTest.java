package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.BehandlingRestKlientImpl;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.vedtak.felles.integrasjon.rest.JsonMapper;
import no.nav.vedtak.util.StringUtils;

public class EtterlysInntektsmeldingBrevMapperTest {
    private static final long ID = 123L;
    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;

    private JSONObject testdata;
    private ResourceBundle testScenario = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, "etterlysinntektsmelding", "testdata"),
                new Locale("nb", "NO"));

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Mock
    DokumentFelles dokumentFelles;
    @Mock
    FellesType fellesType;
    @Mock
    BrevParametere brevParametere;

    @Spy
    private BehandlingRestKlient behandlingRestKlient = new RedirectedToJsonResource();

    @InjectMocks
    private DomeneobjektProvider domeneobjektProvider = Mockito.spy(DomeneobjektProvider.class);

    @InjectMocks
    private EtterlysInntektsmeldingBrevMapper mapper;

    @Before
    public void setup() {
        testdata = new JSONObject(testScenario.getString("scenario"));
        // Mockito.doReturn(null).when(behandlingRestKlient)
        Behandling.Builder behanlingBuilder = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .medSpråkkode(Språkkode.nb);
        testdata.keySet().stream().map(key -> BehandlingResourceLink.ny().medRel(key).build())
                .forEach(behanlingBuilder::leggTilResourceLink);
        behandling = behanlingBuilder.build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();

        mapper = new EtterlysInntektsmeldingBrevMapper();
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void test_map_fagtype() {
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(testScenario.getString("forventet_brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(testScenario.getString("forventet_overskrift"));
    }

    private class RedirectedToJsonResource extends BehandlingRestKlientImpl {

        @Override
        protected <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {
            String entity = testdata.getString(link.getRel());
            if (StringUtils.nullOrEmpty(entity)) {
                return Optional.empty();
            }
            return Optional.of(JsonMapper.fromJson(entity, clazz));
        }
    }
}
