package no.nav.foreldrepenger.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class BehandlingRestKlientImpl implements BehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlientImpl.class);
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/behandlinger";

    private OidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;

    public BehandlingRestKlientImpl() {//For CDI
    }

    @Inject
    public BehandlingRestKlientImpl(OidcRestClient oidcRestClient,
                                    @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    @Override
    public Optional<BehandlingDto> hentBehandling(BehandlingIdDto behandlingIdDto) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            URIBuilder behandlingUriBuilder = new URIBuilder(endpointFpsakRestBase + HENT_BEHANLDING_ENDPOINT);
            behandlingUriBuilder.setParameter("behandlingId", String.valueOf(behandlingIdDto.getBehandlingId()));
            behandling = oidcRestClient.getReturnsOptional(behandlingUriBuilder.build(), BehandlingDto.class);
            if (behandling.isPresent()) {
                final BehandlingDto behandlingDto = behandling.get();
                final Optional<PersonopplysningDto> personopplysningDto = hentPersonopplysninger(behandlingIdDto, behandlingDto.getLinks());
                personopplysningDto.ifPresent(behandlingDto::setPersonopplysningDto);
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling;
    }

    @Override
    public Optional<PersonopplysningDto> hentPersonopplysninger(BehandlingIdDto behandlingIdDto, List<BehandlingResourceLinkDto> resourceLinkDtos) {
        Optional<PersonopplysningDto> personopplysningDto = Optional.empty();
        for (BehandlingResourceLinkDto resourceLinkDto : resourceLinkDtos) {
            if (resourceLinkDto.getRel().equals("soeker-personopplysninger")) {
                URI personopplysningUri = URI.create(endpointFpsakRestBase + resourceLinkDto.getHref());

                behandlingIdDto.setSaksnummer(resourceLinkDto.getRequestPayload().getSaksnummer());
                personopplysningDto = oidcRestClient.postReturnsOptional(personopplysningUri, behandlingIdDto, PersonopplysningDto.class);
            }
        }
        return personopplysningDto;
    }

    @Override
    public Optional<VergeDto> hentVerge(BehandlingIdDto behandlingIdDto, List<BehandlingResourceLinkDto> resourceLinkDtos) {
        Optional<VergeDto> vergeDto = Optional.empty();
        for (BehandlingResourceLinkDto resourceLinkDto : resourceLinkDtos) {
            if (resourceLinkDto.getRel().equals("soeker-verge")) {
                URI personopplysningUri = URI.create(endpointFpsakRestBase + resourceLinkDto.getHref());
                behandlingIdDto.setSaksnummer(resourceLinkDto.getRequestPayload().getSaksnummer());
                vergeDto = oidcRestClient.postReturnsOptional(personopplysningUri, behandlingIdDto, VergeDto.class);
            }
        }
        return vergeDto;
    }

    @Override
    public Optional<FamiliehendelseDto> hentFamiliehendelse(List<BehandlingResourceLinkDto> resourceLinkDtos) {
        return resourceLinkDtos.stream()
                .filter(dto -> "familiehendelse".equals(dto.getRel()))
                .findFirst().flatMap(this::hentFamiliehendelseFraLink);
    }

    private Optional<FamiliehendelseDto> hentFamiliehendelseFraLink(BehandlingResourceLinkDto link) {
        URI familiehendelseUri = URI.create(endpointFpsakRestBase + link.getHref());
        BehandlingIdDto behandlingIdDto = new BehandlingIdDto();
        behandlingIdDto.setBehandlingId(link.getRequestPayload().getBehandlingId());
        behandlingIdDto.setSaksnummer(link.getRequestPayload().getSaksnummer());
        return oidcRestClient.postReturnsOptional(familiehendelseUri, behandlingIdDto, FamiliehendelseDto.class);
    }
}

