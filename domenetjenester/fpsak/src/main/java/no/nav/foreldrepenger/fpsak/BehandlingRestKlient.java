package no.nav.foreldrepenger.fpsak;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpsak.dto.anke.AnkebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.MottattDokumentDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamilieHendelseGrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.v2.BeregningsgrunnlagDtoV2;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektArbeidYtelseDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.klage.MottattKlagedokumentDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadBackendDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.SaldoerDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatDto;
import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.melding.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;
import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class BehandlingRestKlient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BehandlingRestKlient.class);
    private static final String FPSAK_REST_BASE_URL = "fpsak_rest_base.url";
    private static final String HENT_BEHANLDING_ENDPOINT = "/fpsak/api/formidling/ressurser";
    private static final String SAKSNUMMER = "saksnummer";
    private static final String BEHANDLING_ID = "behandlingId";

    private OidcRestClient oidcRestClient;
    private String endpointFpsakRestBase;

    Map<URI, String> rel = new HashMap<>();

    public BehandlingRestKlient() {
        //CDI
    }

    @Inject
    public BehandlingRestKlient(OidcRestClient oidcRestClient,
                                @KonfigVerdi(FPSAK_REST_BASE_URL) String endpointFpsakRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointFpsakRestBase = endpointFpsakRestBase;
    }

    public BehandlingDto hentBehandling(BehandlingIdDto behandlingIdDto) {
        Optional<BehandlingDto> behandling = Optional.empty();
        try {
            URIBuilder behandlingUriBuilder = new URIBuilder(endpointFpsakRestBase + HENT_BEHANLDING_ENDPOINT);
            behandlingUriBuilder.setParameter(BEHANDLING_ID, velgRiktigBehandlingIdfraDto(behandlingIdDto));
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

    public Optional<VergeDto> hentVergeHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "verge-backend".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, VergeDto.class));
    }

    public FamilieHendelseGrunnlagDto hentFamiliehendelse(List<BehandlingResourceLink> resourceLinker) {
        return hentFamiliehendelseHvisFinnes(resourceLinker)
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Familiehendelse for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public Optional<FamilieHendelseGrunnlagDto> hentFamiliehendelseHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "familiehendelse-v2".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, FamilieHendelseGrunnlagDto.class));
    }

    public Optional<BeregningsresultatEngangsstønadDto> hentBeregningsresultatEngangsstønadHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "beregningsresultat-engangsstonad".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BeregningsresultatEngangsstønadDto.class));
    }

    public BeregningsresultatEngangsstønadDto hentBeregningsresultatEngangsstønad(List<BehandlingResourceLink> resourceLinker) {
        return hentBeregningsresultatEngangsstønadHvisFinnes(resourceLinker)
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente Beregningsresultat engangsstønad for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public BeregningsresultatMedUttaksplanDto hentBeregningsresultatForeldrepenger(List<BehandlingResourceLink> resourceLinker) {
        return hentBeregningsresultatForeldrepengerHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente Beregningsresultat foreldrepenger for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    public Optional<BeregningsresultatMedUttaksplanDto> hentBeregningsresultatForeldrepengerHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "beregningsresultat-foreldrepenger".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BeregningsresultatMedUttaksplanDto.class));
    }

    public Optional<SoknadBackendDto> hentSoknadHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "soknad-backend".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SoknadBackendDto.class));
    }

    public InntektArbeidYtelseDto hentInntektArbeidYtelseDto(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "inntekt-arbeid-ytelse".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, InntektArbeidYtelseDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente IAY dto for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public KlagebehandlingDto hentKlagebehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "klage-vurdering".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, KlagebehandlingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente klage for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public AnkebehandlingDto hentAnkebehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "anke-vurdering".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, AnkebehandlingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente klage for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public InnsynsbehandlingDto hentInnsynsbehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "innsyn".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, InnsynsbehandlingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente innsyn for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public BeregningsgrunnlagDtoV2 hentBeregningsgrunnlag(List<BehandlingResourceLink> resourceLinker) {
        return hentFormidlingBeregningsgrunnlagHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente beregningsgrunnlag for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    public Optional<BeregningsgrunnlagDtoV2> hentFormidlingBeregningsgrunnlagHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "beregningsgrunnlag".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, BeregningsgrunnlagDtoV2.class));
    }

    public List<VilkårDto> hentVilkår(List<BehandlingResourceLink> resourceLinker) {
        return Arrays.asList(resourceLinker.stream()
                .filter(dto -> "vilkar".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, VilkårDto[].class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente vilkår for behandling: " + hentBehandlingId(resourceLinker));
                }));
    }

    public UttakResultatPerioderDto hentUttaksresultat(List<BehandlingResourceLink> resourceLinker) {
        return hentUttaksresultatHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente uttaksperioder for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    public Optional<UttakResultatPerioderDto> hentUttaksresultatHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttaksresultat-perioder-formidling".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, UttakResultatPerioderDto.class));

    }

    public SvangerskapspengerUttakResultatDto hentUttaksresultatSvp(List<BehandlingResourceLink> resourceLinker) {
        return hentUttaksresultatSvpHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException("Klarte ikke hente uttaksresultat for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    public Optional<SvangerskapspengerUttakResultatDto> hentUttaksresultatSvpHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttaksresultat-svangerskapspenger".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SvangerskapspengerUttakResultatDto.class));
    }


    public FagsakDto hentFagsak(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "fagsak".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, FagsakDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente fagsak for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public YtelseFordelingDto hentYtelseFordeling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "ytelsefordeling".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, YtelseFordelingDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente ytelsefordeling for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public SaldoerDto hentSaldoer(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "uttak-stonadskontoer".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, SaldoerDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente uttakssaldoer for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public List<AksjonspunktDto> hentAksjonspunkter(List<BehandlingResourceLink> resourceLinker) {
        return Arrays.asList(resourceLinker.stream()
                .filter(dto -> "aksjonspunkter".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, AksjonspunktDto[].class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente vilkår for behandling: " + hentBehandlingId(resourceLinker));
                }));
    }

    public MottattKlagedokumentDto hentKlagedokument(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "mottatt-klagedokument".equals(dto.getRel()))
                .findFirst().flatMap(link -> hentDtoFraLink(link, MottattKlagedokumentDto.class))
                .orElseThrow(() -> {
                    throw new IllegalStateException("Klarte ikke hente klagedokument for behandling: " + hentBehandlingId(resourceLinker));
                });
    }

    public List<MottattDokumentDto> hentMottatteDokumenter(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker.stream()
                .filter(dto -> "mottattdokument".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, MottattDokumentDto[].class))
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

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
                    uriBuilder.addParameter(BEHANDLING_ID, String.valueOf(payload.getBehandlingId()));
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

    public String getJsonTestdata() {
        return null;
    }
}

