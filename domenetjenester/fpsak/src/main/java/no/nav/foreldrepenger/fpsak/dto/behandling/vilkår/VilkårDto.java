package no.nav.foreldrepenger.fpsak.dto.behandling.vilkår;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class VilkårDto {

    private KodeDto vilkarType;
    public VilkårDto(KodeDto vilkårType) {
        this.vilkarType = vilkårType;
    }

    public VilkårDto() {
    }

    public KodeDto getVilkarType() {
        return vilkarType;
    }

    public void setVilkarType(KodeDto vilkarType) {
        this.vilkarType = vilkarType;
    }
}

