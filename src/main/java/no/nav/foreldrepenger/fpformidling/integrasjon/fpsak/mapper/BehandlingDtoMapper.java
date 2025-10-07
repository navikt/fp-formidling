package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.Collections;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.Fagsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingÅrsakDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;

public final class BehandlingDtoMapper {

    private BehandlingDtoMapper() {
    }

    private static BehandlingResourceLink mapResourceLinkFraDto(BehandlingResourceLinkDto dto) {
        var linkBuilder = BehandlingResourceLink.ny().medHref(dto.getHref()).medRel(dto.getRel()).medType(dto.getType());
        if (dto.getRequestPayload() != null) {
            linkBuilder.medRequestPayload(
                new BehandlingRelLinkPayload(dto.getRequestPayload().getSaksnummer(), dto.getRequestPayload().getBehandlingUuid()));
        }
        return linkBuilder.build();
    }

    public static Behandling mapBehandlingFraDto(BehandlingDto dto, FagsakDto fagsakDto) {
        var builder = Behandling.builder();
        mapLinks(dto.getLinks()).forEach(builder::leggTilResourceLink);
        mapLinks(dto.getFormidlingRessurser()).forEach(builder::leggTilFormidlingResourceLink);
        builder.medUuid(dto.getUuid())
            .medBehandlingType(dto.getType())
            .medStatus(dto.getStatus())
            .medOpprettetDato(dto.getOpprettet())
            .medAvsluttet(dto.getAvsluttet())
            .medToTrinnsBehandling(dto.getToTrinnsBehandling())
            .medBehandlendeEnhetId(dto.getBehandlendeEnhetId())
            .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingÅrsaker()))
            .medSpråkkode(dto.getSprakkode())
            .medMedlemskapOpphørsårsak(dto.getMedlemskapOpphørsårsak())
            .medMedlemskapFom(dto.getMedlemskapFom())
            .medVilkår(VilkårDtoMapper.mapVilkårFraDto(dto.getVilkår()))
            .medOriginalBehandlingUuid(dto.getOriginalBehandlingUuid())
            .medRettigheter(dto.getRettigheter())
            .medFagsak(mapFagsak(fagsakDto))
            .medFamilieHendelse(dto.getFamilieHendelse());

        if (dto.getBehandlingsresultat() != null) {
            builder.medBehandlingsresultat(BehandlingsresultatDtoMapper.mapBehandlingsresultatFraDto(dto.getBehandlingsresultat()));
        }

        return builder.build();
    }

    public static List<BehandlingResourceLink> mapLinks(List<BehandlingResourceLinkDto> links) {
        return links.stream().map(BehandlingDtoMapper::mapResourceLinkFraDto).toList();
    }

    private static Fagsak mapFagsak(FagsakDto fagsakDto) {
        return Fagsak.ny()
            .medSaksnummer(fagsakDto.saksnummer())
            .medYtelseType(fagsakDto.fagsakYtelseType())
            .medBrukerRolle(fagsakDto.relasjonsRolleType())
            .medAktørId(new AktørId(fagsakDto.aktørId()))
            .medDekningsgrad(fagsakDto.dekningsgrad())
            .build();
    }

    private static List<BehandlingÅrsak> mapBehandlingÅrsakListe(List<BehandlingÅrsakDto> behandlingÅrsakDtoer) {
        if (!behandlingÅrsakDtoer.isEmpty()) {
            return behandlingÅrsakDtoer.stream().map(BehandlingDtoMapper::mapBehandlingÅrsakFraDto).toList();
        }
        return Collections.emptyList();
    }

    private static BehandlingÅrsak mapBehandlingÅrsakFraDto(BehandlingÅrsakDto dto) {
        return BehandlingÅrsak.builder().medBehandlingÅrsakType(dto.getBehandlingArsakType()).build();
    }

}
