package no.nav.foreldrepenger.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.MottattDokumentDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamilieHendelseGrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.klage.MottattKlagedokumentDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.PersonopplysningDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.SaldoerDto;
import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class BehandlingRestKlientImpl implements BehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlientImpl.class);
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/behandlinger";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String BEHANDLINGID = "behandlingId";

    private OidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;

    public BehandlingRestKlientImpl() {
        //CDI
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
            behandlingUriBuilder.setParameter("behandlingId", velgRiktigBehandlingIdfraDto(behandlingIdDto));
            behandling = oidcRestClient.getReturnsOptional(behandlingUriBuilder.build(), BehandlingDto.class);
        } catch (URISyntaxException e) {
            LOGGER.error("Feil ved oppretting av URI.", e);
        }
        return behandling.orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente behandling: " + behandlingIdDto.getBehandlingId().toString());
        });
    }

    private String velgRiktigBehandlingIdfraDto(BehandlingIdDto behandlingIdDto) {
        return behandlingIdDto.getBehandlingUuid() != null ? behandlingIdDto.getBehandlingUuid().toString() : behandlingIdDto.getBehandlingId().toString();
    }

    public Optional<BehandlingDto> hentOriginalBehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "original-behandling".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BehandlingDto.class));
    }

    @Override
    public PersonopplysningDto hentPersonopplysninger(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "soeker-personopplysninger".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, PersonopplysningDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Personopplysning for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public VergeDto hentVerge(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "soeker-verge".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, VergeDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Verge for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public FamilieHendelseGrunnlagDto hentFamiliehendelse(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "familiehendelse-v2".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, FamilieHendelseGrunnlagDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Familiehendelse for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public Optional<BeregningsresultatEngangsstønadDto> hentBeregningsresultatEngangsstønadHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "beregningsresultat-engangsstonad".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BeregningsresultatEngangsstønadDto.class));
    }

    @Override
    public BeregningsresultatEngangsstønadDto hentBeregningsresultatEngangsstønad(List<BehandlingResourceLink> resourceLinker) {
        return hentBeregningsresultatEngangsstønadHvisFinnes(resourceLinker)
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Beregningsresultat engangsstønad for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public BeregningsresultatMedUttaksplanDto hentBeregningsresultatForeldrepenger(List<BehandlingResourceLink> resourceLinker) {
        return hentBeregningsresultatForeldrepengerHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente Beregningsresultat foreldrepenger for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    @Override
    public Optional<BeregningsresultatMedUttaksplanDto> hentBeregningsresultatForeldrepengerHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "beregningsresultat-foreldrepenger".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BeregningsresultatMedUttaksplanDto.class));
    }

    @Override
    public Optional<SoknadDto> hentSoknadHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "soknad".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SoknadDto.class));
    }

    @Override
    public InntektArbeidYtelseDto hentInntektArbeidYtelseDto(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "inntekt-arbeid-ytelse".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, InntektArbeidYtelseDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente IAY dto for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public KlagebehandlingDto hentKlagebehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "klage-vurdering".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, KlagebehandlingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente klage for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public InnsynsbehandlingDto hentInnsynsbehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "innsyn".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, InnsynsbehandlingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente innsyn for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public BeregningsgrunnlagDto hentBeregningsgrunnlag(List<BehandlingResourceLink> resourceLinker) {
        return hentBeregningsgrunnlagHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente beregningsgrunnlag for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    @Override
    public Optional<BeregningsgrunnlagDto> hentBeregningsgrunnlagHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "beregningsgrunnlag".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BeregningsgrunnlagDto.class));
    }

    @Override
    public List<VilkårDto> hentVilkår(List<BehandlingResourceLink> resourceLinker) {
        return Arrays.asList(resourceLinker.stream()
                .filter(dto -> "vilkar".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, VilkårDto[].class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente vilkår for behandling: " + hentBehandlingId(resourceLinker));
                }));
    }

    @Override
    public UttakResultatPerioderDto hentUttaksresultat(List<BehandlingResourceLink> resourceLinker) {
        return hentUttaksresultatHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente uttaksperioder for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    @Override
    public Optional<UttakResultatPerioderDto> hentUttaksresultatHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttaksresultat-perioder".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, UttakResultatPerioderDto.class));

    }

    @Override
    public FagsakDto hentFagsak(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "fagsak".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, FagsakDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente fagsak for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public YtelseFordelingDto hentYtelseFordeling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "ytelsefordeling".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, YtelseFordelingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente ytelsefordeling for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public SaldoerDto hentSaldoer(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttak-stonadskontoer".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SaldoerDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente uttakssaldoer for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public List<AksjonspunktDto> hentAksjonspunkter(List<BehandlingResourceLink> resourceLinker) {
        return Arrays.asList(resourceLinker.stream()
                .filter(dto -> "aksjonspunkter".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, AksjonspunktDto[].class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente vilkår for behandling: " + hentBehandlingId(resourceLinker));
                }));
    }

    @Override
    public MottattKlagedokumentDto hentKlagedokument(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "mottatt-klagedokument".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, MottattKlagedokumentDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente klagedokument for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    public List<MottattDokumentDto> hentMottatteDokumenter(List<BehandlingResourceLink> resourceLinker) {
        return Arrays.asList(resourceLinker.stream()
                .filter(dto -> "mottattdokument".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, MottattDokumentDto[].class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente mottatte dokumenter for behandling: " + hentBehandlingId(resourceLinker));
                }));
    }

    private <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {

        if ("POST".equals(link.getType())) {
            URI uri = URI.create(endpointFpsakRestBase + link.getHref());
            return oidcRestClient.postReturnsOptional(uri, link.getRequestPayload(), clazz);
        } else {
            URI uri = saksnummerRequest(endpointFpsakRestBase + link.getHref(), link.getRequestPayload());
            return oidcRestClient.getReturnsOptional(uri, clazz);
        }
    }

    private URI saksnummerRequest(String endpoint, BehandlingRelLinkPayload payload) {
        try {
            URIBuilder uriBuilder = new URIBuilder(endpoint);
            if (payload != null) {
                //Hvis payloaden er null, er GET parameterne antagelivis allerede satt i urlen
                if (payload.getSaksnummer() != null) {
                    uriBuilder.addParameter(SAKSNUMMER, String.valueOf(payload.getSaksnummer()));
                }
                if (payload.getBehandlingId() != null) {
                    uriBuilder.addParameter(BEHANDLINGID, String.valueOf(payload.getBehandlingId()));
                }
            }
            return uriBuilder
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String hentBehandlingId(List<BehandlingResourceLink> linkListe) {
        return linkListe.stream()
                .map(BehandlingResourceLink::getRequestPayload)
                .filter(Objects::nonNull)
                .map(BehandlingRelLinkPayload::getBehandlingUuid)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}

