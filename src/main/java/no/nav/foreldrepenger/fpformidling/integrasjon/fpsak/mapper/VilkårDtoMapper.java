package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.vilkår.Vilkår;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;

public final class VilkårDtoMapper {

    private VilkårDtoMapper() {
    }

    public static List<Vilkår> mapVilkårFraDto(List<VilkårType> dto) {
        return dto.stream().map(Vilkår::new).toList();
    }

}
