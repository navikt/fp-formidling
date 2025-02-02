package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import static no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat.builder;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
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
        builder.medFritekstbrev(dto.getFritekstbrev())
            .medOverskrift(dto.getOverskrift())
            .medAvslagarsakFritekst(dto.getAvslagsarsakFritekst());
        List<KonsekvensForYtelsen> konsekvenserForYtelsen = new ArrayList<>(dto.getKonsekvenserForYtelsen());

        builder.medKonsekvenserForYtelsen(konsekvenserForYtelsen);
        builder.medSkjæringstidspunkt(dto.getSkjæringstidspunkt());
        builder.medUtenMinsterett(dto.utenMinsterett());
        builder.medEndretDekningsgrad(dto.endretDekningsgrad());
        builder.medOpphørsdato(dto.getOpphørsdato());
        return builder.build();
    }
}
