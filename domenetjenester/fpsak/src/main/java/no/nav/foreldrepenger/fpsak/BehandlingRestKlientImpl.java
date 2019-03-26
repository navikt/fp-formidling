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
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingRelLinkPayloadDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
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
    public BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            URIBuilder behandlingUriBuilder = new URIBuilder(endpointFpsakRestBase + HENT_BEHANLDING_ENDPOINT);
            behandlingUriBuilder.setParameter("behandlingId", String.valueOf(behandlingIdDto.getBehandlingId()));
            behandling = oidcRestClient.getReturnsOptional(behandlingUriBuilder.build(), BehandlingDto.class);
            if (behandling.isPresent()) {
                final BehandlingDto behandlingDto = behandling.get();
                final Optional<PersonopplysningDto> personopplysningDto = hentPersonopplysninger(behandlingDto.getLinks());
                personopplysningDto.ifPresent(behandlingDto::setPersonopplysningDto);
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling.orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente behandling: " + behandlingIdDto.getBehandlingId());
        });
    }

    //TODO ramesh/aleksander - er det egentlig greit at denne er optional?
    @Override
    public Optional<PersonopplysningDto> hentPersonopplysninger(List<BehandlingResourceLinkDto> resourceLinkDtos) {
        return resourceLinkDtos.stream()
                .filter(dto -> "soeker-personopplysninger".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, PersonopplysningDto.class));
    }

    @Override
    public VergeDto hentVerge(List<BehandlingResourceLinkDto> resourceLinkDtos) {
        return resourceLinkDtos.stream()
                .filter(dto -> "soeker-verge".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, VergeDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Verge for behandling: " + hentBehandlingId(resourceLinkDtos));
                });
    }

    @Override
    public FamiliehendelseDto hentFamiliehendelse(List<BehandlingResourceLinkDto> resourceLinkDtos) {
        return resourceLinkDtos.stream()
                .filter(dto -> "familiehendelse".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, FamiliehendelseDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Familiehendelse for behandling: " + hentBehandlingId(resourceLinkDtos));
                });
    }

    @Override
    public BeregningsresultatEngangsstønadDto hentBeregningsresultatEngangsstønad(List<BehandlingResourceLinkDto> resourceLinkDtos) {
        return resourceLinkDtos.stream()
                .filter(dto -> "beregningsresultat-engangsstonad".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BeregningsresultatEngangsstønadDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Beregningsresultat engangsstønad for behandling: " + hentBehandlingId(resourceLinkDtos));
                });
    }

    private <T> Optional<T> hentDtoFraLink(BehandlingResourceLinkDto link, Class<T> clazz) {
        URI familiehendelseUri = URI.create(endpointFpsakRestBase + link.getHref());
        BehandlingIdDto behandlingIdDto = new BehandlingIdDto();
        behandlingIdDto.setBehandlingId(link.getRequestPayload().getBehandlingId());
        behandlingIdDto.setSaksnummer(link.getRequestPayload().getSaksnummer());
        return oidcRestClient.postReturnsOptional(familiehendelseUri, behandlingIdDto, clazz);
    }

    private Long hentBehandlingId(List<BehandlingResourceLinkDto> linkListe) {
        return linkListe.stream()
                .map(BehandlingResourceLinkDto::getRequestPayload)
                .map(BehandlingRelLinkPayloadDto::getBehandlingId)
                .findFirst()
                .orElse(null);
    }

}

