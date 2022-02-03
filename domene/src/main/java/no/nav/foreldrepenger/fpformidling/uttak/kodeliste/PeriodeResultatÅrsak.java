package no.nav.foreldrepenger.fpformidling.uttak.kodeliste;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import no.nav.foreldrepenger.fpformidling.behandling.ÅrsakMedLovReferanse;

public class PeriodeResultatÅrsak implements ÅrsakMedLovReferanse {

    // Her kommer Lovhjemler fra UttakResultatPeriodeDto - søk på bruk av CTOR - lar derfor være å lage full enum her inntil uttak i fpsak enda mer stabilt.
    public static final String GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR = "GRADERING_AVSLAG_AARSAK";
    public static final String PERIODE_ÅRSAK_DISCRIMINATOR = "PERIODE_UTFALL_AARSAK";

    // UKJENT
    public static final PeriodeResultatÅrsak UKJENT = new PeriodeResultatÅrsak("-", "UKJENT", null);

    // GRADERING avslått
    public static final PeriodeResultatÅrsak FOR_SEN_SØKNAD = new PeriodeResultatÅrsak("4501", GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR, null);

    // UTTAK periode innvilget
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID = new PeriodeResultatÅrsak("2011", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_FERIE = new PeriodeResultatÅrsak("2010", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT = new PeriodeResultatÅrsak("2016", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT = new PeriodeResultatÅrsak("2015", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT= new PeriodeResultatÅrsak("2004", PERIODE_ÅRSAK_DISCRIMINATOR, null);

    // UTTAK periode avslått
    public static final PeriodeResultatÅrsak ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT = new PeriodeResultatÅrsak("4023", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak FAR_HAR_IKKE_OMSORG = new PeriodeResultatÅrsak("4012", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak HULL_MELLOM_FORELDRENES_PERIODER = new PeriodeResultatÅrsak("4005", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak MOR_HAR_IKKE_OMSORG = new PeriodeResultatÅrsak("4003", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak BARNET_ER_DØD = new PeriodeResultatÅrsak("4072", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak SØKER_ER_DØD = new PeriodeResultatÅrsak("4071", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak OPPHØR_MEDLEMSKAP = new PeriodeResultatÅrsak("4087", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak MOR_TAR_IKKE_ALLE_UKENE = new PeriodeResultatÅrsak("4095", PERIODE_ÅRSAK_DISCRIMINATOR, null);

    public static final PeriodeResultatÅrsak FØDSELSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4096", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak ADOPSJONSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4097", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak FORELDREANSVARSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4098", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak OPPTJENINGSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4099", PERIODE_ÅRSAK_DISCRIMINATOR, null);
    public static final PeriodeResultatÅrsak AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD = new PeriodeResultatÅrsak("4080", PERIODE_ÅRSAK_DISCRIMINATOR, null);

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
