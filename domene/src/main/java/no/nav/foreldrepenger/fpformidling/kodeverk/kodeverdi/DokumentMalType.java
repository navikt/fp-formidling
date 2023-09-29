package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum DokumentMalType implements Kodeverdi {

    FRITEKSTBREV(DokumentMalTypeKode.FRITEKSTBREV, "Fritekstbrev"),
    ENGANGSSTØNAD_INNVILGELSE(DokumentMalTypeKode.ENGANGSSTØNAD_INNVILGELSE, "Vedtak om innvilgelse av engangsstønad"),
    ENGANGSSTØNAD_AVSLAG(DokumentMalTypeKode.ENGANGSSTØNAD_AVSLAG, "Avslag engangsstønad"),
    FORELDREPENGER_INNVILGELSE(DokumentMalTypeKode.FORELDREPENGER_INNVILGELSE, "Innvilgelsesbrev Foreldrepenger"),
    FORELDREPENGER_AVSLAG(DokumentMalTypeKode.FORELDREPENGER_AVSLAG, "Avslagsbrev Foreldrepenger"),
    FORELDREPENGER_OPPHØR(DokumentMalTypeKode.FORELDREPENGER_OPPHØR, "Opphør Foreldrepenger"),
    FORELDREPENGER_ANNULLERT(DokumentMalTypeKode.FORELDREPENGER_ANNULLERT, "Annullering av Foreldrepenger"),
    FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER(DokumentMalTypeKode.FORELDREPENGER_INFO_TIL_ANNEN_FORELDER,
        "Informasjonsbrev til den andre forelderen"),
    SVANGERSKAPSPENGER_INNVILGELSE(DokumentMalTypeKode.SVANGERSKAPSPENGER_INNVILGELSE, "Innvilgelsesbrev svangerskapspenger"),
    SVANGERSKAPSPENGER_OPPHØR(DokumentMalTypeKode.SVANGERSKAPSPENGER_OPPHØR, "Opphørsbrev svangerskapspenger"),
    SVANGERSKAPSPENGER_AVSLAG(DokumentMalTypeKode.SVANGERSKAPSPENGER_AVSLAG, "Avslagsbrev svangerskapspenger"),
    INNHENTE_OPPLYSNINGER(DokumentMalTypeKode.INNHENTE_OPPLYSNINGER, "Innhent dokumentasjon"),
    VARSEL_OM_REVURDERING(DokumentMalTypeKode.VARSEL_OM_REVURDERING, "Varsel om revurdering"),
    INFO_OM_HENLEGGELSE(DokumentMalTypeKode.INFO_OM_HENLEGGELSE, "Behandling henlagt"),
    INNSYN_SVAR(DokumentMalTypeKode.INNSYN_SVAR, "Svar på innsynskrav"),
    IKKE_SØKT(DokumentMalTypeKode.IKKE_SØKT, "Ikke mottatt søknad"),
    INGEN_ENDRING(DokumentMalTypeKode.INGEN_ENDRING, "Uendret utfall"),
    FORLENGET_SAKSBEHANDLINGSTID(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID, "Forlenget saksbehandlingstid"),
    FORLENGET_SAKSBEHANDLINGSTID_MEDL(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_MEDL, "Forlenget saksbehandlingstid - medlemskap"),
    FORLENGET_SAKSBEHANDLINGSTID_TIDLIG(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG, "Forlenget saksbehandlingstid - Tidlig søknad"),
    KLAGE_AVVIST(DokumentMalTypeKode.KLAGE_AVVIST, Constants.VEDTAK_OM_AVVIST_KLAGE),
    KLAGE_OMGJORT(DokumentMalTypeKode.KLAGE_OMGJORT, "Vedtak om omgjøring av klage"),
    KLAGE_OVERSENDT(DokumentMalTypeKode.KLAGE_OVERSENDT, "Klage oversendt til klageinstans"),
    ETTERLYS_INNTEKTSMELDING(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING, "Etterlys inntektsmelding"),
    ENDRING_UTBETALING(DokumentMalTypeKode.ENDRING_UTBETALING, "Endring av utbetaling av ytelse"),

    // Disse brevene er utgåtte, men beholdes her grunnet historisk bruk i databasen:
    @Deprecated FRITEKSTBREV_DOK(DokumentMalTypeKode.FRITEKSTBREV_DOK, "Fritekstbrev"), //NOSONAR
    @Deprecated ENGANGSSTØNAD_INNVILGELSE_DOK(DokumentMalTypeKode.ENGANGSSTØNAD_INNVILGELSE_DOK, "Positivt vedtaksbrev"), //NOSONAR
    @Deprecated ENGANGSSTØNAD_AVSLAG_DOK(DokumentMalTypeKode.ENGANGSSTØNAD_AVSLAG_DOK, "Avslagsbrev"), //NOSONAR
    @Deprecated FORELDREPENGER_INNVILGELSE_DOK(DokumentMalTypeKode.FORELDREPENGER_INNVILGELSE_DOK, "Innvilgelsesbrev Foreldrepenger"), //NOSONAR
    @Deprecated FORELDREPENGER_AVSLAG_DOK(DokumentMalTypeKode.FORELDREPENGER_AVSLAG_DOK, "Avslagsbrev Foreldrepenger"), //NOSONAR
    @Deprecated FORELDREPENGER_OPPHØR_DOK(DokumentMalTypeKode.FORELDREPENGER_OPPHØR_DOK, "Opphør brev"), //NOSONAR
    @Deprecated FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER_DOK(DokumentMalTypeKode.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER_DOK, //NOSONAR
        "Informasjonsbrev til den andre forelderen"), //NOSONAR
    @Deprecated SVANGERSKAPSPENGER_INNVILGELSE_FRITEKST(DokumentMalTypeKode.SVANGERSKAPSPENGER_INNVILGELSE_FRITEKST, //NOSONAR
        "Innvilgelsesbrev svangerskapspenger"), //NOSONAR
    @Deprecated INNHENTE_OPPLYSNINGER_DOK(DokumentMalTypeKode.INNHENTE_OPPLYSNINGER_DOK, "Innhent dokumentasjon"), //NOSONAR
    @Deprecated VARSEL_OM_REVURDERING_DOK(DokumentMalTypeKode.VARSEL_OM_REVURDERING_DOK, "Varsel om revurdering"), //NOSONAR
    @Deprecated INFO_OM_HENLEGGELSE_DOK(DokumentMalTypeKode.INFO_OM_HENLEGGELSE_DOK, "Behandling henlagt"), //NOSONAR
    @Deprecated INNSYN_SVAR_DOK(DokumentMalTypeKode.INNSYN_SVAR_DOK, "Svar på innsynskrav"), //NOSONAR
    @Deprecated IKKE_SØKT_DOK(DokumentMalTypeKode.IKKE_SØKT_DOK, "Ikke mottatt søknad"), //NOSONAR
    @Deprecated INGEN_ENDRING_DOK(DokumentMalTypeKode.INGEN_ENDRING_DOK, "Uendret utfall"), //NOSONAR
    @Deprecated FORLENGET_SAKSBEHANDLINGSTID_DOK(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_DOK, "Forlenget saksbehandlingstid"), //NOSONAR
    @Deprecated FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK, //NOSONAR
        "Forlenget saksbehandlingstid - medlemskap"), //NOSONAR
    @Deprecated FORLENGET_SAKSBEHANDLINGSTID_TIDLIG_DOK(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG_DOK, //NOSONAR
        "Forlenget saksbehandlingstid - Tidlig søknad"), //NOSONAR
    @Deprecated KLAGE_AVVIST_DOK(DokumentMalTypeKode.KLAGE_AVVIST_DOK, Constants.VEDTAK_OM_AVVIST_KLAGE), //NOSONAR
    @Deprecated KLAGE_AVVIST_FRITEKST(DokumentMalTypeKode.KLAGE_AVVIST_FRITEKST, Constants.VEDTAK_OM_AVVIST_KLAGE), //NOSONAR
    @Deprecated KLAGE_HJEMSENDT_DOK(DokumentMalTypeKode.KLAGE_HJEMSENDT_DOK, "Vedtak opphevet, sendt til ny behandling"), //NOSONAR
    @Deprecated KLAGE_HJEMSENDT_FRITEKST(DokumentMalTypeKode.KLAGE_HJEMSENDT_FRITEKST, "Klage hjemsendt/opphevet"), //NOSONAR
    @Deprecated KLAGE_OMGJORT_DOK(DokumentMalTypeKode.KLAGE_OMGJORT_DOK, "Vedtak om medhold"), //NOSONAR
    @Deprecated KLAGE_OMGJORT_FRITEKST(DokumentMalTypeKode.KLAGE_OMGJORT_FRITEKST, "Vedtak om omgjøring av klage"), //NOSONAR
    @Deprecated KLAGE_OVERSENDT_DOK(DokumentMalTypeKode.KLAGE_OVERSENDT_DOK, "Overføring til NAV Klageinstans"), //NOSONAR
    @Deprecated KLAGE_OVERSENDT_FRITEKST(DokumentMalTypeKode.KLAGE_OVERSENDT_FRITEKST, "Klage oversendt til klageinstans"), //NOSONAR
    @Deprecated KLAGE_STADFESTET_DOK(DokumentMalTypeKode.KLAGE_STADFESTET_DOK, "Vedtak om stadfestelse"), //NOSONAR
    @Deprecated KLAGE_STADFESTET_FRITEKST(DokumentMalTypeKode.KLAGE_STADFESTET_FRITEKST, "Vedtak om stadfestelse i klagesak"), //NOSONAR
    @Deprecated ANKE_OMGJORT_FRITEKST(DokumentMalTypeKode.ANKE_OMGJORT_FRITEKST, "Vedtak om omgjøring i ankesak"), //NOSONAR
    @Deprecated ANKE_OPPHEVET_FRITEKST(DokumentMalTypeKode.ANKE_OPPHEVET_FRITEKST, "Ankebrev om beslutning om oppheving"), //NOSONAR
    @Deprecated ETTERLYS_INNTEKTSMELDING_FRITEKST(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING_FRITEKST, "Etterlys inntektsmelding"), //NOSONAR
    @Deprecated ANKE_OMGJORT(DokumentMalTypeKode.ANKE_OMGJORT, "Vedtak om omgjøring i ankesak"), //NOSONAR
    @Deprecated ANKE_OPPHEVET(DokumentMalTypeKode.ANKE_OPPHEVET, "Ankebrev om beslutning om oppheving"),  //NOSONAR
    @Deprecated KLAGE_STADFESTET(DokumentMalTypeKode.KLAGE_STADFESTET, "Vedtak om stadfestelse i klagesak"), //NOSONAR
    @Deprecated KLAGE_HJEMSENDT(DokumentMalTypeKode.KLAGE_HJEMSENDT, "Klage hjemsendt/opphevet"), //NOSONAR

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
