package no.nav.foreldrepenger.melding.vilkår.repository;

import java.util.List;

import no.nav.foreldrepenger.melding.vilkår.VilkårType;

public interface VilkårKodeverkRepository {
    List<VilkårType> finnVilkårTypeListe(String avslagsårsakKode);
}
