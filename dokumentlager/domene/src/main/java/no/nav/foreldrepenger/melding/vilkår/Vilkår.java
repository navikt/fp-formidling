package no.nav.foreldrepenger.melding.vilkår;

import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;

public class Vilkår {
    private String vilkårType; // Kodeliste.VilkårType

    public Vilkår(VilkårDto dto) {
        this.vilkårType = dto.getVilkarType().kode;
    }

    public String getVilkårType() {
        return vilkårType;
    }
}
