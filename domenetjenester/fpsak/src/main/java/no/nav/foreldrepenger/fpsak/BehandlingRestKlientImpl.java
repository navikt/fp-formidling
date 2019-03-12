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
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.felles.integrasjon.rest.SystemUserOidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class BehandlingRestKlientImpl implements BehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlientImpl.class);
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/behandlinger";

    //TODO bruk bare en restklient, avklar først
    private OidcRestClient oidcRestClient;
    private SystemUserOidcRestClient systemUserOidcRestClient;
    private String endpointFpsakRestBase;

    public BehandlingRestKlientImpl() {//For CDI
    }

    @Inject
    public BehandlingRestKlientImpl(OidcRestClient oidcRestClient,
                                    SystemUserOidcRestClient systemUserOidcRestClient,
                                    @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.systemUserOidcRestClient = systemUserOidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    @Override
    public Optional<BehandlingDto> hentBehandling(BehandlingIdDto behandlingIdDto, boolean systembruker) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            URIBuilder behandlingUriBuilder = new URIBuilder(endpointFpsakRestBase + HENT_BEHANLDING_ENDPOINT);
            behandlingUriBuilder.setParameter("behandlingId", String.valueOf(behandlingIdDto.getBehandlingId()));
            if (systembruker) {
                behandling = systemUserOidcRestClient.getReturnsOptional(behandlingUriBuilder.build(), BehandlingDto.class);
            } else {
                behandling = oidcRestClient.getReturnsOptional(behandlingUriBuilder.build(), BehandlingDto.class);
            }
            if (behandling.isPresent()) {
                final BehandlingDto behandlingDto = behandling.get();
                final Optional<PersonopplysningDto> personopplysningDto = hentPersonopplysninger(behandlingIdDto, behandlingDto.getLinks(), systembruker);
                personopplysningDto.ifPresent(behandlingDto::setPersonopplysningDto);
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling;
    }

    @Override
    public Optional<PersonopplysningDto> hentPersonopplysninger(BehandlingIdDto behandlingIdDto, List<BehandlingResourceLinkDto> resourceLinkDtos, boolean systembruker) {
        Optional<PersonopplysningDto> personopplysningDto = Optional.empty();
        for (BehandlingResourceLinkDto resourceLinkDto : resourceLinkDtos) {
            if (resourceLinkDto.getRel().equals("soeker-personopplysninger")) {
                URI personopplysningUri = URI.create(endpointFpsakRestBase + resourceLinkDto.getHref());

                behandlingIdDto.setSaksnummer(resourceLinkDto.getRequestPayload().getSaksnummer());
                if (systembruker) {
                    personopplysningDto = systemUserOidcRestClient.postReturnsOptional(personopplysningUri, behandlingIdDto, PersonopplysningDto.class);
                } else {
                    personopplysningDto = oidcRestClient.postReturnsOptional(personopplysningUri, behandlingIdDto, PersonopplysningDto.class);
                }
            }
        }
        return personopplysningDto;
    }

    @Override
    public Optional<VergeDto> hentVerge(BehandlingIdDto behandlingIdDto, List<BehandlingResourceLinkDto> resourceLinkDtos, boolean systembruker) {
        Optional<VergeDto> vergeDto = Optional.empty();
        for (BehandlingResourceLinkDto resourceLinkDto : resourceLinkDtos) {
            if (resourceLinkDto.getRel().equals("soeker-verge")) {
                URI personopplysningUri = URI.create(endpointFpsakRestBase + resourceLinkDto.getHref());

                behandlingIdDto.setSaksnummer(resourceLinkDto.getRequestPayload().getSaksnummer());

                if (systembruker) {
                    vergeDto = systemUserOidcRestClient.postReturnsOptional(personopplysningUri, behandlingIdDto, VergeDto.class);
                } else {
                    vergeDto = oidcRestClient.postReturnsOptional(personopplysningUri, behandlingIdDto, VergeDto.class);
                }
            }
        }
        return vergeDto;
    }
}
