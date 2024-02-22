package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum DokumentMalType implements Kodeverdi {

    FRITEKSTBREV("FRITEK", "Fritekstbrev"),
    ENGANGSSTØNAD_INNVILGELSE("INNVES", "Vedtak om innvilgelse av engangsstønad"),
    ENGANGSSTØNAD_AVSLAG("AVSLES", "Avslag engangsstønad"),
    FORELDREPENGER_INNVILGELSE("INVFOR", "Innvilgelsesbrev Foreldrepenger"),
    FORELDREPENGER_AVSLAG("AVSFOR", "Avslagsbrev Foreldrepenger"),
    FORELDREPENGER_OPPHØR("OPPFOR", "Opphør Foreldrepenger"),
    FORELDREPENGER_ANNULLERT("ANUFOR", "Annullering av Foreldrepenger"),
    FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER("INFOAF", "Informasjonsbrev til den andre forelderen"),
    FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_INFOBREV("INFOPU", "Informasjonsbrev feil praksis utsettelse"),
    SVANGERSKAPSPENGER_INNVILGELSE("INVSVP", "Innvilgelsesbrev svangerskapspenger"),
    SVANGERSKAPSPENGER_OPPHØR("OPPSVP", "Opphørsbrev svangerskapspenger"),
    SVANGERSKAPSPENGER_AVSLAG("AVSSVP", "Avslagsbrev svangerskapspenger"),
    INNHENTE_OPPLYSNINGER("INNOPP", "Innhent dokumentasjon"),
    VARSEL_OM_REVURDERING("VARREV", "Varsel om revurdering"),
    INFO_OM_HENLEGGELSE("IOHENL", "Behandling henlagt"),
    INNSYN_SVAR("INNSYN", "Svar på innsynskrav"),
    IKKE_SØKT("IKKESO", "Ikke mottatt søknad"),
    INGEN_ENDRING("INGEND", "Uendret utfall"),
    FORLENGET_SAKSBEHANDLINGSTID("FORSAK", "Forlenget saksbehandlingstid"),
    FORLENGET_SAKSBEHANDLINGSTID_MEDL("FORMED", "Forlenget saksbehandlingstid - medlemskap"),
    FORLENGET_SAKSBEHANDLINGSTID_TIDLIG("FORTID", "Forlenget saksbehandlingstid - Tidlig søknad"),
    KLAGE_AVVIST("KGEAVV", Constants.VEDTAK_OM_AVVIST_KLAGE),
    KLAGE_OMGJORT("KGEOMG", "Vedtak om omgjøring av klage"),
    KLAGE_OVERSENDT("KGEOVE", "Klage oversendt til klageinstans"),
    ETTERLYS_INNTEKTSMELDING("ELYSIM", "Etterlys inntektsmelding"),
    ENDRING_UTBETALING("ENDUTB", "Endring i fordeling av ytelsen"), // Denne er kun teknisk når sbh overstyrer vedtaksbrev pga endring i utbetaling av ytelse. Dette skal på sikt bli en egen mal

    // Disse brevene er utgåtte, men beholdes her grunnet historisk bruk i databasen:
    @Deprecated FRITEKSTBREV_DOK("FRITKS", "Fritekstbrev"), //NOSONAR
    @Deprecated ENGANGSSTØNAD_INNVILGELSE_DOK("POSVED", "Positivt vedtaksbrev"), //NOSONAR
    @Deprecated ENGANGSSTØNAD_AVSLAG_DOK("AVSLAG", "Avslagsbrev"), //NOSONAR
    @Deprecated FORELDREPENGER_INNVILGELSE_DOK("INNVFP", "Innvilgelsesbrev Foreldrepenger"), //NOSONAR
    @Deprecated FORELDREPENGER_AVSLAG_DOK("AVSLFP", "Avslagsbrev Foreldrepenger"), //NOSONAR
    @Deprecated FORELDREPENGER_OPPHØR_DOK("OPPHOR", "Opphør brev"), //NOSONAR
    @Deprecated FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER_DOK("INAFOR", //NOSONAR
        "Informasjonsbrev til den andre forelderen"), //NOSONAR
    @Deprecated SVANGERSKAPSPENGER_INNVILGELSE_FRITEKST("INNSVP", //NOSONAR
        "Innvilgelsesbrev svangerskapspenger"), //NOSONAR
    @Deprecated INNHENTE_OPPLYSNINGER_DOK("INNHEN", "Innhent dokumentasjon"), //NOSONAR
    @Deprecated VARSEL_OM_REVURDERING_DOK("REVURD", "Varsel om revurdering"), //NOSONAR
    @Deprecated INFO_OM_HENLEGGELSE_DOK("HENLEG", "Behandling henlagt"), //NOSONAR
    @Deprecated INNSYN_SVAR_DOK("INSSKR", "Svar på innsynskrav"), //NOSONAR
    @Deprecated IKKE_SØKT_DOK("INNTID", "Ikke mottatt søknad"), //NOSONAR
    @Deprecated INGEN_ENDRING_DOK("UENDRE", "Uendret utfall"), //NOSONAR
    @Deprecated FORLENGET_SAKSBEHANDLINGSTID_DOK("FORLEN", "Forlenget saksbehandlingstid"), //NOSONAR
    @Deprecated FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK("FORLME", //NOSONAR
        "Forlenget saksbehandlingstid - medlemskap"), //NOSONAR
    @Deprecated FORLENGET_SAKSBEHANDLINGSTID_TIDLIG_DOK("FORLTS", //NOSONAR
        "Forlenget saksbehandlingstid - Tidlig søknad"), //NOSONAR
    @Deprecated KLAGE_AVVIST_DOK("KLAGAV", Constants.VEDTAK_OM_AVVIST_KLAGE), //NOSONAR
    @Deprecated KLAGE_AVVIST_FRITEKST("KAVVIS", Constants.VEDTAK_OM_AVVIST_KLAGE), //NOSONAR
    @Deprecated KLAGE_HJEMSENDT_DOK("KLAGNY", "Vedtak opphevet, sendt til ny behandling"), //NOSONAR
    @Deprecated KLAGE_HJEMSENDT_FRITEKST("KHJEMS", "Klage hjemsendt/opphevet"), //NOSONAR
    @Deprecated KLAGE_OMGJORT_DOK("VEDMED", "Vedtak om medhold"), //NOSONAR
    @Deprecated KLAGE_OMGJORT_FRITEKST("KOMGJO", "Vedtak om omgjøring av klage"), //NOSONAR
    @Deprecated KLAGE_OVERSENDT_DOK("KLAGOV", "Overføring til NAV Klageinstans"), //NOSONAR
    @Deprecated KLAGE_OVERSENDT_FRITEKST("KOVKLA", "Klage oversendt til klageinstans"), //NOSONAR
    @Deprecated KLAGE_STADFESTET_DOK("KLAGVE", "Vedtak om stadfestelse"), //NOSONAR
    @Deprecated KLAGE_STADFESTET_FRITEKST("KSTADF", "Vedtak om stadfestelse i klagesak"), //NOSONAR
    @Deprecated ANKE_OMGJORT_FRITEKST("VEDOGA", "Vedtak om omgjøring i ankesak"), //NOSONAR
    @Deprecated ANKE_OPPHEVET_FRITEKST("ANKEBO", "Ankebrev om beslutning om oppheving"), //NOSONAR
    @Deprecated ETTERLYS_INNTEKTSMELDING_FRITEKST("INNLYS", "Etterlys inntektsmelding"), //NOSONAR
    @Deprecated ANKE_OMGJORT("ANKOMG", "Vedtak om omgjøring i ankesak"), //NOSONAR
    @Deprecated ANKE_OPPHEVET("ANKOPP", "Ankebrev om beslutning om oppheving"),  //NOSONAR
    @Deprecated KLAGE_STADFESTET("KGESTA", "Vedtak om stadfestelse i klagesak"), //NOSONAR
    @Deprecated KLAGE_HJEMSENDT("KGEHJE", "Klage hjemsendt/opphevet"), //NOSONAR

    UDEFINERT("-");

    private static final Set<DokumentMalType> VEDTAKSBREV = Set.of(ENGANGSSTØNAD_INNVILGELSE, ENGANGSSTØNAD_AVSLAG, FORELDREPENGER_INNVILGELSE,
        FORELDREPENGER_AVSLAG, FORELDREPENGER_OPPHØR, FORELDREPENGER_ANNULLERT, SVANGERSKAPSPENGER_INNVILGELSE, SVANGERSKAPSPENGER_AVSLAG,
        SVANGERSKAPSPENGER_OPPHØR);

    private static final Set<DokumentMalType> KLAGE_VEDTAKSBREV = Set.of(KLAGE_AVVIST, KLAGE_OMGJORT);


    public static boolean erVedtaksBrev(DokumentMalType brev) {
        return VEDTAKSBREV.contains(brev) || KLAGE_VEDTAKSBREV.contains(brev);
    }

    public static final Set<DokumentMalType> FORLENGET_SAKSBEHANDLINGSTID_BREVMALER = Set.of(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID,
        DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL, DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG);

    private static final Map<String, DokumentMalType> KODER = new LinkedHashMap<>();

    private String kode;
    private String navn;

    DokumentMalType() {
        // for hibernate
    }

    DokumentMalType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public String getNavn() {
        return navn;
    }

    DokumentMalType(String kode) {
        this.kode = kode;
    }

    public static DokumentMalType fraKode(String kode) {
        if (kode == null) {
            return null;
        }
        return Optional.ofNullable(KODER.get(kode)).orElseThrow(() -> new IllegalArgumentException("Ukjent Dokumentmaltype: " + kode));
    }

    @Override
    public String getKode() {
        return kode;
    }

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<DokumentMalType, String> {
        @Override
        public String convertToDatabaseColumn(DokumentMalType attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public DokumentMalType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : fraKode(dbData);
        }
    }

    private static class Constants {
        protected static final String VEDTAK_OM_AVVIST_KLAGE = "Vedtak om avvist klage";
    }
}
