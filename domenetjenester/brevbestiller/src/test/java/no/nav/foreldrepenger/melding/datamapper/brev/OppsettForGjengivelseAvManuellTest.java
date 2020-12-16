package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import org.json.JSONObject;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
import no.nav.vedtak.util.StringUtils;

public abstract class OppsettForGjengivelseAvManuellTest {

    @Mock
    BrevParametere brevParametere;
    @Mock
    ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    private JSONObject testdata;
    private ObjectMapper jsonMapper = new ObjectMapper();
    protected ResourceBundle testScenario = ResourceBundle.getBundle(
            String.join("/", ROTMAPPE, mappenHvorFilenMedLoggetTestdataLigger(), "testdata"),
            new Locale("nb", "NO"));

    protected Behandling behandling;
    protected DokumentHendelse dokumentHendelse;

    protected BehandlingRestKlient behandlingRestKlient;

    protected DomeneobjektProvider domeneobjektProvider;


    protected void setup(String scenario) {
        jsonMapper.registerModule(new Jdk8Module());
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        testdata = new JSONObject(testScenario.getString(scenario));
        String behandlingJson = testdata.getString("null");
        BehandlingDto dto;
        try {
            dto = jsonMapper.readValue(behandlingJson, BehandlingDto.class);
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
            String entity = testdata.getString(link.getRel());
            if (StringUtils.nullOrEmpty(entity)) {
                return Optional.empty();
            }
            T value;
            try {
                value = jsonMapper.readValue(entity, clazz);
            } catch (IOException e) {
                throw new IllegalArgumentException("Kunne ikke deserialiser fra json til klasse" + clazz.getSimpleName(), e);
            }
            return Optional.of(value);
        }
    }
}
