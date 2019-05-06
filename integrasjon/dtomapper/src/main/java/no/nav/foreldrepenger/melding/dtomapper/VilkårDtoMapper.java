package no.nav.foreldrepenger.melding.dtomapper;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

@ApplicationScoped
public class VilkårDtoMapper {

    private KodeverkRepository kodeverkRepository;

    @Inject
    public VilkårDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public VilkårDtoMapper() {
        //CDI
    }

    public List<Vilkår> mapVilkårFraDto(List<VilkårDto> dto) {
        List<Vilkår> vilkårList = new ArrayList<>();
        for (VilkårDto vilkårDto : dto) {
            Vilkår vilkår = new Vilkår(kodeverkRepository.finn(VilkårType.class, vilkårDto.getVilkarType().getKode()));
            vilkårList.add(vilkår);
        }
        return vilkårList;
    }

}
