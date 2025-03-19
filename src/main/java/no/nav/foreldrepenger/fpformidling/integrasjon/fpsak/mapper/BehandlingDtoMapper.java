package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.verge.Verge;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingResourceLinkDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
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

    public static Behandling mapBehandlingFraDto(BehandlingDto dto) {
        var builder = Behandling.builder();
        var behandlingResourceLinkStreamSupplier = (Supplier<Stream<BehandlingResourceLink>>) () -> dto.getLinks()
            .stream()
            .map(BehandlingDtoMapper::mapResourceLinkFraDto);
        behandlingResourceLinkStreamSupplier.get().forEach(builder::leggTilResourceLink);
        builder.medUuid(dto.getUuid())
            .medBehandlingType(dto.getType())
            .medStatus(dto.getStatus())
            .medOpprettet(dto.getOpprettet())
            .medAvsluttet(dto.getAvsluttet())
            .medToTrinnsBehandling(dto.getToTrinnsBehandling())
            .medBehandlendeEnhetId(dto.getBehandlendeEnhetId())
            .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingÅrsaker()))
            .medSpråkkode(dto.getSprakkode())
            .medHarAvklartAnnenForelderRett(dto.getHarAvklartAnnenForelderRett())
            .medMedlemskapOpphørsårsak(dto.getMedlemskapOpphørsårsak())
            .medMedlemskapFom(dto.getMedlemskapFom())
            .medVilkår(VilkårDtoMapper.mapVilkårFraDto(dto.getVilkår()))
            .medOriginalBehandlingUuid(dto.getOriginalBehandlingUuid());

        if (dto.getBehandlingsresultat() != null) {
            builder.medBehandlingsresultat(BehandlingsresultatDtoMapper.mapBehandlingsresultatFraDto(dto.getBehandlingsresultat()));
        }

        if (dto.fagsak() != null) {
            var fagsakDto = dto.fagsak();
            var fagsak = FagsakBackend.ny()
                .medSaksnummer(fagsakDto.saksnummer())
                .medFagsakYtelseType(fagsakDto.fagsakYtelseType())
                .medBrukerRolle(fagsakDto.relasjonsRolleType())
                .medAktørId(new AktørId(fagsakDto.aktørId()))
                .medDekningsgrad(fagsakDto.dekningsgrad())
                .build();
            builder.medFagsak(fagsak);
        }

        if (dto.verge() != null) {
            var vergeDto = dto.verge();
            var verge = new Verge(vergeDto.aktoerId(), vergeDto.organisasjonsnummer(), vergeDto.navn(), vergeDto.gyldigFom(), vergeDto.gyldigTom());
            builder.medVerge(verge);
        }

        return builder.build();
    }

    private static List<BehandlingÅrsak> mapBehandlingÅrsakListe(List<BehandlingÅrsakType> behandlingÅrsakTyper) {
        if (!behandlingÅrsakTyper.isEmpty()) {
            return behandlingÅrsakTyper.stream().map(BehandlingDtoMapper::mapBehandlingÅrsakFraDto).toList();
        }
        return Collections.emptyList();
    }

    private static BehandlingÅrsak mapBehandlingÅrsakFraDto(BehandlingÅrsakType årsakType) {
        return BehandlingÅrsak.builder().medBehandlingÅrsakType(årsakType).build();
    }

}
