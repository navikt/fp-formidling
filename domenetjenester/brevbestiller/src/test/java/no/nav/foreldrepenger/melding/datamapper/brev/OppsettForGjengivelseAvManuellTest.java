package no.nav.foreldrepenger.melding.datamapper.brev;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.foreldrepenger.PersonTjeneste;
import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dtomapper.BehandlingDtoMapper;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.organisasjon.VirksomhetTjeneste;
import no.nav.vedtak.util.StringUtils;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;

public abstract class OppsettForGjengivelseAvManuellTest {

    @Mock
    BrevParametere brevParametere;
    @Mock
    PersonTjeneste personTjeneste;
    @Mock
    VirksomhetTjeneste virksomhetTjeneste;
    @Mock
    ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    private JSONObject testdata;
    private ObjectMapper jsonMapper = new ObjectMapper();
    private JSONParser parser = new JSONParser();
    private static final String TESTDATA_CONTENT = "src/test/resources/dokumentmal/innvilgelsesvangerskapspenger/testdata/";

    protected ResourceBundle testScenario = ResourceBundle.getBundle(
            String.join("/", ROTMAPPE, mappenHvorFilenMedLoggetTestdataLigger(), "testdata"),
            new Locale("nb", "NO"));

    protected Behandling behandling;
    protected DokumentHendelse dokumentHendelse;
    protected BehandlingRestKlient behandlingRestKlient;
    protected DomeneobjektProvider domeneobjektProvider;


    protected void setup(String scenario) throws IOException, ParseException {
        jsonMapper.registerModule(new Jdk8Module());
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(TESTDATA_CONTENT + scenario + ".json"), StandardCharsets.UTF_8));
        testdata = (JSONObject)parser.parse(reader);

        JSONObject behandlingJson = (JSONObject) testdata.get("behandling");
        BehandlingDto dto;
        try {
            dto = jsonMapper.readValue(behandlingJson.toJSONString(), BehandlingDto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne ikke deserialiser fra json til BehandlingDto", e);
        }
        //behandling = Behandling.builder(behandlingDtoMapper.mapBehandlingFraDto(dto)).build();
        behandling = BehandlingDtoMapper.mapBehandlingFraDto(dto);
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
    }

    abstract String mappenHvorFilenMedLoggetTestdataLigger();

    protected class RedirectedToJsonResource extends BehandlingRestKlient {

        @Override
        protected <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {
            JSONAware entity = (JSONAware)testdata.get(link.getRel());
            jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            if (StringUtils.nullOrEmpty(entity.toJSONString())) {
                return Optional.empty();
            }
            T value;
            try {
                value = jsonMapper.readValue(entity.toJSONString(), clazz);
            } catch (IOException e) {
                throw new IllegalArgumentException("Kunne ikke deserialiser fra json til klasse" + clazz.getSimpleName(), e);
            }
            return Optional.of(value);
        }
    }
}
