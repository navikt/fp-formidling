package no.nav.foreldrepenger.melding.datamapper.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingRelLinkPayloadDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
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

    private static Long finnSaksnummer(BehandlingDto dto) {
        return dto.getLinks().stream()
                .filter(Objects::nonNull)
                .map(BehandlingResourceLinkDto::getRequestPayload)
                .filter(Objects::nonNull)
                .map(BehandlingRelLinkPayloadDto::getSaksnummer)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    public Behandling mapBehandlingFraDto(BehandlingDto dto) {
        Fagsak fagsak = fagsakDtoMapper.mapFagsakFraDto(behandlingRestKlient.hentFagsak(dto.getLinks()));
        Behandling.Builder builder = Behandling.builder();
        builder.medId(dto.getId())
                .medBehandlingType(finnBehandlingType(dto.getType().getKode()))
                .medOpprettetDato(dto.getOpprettet())
                .medOriginalBehandling(dto.getOriginalBehandlingId())
                .medAnsvarligSaksbehandler(dto.getAnsvarligSaksbehandler())
                .medToTrinnsBehandling(dto.getToTrinnsBehandling())
                .medBehandlendeEnhetNavn(dto.getBehandlendeEnhetNavn())
                .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingArsaker()))
                .medPersonopplysning(new Personopplysning(dto.getPersonopplysningDto(), kodeverkRepository))
                .medFagsak(fagsak);

        dto.getLinks().forEach(builder::leggTilResourceLink);

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
                .medBehandlingÅrsakType(kodeverkRepository.finn(BehandlingÅrsakType.class, dto.getBehandlingArsakType().getKode()))
                .medManueltOpprettet(dto.getManueltOpprettet())
                .build();
    }

    private BehandlingType finnBehandlingType(String behandlingType) {
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }
}
