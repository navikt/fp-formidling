package no.nav.foreldrepenger.fpsak.mapper;

import static no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.vedtak.Vedtaksbrev;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingsresultatDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class BehandlingsresultatDtoMapper {


    public static Behandlingsresultat mapBehandlingsresultatFraDto(BehandlingsresultatDto dto) {
        Behandlingsresultat.Builder builder = builder();
        if (dto.getAvslagsarsak() != null) {
            builder.medAvslagsårsak(Avslagsårsak.fraKode(dto.getAvslagsarsak().getKode()));
        }
        if (dto.getType() != null) {
            builder.medBehandlingResultatType(BehandlingResultatType.fraKode(dto.getType().getKode()));
        }
        builder.medFritekstbrev(dto.getFritekstbrev())
                .medOverskrift(dto.getOverskrift())
                .medVedtaksbrev(Vedtaksbrev.fraKode(dto.getVedtaksbrev().getKode()))
                .medAvslagarsakFritekst(dto.getAvslagsarsakFritekst());
        List<KonsekvensForYtelsen> konsekvenserForYtelsen = new ArrayList<>();
        for (KodeDto kodeDto : dto.getKonsekvenserForYtelsen()) {
            konsekvenserForYtelsen.add(KonsekvensForYtelsen.fraKode(kodeDto.getKode()));
        }
        builder.medKonsekvenserForYtelsen(konsekvenserForYtelsen);
        builder.medErRevurderingMedUendretUtfall(dto.getErRevurderingMedUendretUtfall());
        builder.medSkjæringstidspunkt(Optional.ofNullable(dto.getSkjæringstidspunkt()));
        return builder.build();
    }
}
