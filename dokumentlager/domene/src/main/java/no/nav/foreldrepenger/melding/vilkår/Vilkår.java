package no.nav.foreldrepenger.melding.vilkår;

public class Vilkår {
    private VilkårType vilkårType;

    public Vilkår(VilkårType vilkårType) {
        this.vilkårType = vilkårType;
    }

    public VilkårType getVilkårType() {
        return vilkårType;
    }
}
