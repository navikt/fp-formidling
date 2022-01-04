package no.nav.foreldrepenger.melding.uttak.kodeliste;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;

public class PeriodeResultatÅrsak implements ÅrsakMedLovReferanse {

    // Her kommer Lovhjemler fra UttakResultatPeriodeDto - søk på bruk av CTOR - lar derfor være å lage full enum her inntil uttak i fpsak enda mer stabilt.

    public static final PeriodeResultatÅrsak UKJENT = new PeriodeResultatÅrsak("-", "UKJENT", null);
    private static final String GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR = "GRADERING_AVSLAG_AARSAK";
    public static final PeriodeResultatÅrsak FOR_SEN_SØKNAD = new PeriodeResultatÅrsak("4501", GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR, null);
    private static final String INNVILGET_ÅRSAK_DISCRIMINATOR = "INNVILGET_AARSAK";
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID = new PeriodeResultatÅrsak("2011", INNVILGET_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_FERIE = new PeriodeResultatÅrsak("2010", INNVILGET_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT = new PeriodeResultatÅrsak("2016", INNVILGET_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT = new PeriodeResultatÅrsak("2015", INNVILGET_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT= new PeriodeResultatÅrsak("2004", INNVILGET_ÅRSAK_DISCRIMINATOR, null);
    private static final String IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR = "IKKE_OPPFYLT_AARSAK";
    // Uttak årsaker
    public static final PeriodeResultatÅrsak ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT = new PeriodeResultatÅrsak("4023", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak FAR_HAR_IKKE_OMSORG = new PeriodeResultatÅrsak("4012", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak HULL_MELLOM_FORELDRENES_PERIODER = new PeriodeResultatÅrsak("4005", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak MOR_HAR_IKKE_OMSORG = new PeriodeResultatÅrsak("4003", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak BARNET_ER_DØD = new PeriodeResultatÅrsak("4072", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak SØKER_ER_DØD = new PeriodeResultatÅrsak("4071", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak OPPHØR_MEDLEMSKAP = new PeriodeResultatÅrsak("4087", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak MOR_TAR_IKKE_ALLE_UKENE = new PeriodeResultatÅrsak("4095", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);

    public static final PeriodeResultatÅrsak FØDSELSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4096", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak ADOPSJONSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4097", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak FORELDREANSVARSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4098", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak OPPTJENINGSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4099", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD = new PeriodeResultatÅrsak("4080", IKKE_OPPFYLT_ÅRSAK_DISCRIMINATOR, null);

    private String kodeverk;
    private String kode;
    private String ekstraData;

    public PeriodeResultatÅrsak(String kode, String kodeverk, String ekstraData) {
        this.kodeverk = kodeverk;
        this.kode = kode;
        this.ekstraData = ekstraData;
    }

    public static Set<String> opphørsAvslagÅrsaker() {
        return new HashSet<>(Arrays.asList(
                MOR_HAR_IKKE_OMSORG.getKode(),
                FAR_HAR_IKKE_OMSORG.getKode(),
                BARNET_ER_DØD.getKode(),
                SØKER_ER_DØD.getKode(),
                OPPHØR_MEDLEMSKAP.getKode(),
                FØDSELSVILKÅRET_IKKE_OPPFYLT.getKode(),
                ADOPSJONSVILKÅRET_IKKE_OPPFYLT.getKode(),
                FORELDREANSVARSVILKÅRET_IKKE_OPPFYLT.getKode(),
                OPPTJENINGSVILKÅRET_IKKE_OPPFYLT.getKode()
        ));
    }

    public String getKodeverk() {
        return kodeverk;
    }

    public String getKode() {
        return kode;
    }

    public String getEkstraData() {
        return ekstraData;
    }

    @Override
    public String getLovHjemmelData() {
        return getEkstraData();
    }

    public boolean erUkjent() {
        return PeriodeResultatÅrsak.UKJENT.getKode().equals(kode);
    }

    public boolean erGraderingAvslagÅrsak() {
        return GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR.equalsIgnoreCase(kodeverk);
    }

}
