package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.fpformidling.vilkår.Vilkår;

public final class VilkårDtoMapper {

    private VilkårDtoMapper() {
    }

    public static List<Vilkår> mapVilkårFraDto(List<VilkårDto> dto) {
        List<Vilkår> vilkårList = new ArrayList<>();
        for (var vilkårDto : dto) {
            var vilkår = new Vilkår(vilkårDto.vilkarType());
            vilkårList.add(vilkår);
        }
        return vilkårList;
    }

}
