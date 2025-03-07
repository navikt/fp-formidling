package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import static no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat.builder;

import java.util.ArrayList;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingsresultatDto;

public final class BehandlingsresultatDtoMapper {

    private BehandlingsresultatDtoMapper() {
    }

    public static Behandlingsresultat mapBehandlingsresultatFraDto(BehandlingsresultatDto dto) {
        var builder = builder();
        if (dto.getAvslagsarsak() != null) {
            builder.medAvslagsårsak(dto.getAvslagsarsak());
        }
        if (dto.getType() != null) {
            builder.medBehandlingResultatType(dto.getType());
        }
        return builder
                .medOverskrift(dto.getOverskrift())
                .medFritekstbrev(dto.getFritekstbrev())
                .medFritektsbrevHtml(dto.getFritekstbrevHtml())
                .medAvslagarsakFritekst(dto.getAvslagsarsakFritekst())
                .medKonsekvenserForYtelsen(new ArrayList<>(dto.getKonsekvenserForYtelsen()))
                .medSkjæringstidspunkt(dto.getSkjæringstidspunkt())
                .medUtenMinsterett(dto.utenMinsterett())
                .medEndretDekningsgrad(dto.endretDekningsgrad())
                .medOpphørsdato(dto.getOpphørsdato())
                .build();
    }
}
