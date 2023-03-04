package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BehandlingsresultatDto;

import java.util.ArrayList;
import java.util.List;

import static no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat.builder;

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
            .medVedtaksbrev(dto.getVedtaksbrev())
            .medAvslagarsakFritekst(dto.getAvslagsarsakFritekst());
        List<KonsekvensForYtelsen> konsekvenserForYtelsen = new ArrayList<>(dto.getKonsekvenserForYtelsen());

        builder.medKonsekvenserForYtelsen(konsekvenserForYtelsen);
        builder.medErRevurderingMedUendretUtfall(dto.getErRevurderingMedUendretUtfall());
        builder.medSkjæringstidspunkt(dto.getSkjæringstidspunkt());
        builder.medUtenMinsterett(dto.utenMinsterett());
        return builder.build();
    }
}
