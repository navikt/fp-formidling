package no.nav.foreldrepenger.melding.uttak.svp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


@Entity(name ="PeriodeIkkeOppfyltÅrsak")
@DiscriminatorValue(PeriodeIkkeOppfyltÅrsak.DISCRIMINATOR)
public class PeriodeIkkeOppfyltÅrsak extends Kodeliste implements ÅrsakMedLovReferanse {

    public static final String DISCRIMINATOR = "SVP_PERIODE_IKKE_OPPFYLT_AARSAK";

    public static final PeriodeIkkeOppfyltÅrsak INGEN = new PeriodeIkkeOppfyltÅrsak("-");
    public static final PeriodeIkkeOppfyltÅrsak BARNET_ER_DØD = new PeriodeIkkeOppfyltÅrsak("4072");
    public static final PeriodeIkkeOppfyltÅrsak SØKER_ER_DØD = new PeriodeIkkeOppfyltÅrsak("4071");
    //Vilkår
    public static final PeriodeIkkeOppfyltÅrsak OPPHØR_MEDLEMSKAP = new PeriodeIkkeOppfyltÅrsak("4087");
    public static final PeriodeIkkeOppfyltÅrsak SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT = new PeriodeIkkeOppfyltÅrsak("4098");
    public static final PeriodeIkkeOppfyltÅrsak OPPTJENINGSVILKÅRET_IKKE_OPPFYLT = new PeriodeIkkeOppfyltÅrsak("4099");

    public static final PeriodeIkkeOppfyltÅrsak SØKT_FOR_SENT = new PeriodeIkkeOppfyltÅrsak("8308");
    public static final PeriodeIkkeOppfyltÅrsak PERIODE_SAMTIDIG_SOM_FERIE = new PeriodeIkkeOppfyltÅrsak("8311");

    PeriodeIkkeOppfyltÅrsak() {
        //For Hibernate
    }

    PeriodeIkkeOppfyltÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public static Set<PeriodeIkkeOppfyltÅrsak> opphørsAvslagÅrsaker() {
        // TODO: Bygg et riktig set av avslagsårsaker her
        return new HashSet<>(Arrays.asList(
                BARNET_ER_DØD,
                SØKER_ER_DØD,
                OPPHØR_MEDLEMSKAP,
                SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT,
                OPPTJENINGSVILKÅRET_IKKE_OPPFYLT,
                SØKT_FOR_SENT,
                PERIODE_SAMTIDIG_SOM_FERIE
        ));
    }

    @Override
    public String getLovHjemmelData() {
        return getEkstraData();
    }
}
