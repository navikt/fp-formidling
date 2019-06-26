package no.nav.foreldrepenger.fpsak;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import no.finn.unleash.Unleash;
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
import no.nav.foreldrepenger.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatDto;
import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.JsonMapper;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class BehandlingRestKlientImpl implements BehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlientImpl.class);
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/behandlinger";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String BEHANDLINGID = "behandlingId";
    private static final String LOG_TESTDATA_TOGGLE = "fpformidling.logging_av_test_data";

    private OidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;

    private Unleash unleash;

    Map<URI, String> rel = new HashMap<>();
    Map<String, String> responseData = new HashMap<>();

    public BehandlingRestKlientImpl() {
        //CDI
    }

    @Inject
    public BehandlingRestKlientImpl(OidcRestClient oidcRestClient,
                                    @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase,
                                    Unleash unleash) {
        this.oidcRestClient = unleash.isEnabled(LOG_TESTDATA_TOGGLE) ? new WrapperCollectingTestdata(oidcRestClient) : oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
        this.unleash = unleash;
    }

    @Override
    @Timed(name = "fpformidling.out.behandling", absolute = true)
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

    @Timed(name = "fpformidling.out.original-behandling", absolute = true)
    public Optional<BehandlingDto> hentOriginalBehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "original-behandling".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BehandlingDto.class));
    }

    @Override
    @Timed(name = "fpformidling.out.personopplysninger", absolute = true)
    public PersonopplysningDto hentPersonopplysninger(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "soeker-personopplysninger".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, PersonopplysningDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Personopplysning for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.verge", absolute = true)
    public Optional<VergeDto> hentVergeHvisfinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "soeker-verge".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, VergeDto.class));
    }

    @Override
    @Timed(name = "fpformidling.out.familiehendelse", absolute = true)
    public FamilieHendelseGrunnlagDto hentFamiliehendelse(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "familiehendelse-v2".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, FamilieHendelseGrunnlagDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Familiehendelse for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.beregningresultatES", absolute = true)
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
    @Timed(name = "fpformidling.out.beregningresultatFP", absolute = true)
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
    @Timed(name = "fpformidling.out.soknad", absolute = true)
    public Optional<SoknadDto> hentSoknadHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "soknad".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SoknadDto.class));
    }

    @Override
    @Timed(name = "fpformidling.out.iay", absolute = true)
    public InntektArbeidYtelseDto hentInntektArbeidYtelseDto(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "inntekt-arbeid-ytelse".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, InntektArbeidYtelseDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente IAY dto for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.klage", absolute = true)
    public KlagebehandlingDto hentKlagebehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "klage-vurdering".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, KlagebehandlingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente klage for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.innsyn", absolute = true)
    public InnsynsbehandlingDto hentInnsynsbehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "innsyn".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, InnsynsbehandlingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente innsyn for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.beregningsgrunnlag", absolute = true)
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
    @Timed(name = "fpformidling.out.vilkar", absolute = true)
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
    @Timed(name = "fpformidling.out.uttak-resultat", absolute = true)
    public Optional<UttakResultatPerioderDto> hentUttaksresultatHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttaksresultat-perioder".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, UttakResultatPerioderDto.class));

    }

    @Override
    public SvangerskapspengerUttakResultatDto hentUttaksresultatSvp(List<BehandlingResourceLink> resourceLinker) {
        return hentUttaksresultatSvpHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente uttaksresultat for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    @Override
    @Timed(name = "fpformidling.out.uttak-resultat-svp", absolute = true)
    public Optional<SvangerskapspengerUttakResultatDto> hentUttaksresultatSvpHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttaksresultat-svangerskapspenger".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SvangerskapspengerUttakResultatDto.class));
    }

    @Override
    @Timed(name = "fpformidling.out.fagsak", absolute = true)
    public FagsakDto hentFagsak(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "fagsak".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, FagsakDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente fagsak for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.ytelsefordeling", absolute = true)
    public YtelseFordelingDto hentYtelseFordeling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "ytelsefordeling".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, YtelseFordelingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente ytelsefordeling for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.saldoer", absolute = true)
    public SaldoerDto hentSaldoer(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttak-stonadskontoer".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SaldoerDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente uttakssaldoer for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.aksjonspunkter", absolute = true)
    public List<AksjonspunktDto> hentAksjonspunkter(List<BehandlingResourceLink> resourceLinker) {
        return Arrays.asList(resourceLinker.stream()
                .filter(dto -> "aksjonspunkter".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, AksjonspunktDto[].class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente vilkår for behandling: " + hentBehandlingId(resourceLinker));
                }));
    }

    @Override
    @Timed(name = "fpformidling.out.klagedokument", absolute = true)
    public MottattKlagedokumentDto hentKlagedokument(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "mottatt-klagedokument".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, MottattKlagedokumentDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente klagedokument for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    @Override
    @Timed(name = "fpformidling.out.mottattdokument", absolute = true)
    public List<MottattDokumentDto> hentMottatteDokumenter(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "mottattdokument".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, MottattDokumentDto[].class))
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

    @Override
    @Timed(name = "fpformidling.out.varsel-revurdering", absolute = true)
    public Optional<Boolean> harSendtVarselOmRevurdering(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "sendt-varsel-om-revurdering".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, Boolean.class));
    }

    protected <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz) {

        if ("POST".equals(link.getType())) {
            URI uri = URI.create(endpointFpsakRestBase + link.getHref());
            rel.put(uri, link.getRel());
            return oidcRestClient.postReturnsOptional(uri, link.getRequestPayload(), clazz);
        } else {
            URI uri = saksnummerRequest(endpointFpsakRestBase + link.getHref(), link.getRequestPayload());
            rel.put(uri, link.getRel());
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

    @Override
    public String getJsonTestdata() {
        if (this.unleash.isEnabled(LOG_TESTDATA_TOGGLE)) {
            return new JSONObject(responseData).toString();
        }
        return null;
    }

    private class WrapperCollectingTestdata extends OidcRestClient {

        public WrapperCollectingTestdata(OidcRestClient oidcRestClient) {
            super(oidcRestClient);
        }

        private <T> Optional<T> getResultOptional(Class<T> clazz, URI endpoint, String entity) {
            if (StringUtils.nullOrEmpty(entity)) {
                return Optional.empty();
            }
            responseData.put(rel.get(endpoint), entity);
            return Optional.of(JsonMapper.fromJson(entity, clazz));
        }

        @Override
        public <T> Optional<T> postReturnsOptional(URI endpoint, Object dto, Class<T> clazz) {
            String entity = post(endpoint, dto);
            return getResultOptional(clazz, endpoint, entity);
        }

        @Override
        public <T> Optional<T> getReturnsOptional(URI endpoint, Class<T> clazz) {
            String entity = get(endpoint);
            return getResultOptional(clazz, endpoint, entity);
        }

        private String get(URI endpoint) {
            HttpGet get = new HttpGet(endpoint);
            try {
                return this.execute(get, new OidcRestClientResponseHandler(endpoint));
            } catch (IOException e) {
                throw new RuntimeException("IOException ved kommunikasjon med server");
            }
        }

        class OidcRestClientResponseHandler implements ResponseHandler<String> {

            private URI endpoint;

            OidcRestClientResponseHandler(URI endpoint) {
                this.endpoint = endpoint;
            }

            @Override
            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;
                } else if (status == HttpStatus.SC_FORBIDDEN) {
                    throw new RuntimeException("Mangler tilgang. Fikk http-kode 403 fra server");
                } else {
                    throw new RuntimeException(String.format("Server svarte med feilkode http-kode '%s' og response var '%s'",
                            status,
                            response.getStatusLine().getReasonPhrase()));
                }
            }
        }
    }
}

