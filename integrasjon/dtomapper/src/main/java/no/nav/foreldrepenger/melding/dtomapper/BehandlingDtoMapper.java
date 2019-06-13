package no.nav.foreldrepenger.melding.dtomapper;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.melding.behandling.BehandlingStatus;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class BehandlingDtoMapper {
    private KodeverkRepository kodeverkRepository;
    private BehandlingRestKlient behandlingRestKlient;
    private FagsakDtoMapper fagsakDtoMapper;
    private BehandlingsresultatDtoMapper behandlingsresultatDtoMapper;

    public BehandlingDtoMapper() {
        //CDI
    }

    @Inject
    public BehandlingDtoMapper(KodeverkRepository kodeverkRepository,
                               BehandlingRestKlient behandlingRestKlient,
                               FagsakDtoMapper fagsakDtoMapper,
                               BehandlingsresultatDtoMapper behandlingsresultatDtoMapper) {
        this.kodeverkRepository = kodeverkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
        this.fagsakDtoMapper = fagsakDtoMapper;
        this.behandlingsresultatDtoMapper = behandlingsresultatDtoMapper;
    }

    private static BehandlingResourceLink mapResourceLinkFraDto(BehandlingResourceLinkDto dto) {
        BehandlingResourceLink.Builder linkBuilder = BehandlingResourceLink.ny()
                .medHref(dto.getHref())
                .medRel(dto.getRel())
                .medType(dto.getType());
        if (dto.getRequestPayload() != null) {
            linkBuilder.medRequestPayload(
                    new BehandlingRelLinkPayload(dto.getRequestPayload().getSaksnummer(),
                            dto.getRequestPayload().getBehandlingId(),
                            dto.getRequestPayload().getBehandlingUuid()));
        }
        return linkBuilder
                .build();
    }

    /**
     * Vi får en forenklet versjon av dtoen når vi kaller på den via revurdering..
     */
    public Behandling mapOriginalBehandlingFraDto(BehandlingDto dto) {
        return mapBehandlingFraDto(behandlingRestKlient.hentBehandling(new BehandlingIdDto(dto.getId()))); //TODO - Endre til UUID..
    }

    public Behandling mapBehandlingFraDto(BehandlingDto dto) {
        Behandling.Builder builder = Behandling.builder();
        Supplier<Stream<BehandlingResourceLink>> behandlingResourceLinkStreamSupplier = () -> dto.getLinks().stream().map(BehandlingDtoMapper::mapResourceLinkFraDto);
        behandlingResourceLinkStreamSupplier.get().forEach(builder::leggTilResourceLink);
        List<BehandlingResourceLink> linkListe = behandlingResourceLinkStreamSupplier.get().collect(Collectors.toList());
        Fagsak fagsak = fagsakDtoMapper.mapFagsakFraDto(behandlingRestKlient.hentFagsak(linkListe));
        Behandling originalBehandling = behandlingRestKlient.hentOriginalBehandling(linkListe).map(this::mapOriginalBehandlingFraDto).orElse(null);
        builder.medId(dto.getId())
                .medUuid(dto.getUuid())
                .medBehandlingType(finnBehandlingType(dto.getType().getKode()))
                .medStatus(kodeverkRepository.finn(BehandlingStatus.class, dto.getStatus().getKode()))
                .medOpprettetDato(dto.getOpprettet())
                .medAvsluttet(dto.getAvsluttet())
                .medOriginalBehandling(originalBehandling)
                .medAnsvarligSaksbehandler(dto.getAnsvarligSaksbehandler())
                .medAnsvarligBeslutter(dto.getAnsvarligBeslutter())
                .medToTrinnsBehandling(dto.getToTrinnsBehandling())
                .medBehandlendeEnhetNavn(dto.getBehandlendeEnhetNavn())
                .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingArsaker()))
                .medFagsak(fagsak)
                .medEndretAv(dto.getEndretAvBrukernavn())
                .medSpråkkode(kodeverkRepository.finn(Språkkode.class, dto.getSprakkode().getKode()));
        if (dto.getBehandlingsresultat() != null) {
            builder.medBehandlingsresultat(behandlingsresultatDtoMapper.mapBehandlingsresultatFraDto(dto.getBehandlingsresultat()));
        }

        return builder.build();
    }

    private List<BehandlingÅrsak> mapBehandlingÅrsakListe(List<BehandlingÅrsakDto> behandlingÅrsakDtoer) {
        if (!behandlingÅrsakDtoer.isEmpty()) {
            return behandlingÅrsakDtoer.stream()
                    .map(this::mapBehandlingÅrsakFraDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private BehandlingÅrsak mapBehandlingÅrsakFraDto(BehandlingÅrsakDto dto) {
        return BehandlingÅrsak.builder()
                .medBehandlingÅrsakType(dto.getBehandlingArsakType().getKode())
                .medManueltOpprettet(dto.getManueltOpprettet())
                .build();
    }

    private BehandlingType finnBehandlingType(String behandlingType) {
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }
}
