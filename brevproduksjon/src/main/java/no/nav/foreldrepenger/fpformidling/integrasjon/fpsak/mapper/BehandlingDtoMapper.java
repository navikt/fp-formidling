package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingÅrsakDto;

public final class BehandlingDtoMapper {

    private BehandlingDtoMapper() {
    }

    private static BehandlingResourceLink mapResourceLinkFraDto(BehandlingResourceLinkDto dto) {
        var linkBuilder = BehandlingResourceLink.ny()
                .medHref(dto.getHref())
                .medRel(dto.getRel())
                .medType(dto.getType());
        if (dto.getRequestPayload() != null) {
            linkBuilder.medRequestPayload(
                    new BehandlingRelLinkPayload(dto.getRequestPayload().getSaksnummer(),
                            dto.getRequestPayload().getBehandlingUuid()));
        }
        return linkBuilder
                .build();
    }

    public static Behandling mapBehandlingFraDto(BehandlingDto dto) {
        var builder = Behandling.builder();
        var behandlingResourceLinkStreamSupplier = (Supplier<Stream<BehandlingResourceLink>>) () -> dto.getLinks().stream().map(BehandlingDtoMapper::mapResourceLinkFraDto);
        var behandlingFormidlingResourceLinkStreamSupplier = (Supplier<Stream<BehandlingResourceLink>>) () -> dto.getFormidlingRessurser().stream().map(BehandlingDtoMapper::mapResourceLinkFraDto);
        behandlingResourceLinkStreamSupplier.get().forEach(builder::leggTilResourceLink);
        behandlingFormidlingResourceLinkStreamSupplier.get().forEach(builder::leggTilFormidlingResourceLink);
        builder.medUuid(dto.getUuid())
                .medBehandlingType(dto.getType())
                .medStatus(dto.getStatus())
                .medOpprettetDato(dto.getOpprettet())
                .medAvsluttet(dto.getAvsluttet())
                .medAnsvarligSaksbehandler(dto.getAnsvarligSaksbehandler())
                .medAnsvarligBeslutter(dto.getAnsvarligBeslutter())
                .medToTrinnsBehandling(dto.getToTrinnsBehandling())
                .medBehandlendeEnhetId(dto.getBehandlendeEnhetId())
                .medBehandlendeEnhetNavn(dto.getBehandlendeEnhetNavn())
                .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingÅrsaker()))
                .medEndretAv(dto.getEndretAvBrukernavn())
                .medSpråkkode(dto.getSprakkode())
                .medHarAvklartAnnenForelderRett(dto.getHarAvklartAnnenForelderRett())
                .medVilkår(VilkårDtoMapper.mapVilkårFraDto(dto.getVilkår()))
                .medOriginalBehandlingUuid(dto.getOriginalBehandlingUuid())
                .medOriginalVedtaksDato(dto.getOriginalVedtaksDato());
        if (dto.getBehandlingsresultat() != null) {
            builder.medBehandlingsresultat(BehandlingsresultatDtoMapper.mapBehandlingsresultatFraDto(dto.getBehandlingsresultat()));
        }

        return builder.build();
    }

    private static List<BehandlingÅrsak> mapBehandlingÅrsakListe(List<BehandlingÅrsakDto> behandlingÅrsakDtoer) {
        if (!behandlingÅrsakDtoer.isEmpty()) {
            return behandlingÅrsakDtoer.stream()
                    .map(BehandlingDtoMapper::mapBehandlingÅrsakFraDto)
                    .toList();
        }
        return Collections.emptyList();
    }

    private static BehandlingÅrsak mapBehandlingÅrsakFraDto(BehandlingÅrsakDto dto) {
        return BehandlingÅrsak.builder()
                .medBehandlingÅrsakType(dto.getBehandlingArsakType())
                .medManueltOpprettet(dto.getManueltOpprettet())
                .build();
    }

}
