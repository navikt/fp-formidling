package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.vilkår.Vilkår;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.vilkår.VilkårDto;

public final class VilkårDtoMapper {

    private VilkårDtoMapper() {
    }

    public static List<Vilkår> mapVilkårFraDto(List<VilkårDto> dto) {
        return dto.stream().map(VilkårDto::vilkarType).map(Vilkår::new).toList();
    }

}
