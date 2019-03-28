package no.nav.foreldrepenger.melding.uttak;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "InnvilgetÅrsak")
@DiscriminatorValue(InnvilgetÅrsak.DISCRIMINATOR)
public class InnvilgetÅrsak extends PeriodeResultatÅrsak {
    public static final String DISCRIMINATOR = "INNVILGET_AARSAK";
    // Uttak årsaker
    public static final InnvilgetÅrsak FELLESPERIODE_ELLER_FORELDREPENGER = new InnvilgetÅrsak("2002");
    public static final InnvilgetÅrsak KVOTE_ELLER_OVERFØRT_KVOTE = new InnvilgetÅrsak("2003");
    public static final InnvilgetÅrsak FORELDREPENGER_KUN_FAR_HAR_RETT = new InnvilgetÅrsak("2004");
    public static final InnvilgetÅrsak FORELDREPENGER_ALENEOMSORG = new InnvilgetÅrsak("2005");
    public static final InnvilgetÅrsak FORELDREPENGER_FØR_FØDSEL = new InnvilgetÅrsak("2006");
    public static final InnvilgetÅrsak FORELDREPENGER_KUN_MOR_HAR_RETT = new InnvilgetÅrsak("2007");
    // Utsettelse årsaker
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_FERIE = new InnvilgetÅrsak("2010");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID = new InnvilgetÅrsak("2011");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_INNLEGGELSE = new InnvilgetÅrsak("2012");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_BARN_INNLAGT = new InnvilgetÅrsak("2013");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_SYKDOM = new InnvilgetÅrsak("2014");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT = new InnvilgetÅrsak("2015");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT = new InnvilgetÅrsak("2016");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_SYKDOM_KUN_FAR_HAR_RETT = new InnvilgetÅrsak("2017");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_INNLEGGELSE_KUN_FAR_HAR_RETT = new InnvilgetÅrsak("2018");
    public static final InnvilgetÅrsak UTSETTELSE_GYLDIG_PGA_BARN_INNLAGT_KUN_FAR_HAR_RETT = new InnvilgetÅrsak("2019");
    // Overføring årsaker
    public static final InnvilgetÅrsak OVERFØRING_ANNEN_PART_HAR_IKKE_RETT_TIL_FORELDREPENGER = new InnvilgetÅrsak("2020");
    public static final InnvilgetÅrsak OVERFØRING_ANNEN_PART_SYKDOM_SKADE = new InnvilgetÅrsak("2021");
    public static final InnvilgetÅrsak OVERFØRING_ANNEN_PART_INNLAGT = new InnvilgetÅrsak("2022");
    public static final InnvilgetÅrsak OVERFØRING_SØKER_HAR_ALENEOMSORG_FOR_BARNET = new InnvilgetÅrsak("2023");
    // Gradering årsaker
    public static final InnvilgetÅrsak GRADERING_FELLESPERIODE_ELLER_FORELDREPENGER = new InnvilgetÅrsak("2030");
    public static final InnvilgetÅrsak GRADERING_KVOTE_ELLER_OVERFØRT_KVOTE = new InnvilgetÅrsak("2031");
    public static final InnvilgetÅrsak GRADERING_ALENEOMSORG = new InnvilgetÅrsak("2032");
    public static final InnvilgetÅrsak GRADERING_FORELDREPENGER_KUN_FAR_HAR_RETT = new InnvilgetÅrsak("2033");
    public static final InnvilgetÅrsak GRADERING_FORELDREPENGER_KUN_MOR_HAR_RETT = new InnvilgetÅrsak("2034");

    //TODO Ikke gyldig lenger, skal ikke brukes. Ligger for å støtte historiske behandlinger
    public static final InnvilgetÅrsak UTTAK_OPPFYLT = new InnvilgetÅrsak("2001");


    InnvilgetÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    InnvilgetÅrsak() {
        // For hibernate
    }
}
