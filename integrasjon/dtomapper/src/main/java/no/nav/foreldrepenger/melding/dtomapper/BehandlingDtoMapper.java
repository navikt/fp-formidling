package no.nav.foreldrepenger.melding.dtomapper;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.melding.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.melding.behandling.BehandlingStatus;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;

public class BehandlingDtoMapper {

    private static BehandlingResourceLink mapResourceLinkFraDto(BehandlingResourceLinkDto dto) {
        BehandlingResourceLink.Builder linkBuilder = BehandlingResourceLink.ny()
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
        Behandling.Builder builder = Behandling.builder();
        Supplier<Stream<BehandlingResourceLink>> behandlingResourceLinkStreamSupplier = () -> dto.getLinks().stream().map(BehandlingDtoMapper::mapResourceLinkFraDto);
        Supplier<Stream<BehandlingResourceLink>> behandlingFormidlingResourceLinkStreamSupplier = () -> dto.getFormidlingRessurser().stream().map(BehandlingDtoMapper::mapResourceLinkFraDto);
        behandlingResourceLinkStreamSupplier.get().forEach(builder::leggTilResourceLink);
        behandlingFormidlingResourceLinkStreamSupplier.get().forEach(builder::leggTilFormidlingResourceLink);
        builder.medUuid(dto.getUuid())
                .medBehandlingType(finnBehandlingType(dto.getType().getKode()))
                .medStatus(BehandlingStatus.fraKode(dto.getStatus().getKode()))
                .medOpprettetDato(dto.getOpprettet())
                .medAvsluttet(dto.getAvsluttet())
                .medAnsvarligSaksbehandler(dto.getAnsvarligSaksbehandler())
                .medAnsvarligBeslutter(dto.getAnsvarligBeslutter())
                .medToTrinnsBehandling(dto.getToTrinnsBehandling())
                .medBehandlendeEnhetNavn(dto.getBehandlendeEnhetNavn())
                .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingÅrsaker()))
                .medEndretAv(dto.getEndretAvBrukernavn())
                .medSpråkkode(Språkkode.defaultNorsk(dto.getSprakkode().getKode()))
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
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static BehandlingÅrsak mapBehandlingÅrsakFraDto(BehandlingÅrsakDto dto) {
        return BehandlingÅrsak.builder()
                .medBehandlingÅrsakType(BehandlingÅrsakType.fraKodeDefaultUdefinert(dto.getBehandlingArsakType().getKode()))
                .medManueltOpprettet(dto.getManueltOpprettet())
                .build();
    }

    private static BehandlingType finnBehandlingType(String behandlingType) {
        return BehandlingType.fraKode(behandlingType);
    }
}
