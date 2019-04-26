package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

@ApplicationScoped
public class VilkårDtoMapper {

    private KodeverkRepository kodeverkRepository;

    public VilkårDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public VilkårDtoMapper() {
        //CDI
    }

    public Vilkår mapVilkårFraDto(VilkårDto dto) {
        return new Vilkår(kodeverkRepository.finn(VilkårType.class, dto.getVilkarType().getKode()));
    }

}
