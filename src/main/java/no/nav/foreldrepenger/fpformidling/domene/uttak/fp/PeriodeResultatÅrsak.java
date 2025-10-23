package no.nav.foreldrepenger.fpformidling.domene.uttak.fp;

public class PeriodeResultatÅrsak {

    // Her kommer Lovhjemler fra UttakResultatPeriodeDto - søk på bruk av CTOR - lar derfor være å lage full enum her inntil uttak i fpsak enda mer stabilt.
    public static final String GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR = "GRADERING_AVSLAG_AARSAK";
    public static final String PERIODE_ÅRSAK_DISCRIMINATOR = "PERIODE_UTFALL_AARSAK";

    // UKJENT
    public static final PeriodeResultatÅrsak UKJENT = new PeriodeResultatÅrsak("-", "UKJENT");

    // GRADERING avslått
    public static final PeriodeResultatÅrsak FOR_SEN_SØKNAD = new PeriodeResultatÅrsak("4501", GRADERING_AVSLAG_ÅRSAK_DISCRIMINATOR);

    // UTTAK periode innvilget
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID = new PeriodeResultatÅrsak("2011");
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_FERIE = new PeriodeResultatÅrsak("2010");
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT = new PeriodeResultatÅrsak("2016");
    public static final PeriodeResultatÅrsak UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT = new PeriodeResultatÅrsak("2015");
    public static final PeriodeResultatÅrsak OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT = new PeriodeResultatÅrsak("2004");

    // UTTAK periode avslått
    public static final PeriodeResultatÅrsak ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT = new PeriodeResultatÅrsak("4023");
    public static final PeriodeResultatÅrsak FAR_HAR_IKKE_OMSORG = new PeriodeResultatÅrsak("4012");
    public static final PeriodeResultatÅrsak HULL_MELLOM_FORELDRENES_PERIODER = new PeriodeResultatÅrsak("4005");
    public static final PeriodeResultatÅrsak MOR_HAR_IKKE_OMSORG = new PeriodeResultatÅrsak("4003");
    public static final PeriodeResultatÅrsak BARNET_ER_DØD = new PeriodeResultatÅrsak("4072");
    public static final PeriodeResultatÅrsak MOR_TAR_IKKE_ALLE_UKENE = new PeriodeResultatÅrsak("4095");

    public static final PeriodeResultatÅrsak FØDSELSVILKÅRET_IKKE_OPPFYLT = new PeriodeResultatÅrsak("4096");
    public static final PeriodeResultatÅrsak AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD = new PeriodeResultatÅrsak("4080");
    public static final PeriodeResultatÅrsak BARN_OVER_3_ÅR = new PeriodeResultatÅrsak("4022");
    public static final PeriodeResultatÅrsak BARE_FAR_RETT_IKKE_SØKT = new PeriodeResultatÅrsak("4102");

    public static final PeriodeResultatÅrsak SØKNADSFRIST = new PeriodeResultatÅrsak("4020");

    private final String kodeverk;
    private final String kode;

    public PeriodeResultatÅrsak(String kode, String kodeverk) {
        this.kodeverk = kodeverk;
        this.kode = kode;
    }

    public PeriodeResultatÅrsak(String kode) {
        this(kode, PERIODE_ÅRSAK_DISCRIMINATOR);
    }

    public String getKodeverk() {
        return kodeverk;
    }

    public String getKode() {
        return kode;
    }
}
