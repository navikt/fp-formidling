package no.nav.foreldrepenger.melding.vilkår;

import java.util.List;

public record VilkårResultat(List<Vilkår> vilkårne) {

    public List<Vilkår> getVilkårene() {
        return vilkårne();
    }
}
