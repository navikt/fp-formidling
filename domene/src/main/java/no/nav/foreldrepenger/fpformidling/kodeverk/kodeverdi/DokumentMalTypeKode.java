package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

public class DokumentMalTypeKode {

    private DokumentMalTypeKode() {
    }

    public static final String FRITEKSTBREV = "FRITEK";
    public static final String ENGANGSSTØNAD_INNVILGELSE = "INNVES";
    public static final String ENGANGSSTØNAD_AVSLAG = "AVSLES";
    public static final String FORELDREPENGER_INNVILGELSE = "INVFOR";
    public static final String FORELDREPENGER_AVSLAG = "AVSFOR";
    public static final String FORELDREPENGER_OPPHØR = "OPPFOR";
    public static final String FORELDREPENGER_ANNULLERT = "ANUFOR";
    public static final String FORELDREPENGER_INFO_TIL_ANNEN_FORELDER = "INFOAF";
    public static final String SVANGERSKAPSPENGER_INNVILGELSE = "INVSVP";
    public static final String SVANGERSKAPSPENGER_OPPHØR = "OPPSVP";
    public static final String SVANGERSKAPSPENGER_AVSLAG = "AVSSVP";
    public static final String INNHENTE_OPPLYSNINGER = "INNOPP";
    public static final String VARSEL_OM_REVURDERING = "VARREV";
    public static final String INFO_OM_HENLEGGELSE = "IOHENL";
    public static final String INNSYN_SVAR = "INNSYN";
    public static final String IKKE_SØKT = "IKKESO";
    public static final String INGEN_ENDRING = "INGEND";
    public static final String FORLENGET_SAKSBEHANDLINGSTID = "FORSAK";
    public static final String FORLENGET_SAKSBEHANDLINGSTID_MEDL = "FORMED";
    public static final String FORLENGET_SAKSBEHANDLINGSTID_TIDLIG = "FORTID";
    public static final String KLAGE_AVVIST = "KGEAVV";
    public static final String KLAGE_HJEMSENDT = "KGEHJE";
    public static final String KLAGE_OMGJORT = "KGEOMG";
    public static final String KLAGE_OVERSENDT = "KGEOVE";
    public static final String KLAGE_STADFESTET = "KGESTA";
    public static final String ETTERLYS_INNTEKTSMELDING = "ELYSIM";

    // Disse brevene er utgåtte, men beholdes her grunnet historisk bruk i databasen:
    @Deprecated(forRemoval = true)
    public static final String FRITEKSTBREV_DOK = "FRITKS"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String ENGANGSSTØNAD_INNVILGELSE_DOK = "POSVED"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String ENGANGSSTØNAD_AVSLAG_DOK = "AVSLAG"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String FORELDREPENGER_INNVILGELSE_DOK = "INNVFP"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String FORELDREPENGER_AVSLAG_DOK = "AVSLFP"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String FORELDREPENGER_OPPHØR_DOK = "OPPHOR"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER_DOK = "INAFOR"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String SVANGERSKAPSPENGER_INNVILGELSE_FRITEKST = "INNSVP"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String INNHENTE_OPPLYSNINGER_DOK = "INNHEN"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String VARSEL_OM_REVURDERING_DOK = "REVURD"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String INFO_OM_HENLEGGELSE_DOK = "HENLEG"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String INNSYN_SVAR_DOK = "INSSKR"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String IKKE_SØKT_DOK = "INNTID"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String INGEN_ENDRING_DOK = "UENDRE"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String FORLENGET_SAKSBEHANDLINGSTID_DOK = "FORLEN"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK = "FORLME"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String FORLENGET_SAKSBEHANDLINGSTID_TIDLIG_DOK = "FORLTS"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_AVVIST_DOK = "KLAGAV"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_AVVIST_FRITEKST = "KAVVIS"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_HJEMSENDT_DOK = "KLAGNY"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_HJEMSENDT_FRITEKST = "KHJEMS"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_OMGJORT_DOK = "VEDMED"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_OMGJORT_FRITEKST = "KOMGJO"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_OVERSENDT_DOK = "KLAGOV"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_OVERSENDT_FRITEKST = "KOVKLA"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_STADFESTET_DOK = "KLAGVE"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String KLAGE_STADFESTET_FRITEKST = "KSTADF"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String ANKE_OMGJORT_FRITEKST = "VEDOGA"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String ANKE_OPPHEVET_FRITEKST = "ANKEBO"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String ETTERLYS_INNTEKTSMELDING_FRITEKST = "INNLYS"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String ANKE_OMGJORT = "ANKOMG"; //NOSONAR
    @Deprecated(forRemoval = true)
    public static final String ANKE_OPPHEVET = "ANKOPP"; //NOSONAR

}
