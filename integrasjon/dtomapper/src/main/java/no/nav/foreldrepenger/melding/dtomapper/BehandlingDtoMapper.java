package no.nav.foreldrepenger.melding.dtomapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingRelLinkPayloadDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.personopplysning.Personopplysning;

@ApplicationScoped
public class BehandlingDtoMapper {
    private KodeverkRepository kodeverkRepository;
    private BehandlingRestKlient behandlingRestKlient;
    private FagsakDtoMapper fagsakDtoMapper;
    private BehandlingsresultatDtoMapper behandlingsresultatDtoMapper;
    private PersonopplysningDtoMapper personopplysningDtoMapper;

    public BehandlingDtoMapper() {
        //CDI
    }

    @Inject
    public BehandlingDtoMapper(KodeverkRepository kodeverkRepository,
                               BehandlingRestKlient behandlingRestKlient,
                               FagsakDtoMapper fagsakDtoMapper,
                               BehandlingsresultatDtoMapper behandlingsresultatDtoMapper,
                               PersonopplysningDtoMapper personopplysningDtoMapper) {
        this.kodeverkRepository = kodeverkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
        this.fagsakDtoMapper = fagsakDtoMapper;
        this.behandlingsresultatDtoMapper = behandlingsresultatDtoMapper;
        this.personopplysningDtoMapper = personopplysningDtoMapper;
    }

    private static Long finnSaksnummer(BehandlingDto dto) {
        return dto.getLinks().stream()
                .filter(Objects::nonNull)
                .map(BehandlingResourceLinkDto::getRequestPayload)
                .filter(Objects::nonNull)
                .map(BehandlingRelLinkPayloadDto::getSaksnummer)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    private static BehandlingResourceLink mapResourceLinkFraDto(BehandlingResourceLinkDto dto) {
        BehandlingResourceLink.Builder linkBuilder = BehandlingResourceLink.ny()
                .medHref(dto.getHref())
                .medRel(dto.getRel())
                .medType(dto.getType());
        if (dto.getRequestPayload() != null) {
            linkBuilder.medRequestPayload(new BehandlingRelLinkPayload(dto.getRequestPayload().getSaksnummer(), dto.getRequestPayload().getBehandlingId()));
        }
        return linkBuilder
                .build();
    }

    public Behandling mapBehandlingFraDto(BehandlingDto dto) {
        Behandling.Builder builder = Behandling.builder();
        Supplier<Stream<BehandlingResourceLink>> behandlingResourceLinkStreamSupplier = () -> dto.getLinks().stream().map(BehandlingDtoMapper::mapResourceLinkFraDto);
        behandlingResourceLinkStreamSupplier.get().forEach(builder::leggTilResourceLink);
        Fagsak fagsak = fagsakDtoMapper.mapFagsakFraDto(behandlingRestKlient.hentFagsak(behandlingResourceLinkStreamSupplier.get().collect(Collectors.toList())));
        builder.medId(dto.getId())
                .medBehandlingType(finnBehandlingType(dto.getType().getKode()))
                .medOpprettetDato(dto.getOpprettet())
                .medOriginalBehandling(dto.getOriginalBehandlingId())
                .medAnsvarligSaksbehandler(dto.getAnsvarligSaksbehandler())
                .medToTrinnsBehandling(dto.getToTrinnsBehandling())
                .medBehandlendeEnhetNavn(dto.getBehandlendeEnhetNavn())
                .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingArsaker()))
                .medPersonopplysning(hentPersonopplysning(dto))
                .medFagsak(fagsak);


        if (dto.getBehandlingsresultat() != null) {
            builder.medBehandlingsresultat(behandlingsresultatDtoMapper.mapBehandlingsresultatFraDto(dto.getBehandlingsresultat()));
        }

        return builder.build();
    }

    private Personopplysning hentPersonopplysning(BehandlingDto dto) {
        if (BehandlingType.FØRSTEGANGSSØKNAD.getKode().equals(dto.getType().kode)) {
            return personopplysningDtoMapper.mapPersonopplysningFraDto(
                    behandlingRestKlient.hentPersonopplysninger(mapOgSamleLenkerFraDto(dto)));
        }
        if (dto.getOriginalBehandlingId() == null) {
            throw new IllegalStateException();
        }
        BehandlingDto originalBehandlingDto = behandlingRestKlient.hentBehandling(new BehandlingIdDto(dto.getOriginalBehandlingId()));
        return personopplysningDtoMapper.mapPersonopplysningFraDto(behandlingRestKlient.hentPersonopplysninger(mapOgSamleLenkerFraDto(originalBehandlingDto)));
    }

    private List<BehandlingResourceLink> mapOgSamleLenkerFraDto(BehandlingDto dto) {
        return dto.getLinks().stream().map(BehandlingDtoMapper::mapResourceLinkFraDto).collect(Collectors.toList());
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
                .medBehandlingÅrsakType(kodeverkRepository.finn(BehandlingÅrsakType.class, dto.getBehandlingArsakType().getKode()))
                .medManueltOpprettet(dto.getManueltOpprettet())
                .build();
    }

    private BehandlingType finnBehandlingType(String behandlingType) {
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }
}
