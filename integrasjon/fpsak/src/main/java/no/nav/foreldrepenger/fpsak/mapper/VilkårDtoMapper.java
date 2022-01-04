package no.nav.foreldrepenger.fpsak.mapper;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

public class VilkårDtoMapper {

    public static List<Vilkår> mapVilkårFraDto(List<VilkårDto> dto) {
        List<Vilkår> vilkårList = new ArrayList<>();
        for (VilkårDto vilkårDto : dto) {
            Vilkår vilkår = new Vilkår(VilkårType.fraKode(vilkårDto.getVilkarType().getKode()));
            vilkårList.add(vilkår);
        }
        return vilkårList;
    }

}
