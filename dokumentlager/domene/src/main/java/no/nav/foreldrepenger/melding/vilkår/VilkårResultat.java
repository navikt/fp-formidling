package no.nav.foreldrepenger.melding.vilkår;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VilkårResultat {
    private Set<Vilkår> vilkårne = new LinkedHashSet<>();

    public List<Vilkår> getVilkårene() {
        return Collections.unmodifiableList(new ArrayList<>(vilkårne));
    }
}
