package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

public class DokumentMalTypeKode {

    //Mal hos team dokument
    public static final String INNHENT_DOK = "INNHEN";
    public static final String HENLEGG_BEHANDLING_DOK = "HENLEG";
    public static final String INNVILGELSE_FORELDREPENGER_DOK = "INNVFP";
    public static final String OPPHØR_DOK = "OPPHOR";
    public static final String AVSLAG_FORELDREPENGER_DOK = "AVSLFP";

    //Fritekstbrev - tekst unntatt header og footer genereres av fpformidling
    public static final String FRITEKST_DOK = "FRITKS";
    public static final String ETTERLYS_INNTEKTSMELDING_DOK = "INNLYS";
    public static final String INNVILGELSE_SVANGERSKAPSPENGER_DOK = "INNSVP";
    public static final String ANKEBREV_BESLUTNING_OM_OPPHEVING = "ANKEBO";
    public static final String ANKE_VEDTAK_OMGJORING_DOK = "VEDOGA";
    public static final String KLAGE_STADFESTET = "KSTADF";
    public static final String KLAGE_AVVIST = "KAVVIS";
    public static final String KLAGE_OMGJØRING = "KOMGJO";
    public static final String KLAGE_OVERSENDT_KLAGEINSTANS = "KOVKLA";
    public static final String KLAGE_HJEMSENDT = "KHJEMS";

    //Dokgen
    public static final String INNVILGELSE_ENGANGSSTØNAD = "INNVES";
    public static final String INNHENTE_OPPLYSNINGER = "INNOPP";
    public static final String AVSLAG_ENGANGSSTØNAD = "AVSLES";
    public static final String VARSEL_OM_REVURDERING = "VARREV";
    public static final String INFO_OM_HENLEGGELSE = "IOHENL";
    public static final String INNSYN_SVAR = "INNSYN";
    public static final String IKKE_SØKT = "IKKESO";
    public static final String INGEN_ENDRING = "INGEND";
    public static final String INFO_TIL_ANNEN_FORELDER ="INFOAF";
    public static final String FORLENGET_SAKSBEHANDLINGSTID = "FORSAK";
    public static final String FORLENGET_SAKSBEHANDLINGSTID_MEDL = "FORMED";
    public static final String FORLENGET_SAKSBEHANDLINGSTID_TIDLIG = "FORTID";

    // Disse brevene er utgåtte, men beholdes her grunnet historisk bruk i databasen:
    @Deprecated
    public static final String KLAGE_OVERSENDT_KLAGEINSTANS_DOK = "KLAGOV"; //NOSONAR
    @Deprecated
    public static final String KLAGE_AVVIST_DOK = "KLAGAV"; //NOSONAR
    @Deprecated
    public static final String KLAGE_YTELSESVEDTAK_OPPHEVET_DOK = "KLAGNY"; //NOSONAR
    @Deprecated
    public static final String VEDTAK_MEDHOLD = "VEDMED"; //NOSONAR
    @Deprecated
    public static final String KLAGE_YTELSESVEDTAK_STADFESTET_DOK = "KLAGVE"; //NOSONAR
    @Deprecated
    public static final String POSITIVT_VEDTAK_DOK = "POSVED"; //NOSONAR
    @Deprecated
    public static final String INNTEKTSMELDING_FOR_TIDLIG_DOK = "INNTID"; //NOSONAR
    @Deprecated
    public static final String AVSLAGSVEDTAK_DOK = "AVSLAG"; //NOSONAR
    @Deprecated
    public static final String REVURDERING_DOK = "REVURD"; //NOSONAR
    @Deprecated
    public static final String UENDRETUTFALL_DOK = "UENDRE"; //NOSONAR
    @Deprecated
    public static final String INFO_TIL_ANNEN_FORELDER_DOK = "INAFOR"; //NOSONAR
    @Deprecated
    public static final String INNSYNSKRAV_SVAR = "INSSKR"; //NOSONAR
    @Deprecated
    public static final String FORLENGET_DOK = "FORLEN"; //NOSONAR
    @Deprecated
    public static final String FORLENGET_MEDL_DOK = "FORLME"; //NOSONAR
    @Deprecated
    public static final String FORLENGET_TIDLIG_SOK = "FORLTS"; //NOSONAR
}
