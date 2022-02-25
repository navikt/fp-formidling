package no.nav.foreldrepenger.fpsak.mapper;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.vilkår.Vilkår;
import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;

public class VilkårDtoMapper {

    public static List<Vilkår> mapVilkårFraDto(List<VilkårDto> dto) {
        List<Vilkår> vilkårList = new ArrayList<>();
        for (VilkårDto vilkårDto : dto) {
            Vilkår vilkår = new Vilkår(vilkårDto.vilkarType());
            vilkårList.add(vilkår);
        }
        return vilkårList;
    }

}
